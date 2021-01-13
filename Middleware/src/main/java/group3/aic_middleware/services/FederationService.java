package group3.aic_middleware.services;

import group3.aic_middleware.exceptions.EventNotCreatedException;
import group3.aic_middleware.exceptions.EventNotFoundException;
import group3.aic_middleware.exceptions.EventNotUpdatedException;
import group3.aic_middleware.restData.*;
import group3.aic_middleware.entities.MetaDataEntity;
import lombok.extern.log4j.Log4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@Log4j
public class FederationService {

    private HashingService hashingService = new HashingService();
    private ImageFileService imageFileService = new ImageFileService();
    private RecoveryService recoveryService = new RecoveryService();

    protected static final String MDSConnection = "http://metadata-service:8080";
    protected static final String IOSConnection = "http://image-object-service:8000";

    public FederationService() throws NoSuchAlgorithmException {
    }

    /*
    * Read operations
    * */
    // TODO delete before final submission
    public ReadDetailsEventDTO testStuff() {
        System.out.println(this.hashingService.getHash("Dnes je pekne ale chladno"));
        return new ReadDetailsEventDTO();
    }

    /**
     * Function reads the details of a sensing event which includes meta data, image and tags
     *
     * @param seqId unique identifier of a sensing event to be read
     *
     * @return DTO that contains details of a sensing event
     */
    public ReadDetailsEventDTO readEvent(String seqId) throws EventNotFoundException {
        String fileName = "";
        String URL_MDS = MDSConnection + "/events/" + seqId;
        RestTemplate restTemplate = new RestTemplate();
        ReadDetailsEventDTO storeEventDTO = new ReadDetailsEventDTO();

        // check existence of an image using MetaDataService
        ResponseEntity<MetaDataServiceDTO> responseMDS = null;
        try {
            responseMDS = restTemplate.exchange(
                    URL_MDS, HttpMethod.GET, null,
                    new ParameterizedTypeReference<MetaDataServiceDTO>() {
                    });
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.info("Requested sensing event doesn't exist.");
                throw new EventNotFoundException("Requested sensing event doesn't exist.");
            }
        }

        MetaDataServiceDTO metaDataDTO = responseMDS.getBody();

        fileName = metaDataDTO.getSensingEventId() + "_base.jpg";

        storeEventDTO.setMetadata(convertMetaDataServiceToMetaDataDetail(metaDataDTO));
        storeEventDTO.setImage(this.recoveryService.recoverImage(fileName, this.recoveryService.getHashValue(metaDataDTO, "base")));
        storeEventDTO.setTags(convertTagDtoToEventDetailTagDto(metaDataDTO.getTags()));

        return storeEventDTO;
    }

    /**
     * Function reads the details of a tag of a sensing event which includes tag name, tag image and creation datetime
     *
     * @param seqId unique identifier of a sensing event
     * @param tagName identifier of a tag to be read for a given sensing event
     *
     * @return DTO that contains tag data
     */
    public TagDataDTO readTagData(String seqId, String tagName) throws EventNotFoundException {
        TagDataDTO tagDataDTO = new TagDataDTO();
        String fileName = seqId + "_" + tagName + ".jpg";
        String URL_MDS = MDSConnection + "/events/" + seqId;
        RestTemplate restTemplate = new RestTemplate();
        MetaDataEntity metaDataEntity = new MetaDataEntity();

        // check existence of an image using MetaDataService
        ResponseEntity<MetaDataServiceDTO> responseMDS = null;
        try {
            responseMDS = restTemplate.exchange(
                    URL_MDS, HttpMethod.GET, null,
                    new ParameterizedTypeReference<MetaDataServiceDTO>() {
                    });
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.info("Requested sensing event doesn't exist.");
                throw new EventNotFoundException("Requested sensing event with the given tag doesn't exist.");
            }
        }

        MetaDataServiceDTO metaDataDTO = responseMDS.getBody();
        copyMetaDataFromDTOToEntity(metaDataEntity, metaDataDTO);
        if(!this.checkTagExistence(tagName, metaDataDTO)) {
            log.info("Requested sensing event with the given tag doesn't exist.");
            throw new EventNotFoundException("Requested sensing event with the given tag doesn't exist.");
        }

        tagDataDTO.setTagName(tagName);
        tagDataDTO.setCreated(metaDataDTO.getCreated(tagName));
        tagDataDTO.setImage(this.recoveryService.recoverImage(fileName, this.recoveryService.getHashValue(metaDataDTO, tagName)));

        return tagDataDTO;
    }

    /**
     * Function reads overview information for all events contained in meta data storage
     * for the UI Event table and also provides search capability
     *
     * @param pageable requested page settings from UI
     * @param search search term
     *
     * @return DTO that contains list of events and page information
     */
    public MetadataPageDTO readEvents(Pageable pageable, String search) {
        String URL_MDS = "";
        RestTemplate restTemplate = new RestTemplate();
        MetaDataEntity metaDataEntity = new MetaDataEntity();
        ResponseEntity<MetadataPageDTO> responseMDS = null;

        URL_MDS = MDSConnection + "/events" + createPageString(pageable) + createSearchString(search);

        // query events
        responseMDS = restTemplate.exchange(
                    URL_MDS, HttpMethod.GET, null,
                    new ParameterizedTypeReference<MetadataPageDTO>() {});

        MetadataPageDTO ret = responseMDS.getBody();

        for(ReadEventsDTO readEventsDTO : ret.getEvents()){
            readEventsDTO.setState(this.recoveryService.getEventStatus(readEventsDTO));
        }

        return ret;
    }

    /**
     * Function reads overview information for all events contained in meta data storage for the UI Event map
     *
     * @param size radius of the search space
     * @param longitude coordinate of a center of search space
     * @param latitude coordinate of a center of search space
     *
     * @return DTO that contains list of events
     */
    public List<ReadEventsDTO> readEvents(double size, double longitude, double latitude) {
        ArrayList<ReadEventsDTO> eventList = new ArrayList<>();
        String URL_MDS = "";
        RestTemplate restTemplate = new RestTemplate();
        MetaDataEntity metaDataEntity = new MetaDataEntity();
        ResponseEntity<List<MetaDataServiceDTO>> responseMDS = null;
        if(size >= 0) {
            URL_MDS = MDSConnection + "/events/radius?size="+size+"&lon="+longitude+"&lat="+latitude;
        } else {
            URL_MDS = MDSConnection + "/events";
        }

        // query events
        responseMDS = restTemplate.exchange(
                URL_MDS, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<MetaDataServiceDTO>>() {}
                );

        Iterator<MetaDataServiceDTO> it = responseMDS.getBody().iterator();
        while(it.hasNext()) {
            MetaDataServiceDTO metaDataDTO = it.next();
            copyMetaDataFromDTOToEntity(metaDataEntity, metaDataDTO);

            ReadEventsDTO readEventsDTO = new ReadEventsDTO();
            readEventsDTO.setPlaceIdent(metaDataDTO.getPlaceIdent());
            readEventsDTO.setName(metaDataDTO.getName());
            readEventsDTO.setSeqId(metaDataDTO.getSensingEventId());
            readEventsDTO.setCreated(metaDataDTO.getCreated("base"));
            readEventsDTO.setUpdated(metaDataDTO.getCreated("base"));
            readEventsDTO.setLongitude(metaDataDTO.getLongitude());
            readEventsDTO.setLatitude(metaDataDTO.getLatitude());
            //readEventsDTO.setState(this.recoveryService.getEventStatus(metaDataDTO));
            readEventsDTO.setState("CORRECT");
            readEventsDTO.setTags(convertTagDtoToSimpleTagDto(metaDataDTO.getTags()));
            readEventsDTO.setCreated(metaDataDTO.getTimestamp());
            readEventsDTO.setUpdated(metaDataDTO.getUpdated());
            eventList.add(readEventsDTO);
        }

        return eventList;
    }


    /*
     * Create operations
     * */

    /**
     * Function stores and replicates a given event
     *
     * @param storeEventDTO event to store
     *
     * @return
     */
    public void saveEvent(StoreEventDTO storeEventDTO) throws EventNotCreatedException {
        RestTemplate restTemplate = new RestTemplate();
        String URL_MDS = MDSConnection + "/events/" + storeEventDTO.getMetadata().getSensingEventId();
        String hashOfNewImage = this.hashingService.getHash(storeEventDTO.getImage());
        MetaDataServiceDTO metaDataDTO = null;

        // check existence of an image using MetaDataService and request old hash
        ResponseEntity<MetaDataServiceDTO> responseMDS = null;
        try {
            responseMDS = restTemplate.exchange(
                    URL_MDS, HttpMethod.GET, null,
                    new ParameterizedTypeReference<MetaDataServiceDTO>() {
                    });
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
                metaDataDTO = new MetaDataServiceDTO();
            } else {
                log.error("Status code: " + e.getStatusCode() + "; Message: " + e.getMessage());
                throw new EventNotCreatedException("Status code: " + e.getStatusCode() + "; Message: " + e.getMessage());
            }
        } catch (Exception e) {
            throw new EventNotCreatedException("Reason: " + e.getMessage());
        }

        if(responseMDS != null) {
            metaDataDTO = responseMDS.getBody();
            if(this.hashingService.compareHash(hashOfNewImage, metaDataDTO.getTags().iterator().next().getImageHash())) {
                log.info("Sensing event already exists.");
                throw new EventNotCreatedException("Sensing event already exists.");
            }
        }

        // save metadata using the MetadataService
        URL_MDS = MDSConnection + "/events";
        metaDataDTO =  storeEventDTO.getMetadata();
        ArrayList<TagDTO> tagList = new ArrayList<>();
        tagList.add(new TagDTO("base", hashOfNewImage));
        metaDataDTO.setTags(tagList);
        HttpEntity<MetaDataServiceDTO> request = null;
        request = new HttpEntity<>(metaDataDTO);
        try {
            restTemplate.exchange(URL_MDS, HttpMethod.POST, request, MetaDataServiceDTO.class);
        } catch(HttpClientErrorException e) {
            log.info(e.getMessage());
            throw new EventNotCreatedException(e.getMessage());
        }

        // save image using ImageObjectStorageService
        String fileName = storeEventDTO.getMetadata().getSensingEventId() + "_base.jpg";
        String URL_IOS = IOSConnection + "/images";
        ImageObjectServiceCreateDTO imageObjectServiceCreateDTO = new ImageObjectServiceCreateDTO(fileName, storeEventDTO.getImage());
        HttpEntity<ImageObjectServiceCreateDTO> requestCreate = new HttpEntity<>(imageObjectServiceCreateDTO);
        restTemplate.exchange(URL_IOS, HttpMethod.PUT, requestCreate, Void.class);

        // replicate image using ImageFileService
        this.imageFileService.saveImage(fileName, storeEventDTO.getImage());
    }

    /**
     * Function stores and replicates a new tag for a given event
     *
     * @param tagDataDTO tag data to be stored (tag name + tag image)
     * @param seqId unique identifier of a sensing event
     *
     * @return
     */
    public void createTag(TagDataDTO tagDataDTO, String seqId) throws EventNotUpdatedException, EventNotFoundException {
        String URL_MDS = MDSConnection + "/events/" + seqId;
        RestTemplate restTemplate = new RestTemplate();

        // check existence of an image using MetaDataService
        ResponseEntity<MetaDataServiceDTO> responseMDS = null;
        try {
            responseMDS = restTemplate.exchange(
                    URL_MDS, HttpMethod.GET, null,
                    new ParameterizedTypeReference<MetaDataServiceDTO>() {
                    });
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.info("Requested sensing event doesn't exist.");
                throw new EventNotFoundException("Requested sensing event doesn't exist.");
            }
        }

        // save new tag using the MetaDataService
        URL_MDS = MDSConnection + "/events/" + seqId + "/tags";
        TagDTO tagDTO = new TagDTO(tagDataDTO.getTagName(), this.hashingService.getHash(tagDataDTO.getImage()));
        HttpEntity<TagDTO> request = new HttpEntity<>(tagDTO);
        try {
            ResponseEntity<TagDTO> response = restTemplate.exchange(URL_MDS, HttpMethod.POST, request, TagDTO.class);
        } catch(HttpClientErrorException e) {
            log.info(e.getMessage());
            throw new EventNotUpdatedException(e.getMessage());
        }

        // save image using ImageObjectStorageService
        String fileName = seqId + "_" + tagDataDTO.getTagName() + ".jpg";
        String URL_IOS = IOSConnection + "/images";
        ImageObjectServiceCreateDTO imageObjectServiceCreateDTO = new ImageObjectServiceCreateDTO(fileName, tagDataDTO.getImage());
        HttpEntity<ImageObjectServiceCreateDTO> requestCreate = new HttpEntity<>(imageObjectServiceCreateDTO);
        restTemplate.exchange(URL_IOS, HttpMethod.PUT, requestCreate, Void.class);

        // replicate image using ImageFileService
        try {
            this.imageFileService.saveImage(fileName, tagDataDTO.getImage());
        } catch (EventNotCreatedException e) {
            log.info("Tag could not be saved into Image File Storage.");
            throw new EventNotUpdatedException("Tag could not be saved into Image File Storage: " + e.getMessage());
        }
    }


    /*
     * Update operations
     * */

    /**
     * Function the meta data for a given event
     *
     * @param storeEventDTO event data to be updated
     *
     * @return
     */
    public void updateEvent(StoreEventDTO storeEventDTO) throws EventNotUpdatedException, EventNotFoundException {
        String URL_MDS = MDSConnection + "/events/" + storeEventDTO.getMetadata().getSensingEventId();
        RestTemplate restTemplate = new RestTemplate();
        MetaDataServiceDTO metaDataServiceDTO = null;

        // check existence of an image using MetaDataService
        ResponseEntity<MetaDataServiceDTO> responseMDS = null;
        try {
            responseMDS = restTemplate.exchange(
                    URL_MDS, HttpMethod.GET, null,
                    new ParameterizedTypeReference<MetaDataServiceDTO>() {
                    });
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.info("Requested sensing event doesn't exist.");
                throw new EventNotFoundException("Requested sensing event doesn't exist.");
            }
        }
        metaDataServiceDTO = responseMDS.getBody();

        if(!metaDataServiceDTO.getSensingEventId().equals(storeEventDTO.getMetadata().getSensingEventId())) {
            throw new EventNotUpdatedException("The sequence ID of the event is not identical, wrong update request");
        }

        if(!this.compareMetadataChanged(metaDataServiceDTO, storeEventDTO.getMetadata())) {
            throw new EventNotUpdatedException("The metadata did not change.");
        } else {
            // update the MetaData for an event
            URL_MDS = MDSConnection + "/events";
            metaDataServiceDTO = storeEventDTO.getMetadata();
            HttpEntity<MetaDataServiceDTO> request = new HttpEntity<>(metaDataServiceDTO);
            try {
                ResponseEntity<MetaDataServiceDTO> response = restTemplate.exchange(URL_MDS, HttpMethod.PUT, request, MetaDataServiceDTO.class);
            } catch (HttpClientErrorException e) {
                log.info(e.getMessage());
                throw new EventNotUpdatedException(e.getMessage());
            }
        }
    }



    /*
     * Delete operations
     * */

    /**
     * Function cascade deletes a sensing event from all storage
     *
     * @param seqId unique identifier of a sensing event
     *
     * @return
     */
    public void deleteEvent(String seqId) throws EventNotFoundException {
        RestTemplate restTemplate = new RestTemplate();
        String fileName = "";
        String URL_MDS = MDSConnection + "/events/" + seqId;

        // check existence of an image using MetaDataService
        ResponseEntity<MetaDataServiceDTO> responseMDS = null;
        try {
            responseMDS = restTemplate.exchange(
                    URL_MDS, HttpMethod.GET, null,
                    new ParameterizedTypeReference<MetaDataServiceDTO>() {
                    });
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.info("Requested sensing event doesn't exist.");
                throw new EventNotFoundException("Requested sensing event doesn't exist.");
            }
        }

        // delete metadata using the MetadataService
        restTemplate.delete(URL_MDS);

        // cascade delete images for all tags from both storage
        Iterator<TagDTO> it = responseMDS.getBody().getTags().iterator();
        while(it.hasNext()) {
            TagDTO tagDTO = it.next();
            fileName = seqId + "_" + tagDTO.getTagName() + ".jpg";
            this.deleteImages(fileName);
        }
    }

    /**
     * Function deletes a given tag (tag from meta data storage and images from IOS and IFS) for a given event
     *
     * @param seqId unique identifier of a sensing event
     * @param tagName tag to be deleted
     *
     * @return
     */
    public void deleteTag(String seqId, String tagName) throws EventNotUpdatedException {
        RestTemplate restTemplate = new RestTemplate();
        String fileName = seqId + "_" + tagName + ".jpg";
        String URL_MDS = MDSConnection + "/events/" + seqId + "/tags/" + tagName;

        // delete a tag using the MetadataService
        try {
            restTemplate.delete(URL_MDS);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.info("Sensing event with a given tag doesn't exist.");
                throw new EventNotUpdatedException("Sensing event with a given tag doesn't exist.");
            }
        }
        this.deleteImages(fileName);
    }

    /**
     * Function deletes images from IOS and IFS for a given file name
     *
     * @param fileName image to be deleted
     *
     * @return
     */
    private void deleteImages(String fileName) {
        RestTemplate restTemplate = new RestTemplate();
        String URL_IOS = IOSConnection + "/images/" + fileName;

        // delete primary image using ImageObjectStorageService
        restTemplate.delete(URL_IOS);

        // delete backup image using ImageFileService
        try {
            this.imageFileService.deleteImage(fileName);
        } catch (EventNotFoundException e) {
            log.info("File with the name " + fileName + " could not be found in Image File Storage.");
        }
    }

    /*
     * Support functions
     * */

    /**
     * Function copies the data from given metaDataServiceDTO into metaDataEntity provided
     *
     * @param metaDataEntity entity that should be filled
     * @param metaDataServiceDTO entity that serves as a source
     *
     * @return
     */
    private void copyMetaDataFromDTOToEntity(MetaDataEntity metaDataEntity, MetaDataServiceDTO metaDataServiceDTO) {
        metaDataEntity.setSeqId(metaDataServiceDTO.getSensingEventId());
        metaDataEntity.setName(metaDataServiceDTO.getName());
        metaDataEntity.setDeviceId(metaDataServiceDTO.getDeviceIdentifier());
        metaDataEntity.setDatetime(LocalDateTime.ofInstant(Instant.ofEpochMilli(metaDataServiceDTO.getTimestamp()), ZoneId.systemDefault()));
        metaDataEntity.setLongitude(metaDataServiceDTO.getLongitude());
        metaDataEntity.setLatitude(metaDataServiceDTO.getLatitude());
        metaDataEntity.setFrameNum(metaDataServiceDTO.getFrameNum());
        metaDataEntity.setPlaceIdent(metaDataServiceDTO.getPlaceIdent());
        metaDataEntity.setSeqNumFrames(metaDataServiceDTO.getEventFrames());
        metaDataEntity.setTags(metaDataServiceDTO.getTags());
        metaDataEntity.setFilename(metaDataServiceDTO.getSensingEventId() + ".jpg");
    }

    /**
     * Function copies the data from given metaDataEntity into metaDataServiceDTO provided
     *
     * @param metaDataServiceDTO entity that should be filled
     * @param metaDataEntity entity that serves as a source
     *
     * @return
     */
    public void copyMetaDataFromEntityToDTO(MetaDataServiceDTO metaDataServiceDTO, MetaDataEntity metaDataEntity) {
        metaDataServiceDTO.setSensingEventId(metaDataEntity.getSeqId());
        metaDataServiceDTO.setName(metaDataEntity.getName());
        metaDataServiceDTO.setDeviceIdentifier(metaDataEntity.getDeviceId());
        metaDataServiceDTO.setTimestamp(ZonedDateTime.of(metaDataEntity.getDatetime(), ZoneId.systemDefault()).toInstant().toEpochMilli());
        metaDataServiceDTO.setLongitude(metaDataEntity.getLongitude());
        metaDataServiceDTO.setLatitude(metaDataEntity.getLatitude());
        metaDataServiceDTO.setFrameNum(metaDataEntity.getFrameNum());
        metaDataServiceDTO.setPlaceIdent(metaDataEntity.getPlaceIdent());
        metaDataServiceDTO.setEventFrames(metaDataEntity.getSeqNumFrames());
        metaDataServiceDTO.setTags(metaDataEntity.getTags());
    }

    /**
     * Function transforms list of TagDTOs into EventDetailsTagDTOs
     *
     * @param tags list of TagDTOs to be converted
     *
     * @return list of EventDetailsTagDTOs
     */
    private List<EventDetailsTagDTO> convertTagDtoToEventDetailTagDto(List<TagDTO> tags) {

        ArrayList<EventDetailsTagDTO> ret = new ArrayList<>();
        for (TagDTO tag: tags) {
            ret.add(new EventDetailsTagDTO(tag.getTagName(), tag.getCreated(), tag.getImageHash()));
        }
        return ret;
    }

    /**
     * Function transforms list of TagDTOs into SimpleTagDTOs
     *
     * @param tags list of TagDTOs to be converted
     *
     * @return list of SimpleTagDTOs
     */
    private List<SimpleTagDTO> convertTagDtoToSimpleTagDto(List<TagDTO> tags) {

        ArrayList<SimpleTagDTO> ret = new ArrayList<>();
        for (TagDTO tag: tags) {
            ret.add(new SimpleTagDTO(tag.getTagName(), tag.getImageHash()));
        }
        return ret;
    }

    /**
     * Function transforms a MetaDataServiceDTO into MetaDataDetailsDTO
     *
     * @param metaDataServiceDTO meta data to be converted
     *
     * @return MetaDataDetailsDTO
     */
    private MetaDataDetailsDTO convertMetaDataServiceToMetaDataDetail(MetaDataServiceDTO metaDataServiceDTO) {

        MetaDataDetailsDTO ret = new MetaDataDetailsDTO();
        ret.setSensingEventId(metaDataServiceDTO.getSensingEventId());
        ret.setName(metaDataServiceDTO.getName());
        ret.setDeviceIdentifier(metaDataServiceDTO.getDeviceIdentifier());
        ret.setTimestamp(metaDataServiceDTO.getTimestamp());
        ret.setLongitude(metaDataServiceDTO.getLongitude());
        ret.setLatitude(metaDataServiceDTO.getLatitude());
        ret.setFrameNum(metaDataServiceDTO.getFrameNum());
        ret.setPlaceIdent(metaDataServiceDTO.getPlaceIdent());
        ret.setEventFrames(metaDataServiceDTO.getEventFrames());
        return ret;
    }

    /**
     * Function compares the meta data stored in given metaDataEntity and metaDataServiceDTO
     *
     * @param metaDataServiceDTO entity to be compared
     * @param metaDataEntity entity to be compared
     *
     * @return true if a change occurred on any parameter of meta data, false otherwise
     */
    private boolean compareMetadataChanged(MetaDataServiceDTO metaDataServiceDTO, MetaDataServiceDTO metaDataEntity) {
        if(!metaDataServiceDTO.getName().equals(metaDataEntity.getName())) {
            return true;
        }
        if(!metaDataServiceDTO.getDeviceIdentifier().equals(metaDataEntity.getDeviceIdentifier())) {
            return true;
        }
        if(metaDataServiceDTO.getTimestamp() != (metaDataEntity.getTimestamp())) {
            return true;
        }
        if(metaDataServiceDTO.getLongitude() != (metaDataEntity.getLongitude())) {
            return true;
        }
        if(metaDataServiceDTO.getLatitude() != (metaDataEntity.getLatitude())) {
            return true;
        }
        if(metaDataServiceDTO.getFrameNum() != (metaDataEntity.getFrameNum())) {
            return true;
        }
        if(!metaDataServiceDTO.getPlaceIdent().equals(metaDataEntity.getPlaceIdent())) {
            return true;
        }
        if(metaDataServiceDTO.getEventFrames() != (metaDataEntity.getEventFrames())) {
            return true;
        }
        return false;
    }

    /**
     * Function looks for a tag with given tag name in a given metaDataServiceDTO
     *
     * @param tagName tag we look for
     * @param metaDataServiceDTO entity to be searched in
     *
     * @return true if metaDataServiceDTO contains a given tag, false otherwise
     */
    private boolean checkTagExistence(String tagName, MetaDataServiceDTO metaDataServiceDTO) {
        Iterator<TagDTO> it = metaDataServiceDTO.getTags().iterator();
        while(it.hasNext()) {
            TagDTO tagDTO = it.next();
            if(tagDTO.getTagName().equals(tagName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Function builds a page part of a POST request to the MetadataService using the data sent from the UI
     *
     * @param pageable page data obtained from UI
     *
     * @return page part of POST request for MetadataService
     */
    private String createPageString(Pageable pageable) {
        String ret = "";

        ret += String.format("?page=%d", pageable.getPageNumber());
        ret += String.format("&size=%d", pageable.getPageSize());

        for(Sort.Order ord : pageable.getSort().toList()) {
            ret += String.format("&sort=%s,%s", ord.getProperty(), ord.getDirection().toString());
        }

        return ret;
    }

    /**
     * Function builds a search part of a POST request to the MetadataService using the data sent from the UI
     *
     * @param search search string
     *
     * @return search part of POST request for MetadataService
     */
    private String createSearchString(String search) {

        if(search == null || search.isEmpty())
        {
            return "";
        }

        return "&search=" + search;
    }
}
