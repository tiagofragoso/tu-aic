package group3.aic_middleware.services;

import group3.aic_middleware.exceptions.EventNotCreatedException;
import group3.aic_middleware.exceptions.EventNotFoundException;
import group3.aic_middleware.exceptions.EventNotUpdatedException;
import group3.aic_middleware.restData.*;
import group3.aic_middleware.entities.MetaDataEntity;
import lombok.extern.log4j.Log4j;
import org.springframework.core.ParameterizedTypeReference;
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

    // TODO Transform into environment variables
    String MDSConnection = "http://metadata-service:8080";
    String IOSConnection = "http://image-object-service:8000";

    public FederationService() throws NoSuchAlgorithmException {
    }

    /*
    * Read operations
    * */
    public ReadEventDetailsDTO readEvent(String seqId) throws EventNotFoundException {
        String fileName = "";
        String URL_MDS = MDSConnection + "/events/" + seqId;
        RestTemplate restTemplate = new RestTemplate();
        ReadEventDetailsDTO readEventDetailsDTO = new ReadEventDetailsDTO();
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
                throw new EventNotFoundException("Requested sensing event doesn't exist.");
            }
        }

        MetaDataServiceDTO metaDataDTO = responseMDS.getBody();

        fileName = metaDataDTO.getSensingEventId() + "_" + metaDataEntity.getTags().iterator().next().getTagName() + ".jpg";
        copyMetaDataFromDTOToEntity(metaDataEntity, metaDataDTO);

        readEventDetailsDTO.setMetaData(metaDataEntity);
        readEventDetailsDTO.setImageBase64Enc(this.recoveryService.recoverImage(fileName));

        return readEventDetailsDTO;
    }

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
                throw new EventNotFoundException("Requested sensing event doesn't exist.");
            }
        }

        MetaDataServiceDTO metaDataDTO = responseMDS.getBody();
        copyMetaDataFromDTOToEntity(metaDataEntity, metaDataDTO);

        tagDataDTO.setTagName(tagName);
        tagDataDTO.setCreated(metaDataDTO.getCreated(tagName));
        tagDataDTO.setImage(this.recoveryService.recoverImage(fileName));

        return tagDataDTO;
    }


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
            readEventsDTO.setState(this.recoveryService.getEventStatus(metaDataDTO));
            readEventsDTO.setTags(metaDataDTO.getTags());
            eventList.add(readEventsDTO);
        }

        return eventList;
    }


    /*
     * Create operations
     * */
    public void saveEvent(ReadEventDetailsDTO readEventDetailsDTO) throws EventNotCreatedException {
        RestTemplate restTemplate = new RestTemplate();
        String URL_MDS = MDSConnection + "/events/" + readEventDetailsDTO.getMetaData().getSeqId();
        int hashOfNewImage = this.hashingService.getHash(readEventDetailsDTO.getImageBase64Enc());
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
        copyMetaDataFromEntityToDTO(metaDataDTO, readEventDetailsDTO.getMetaData());
        ArrayList<TagDTO> tagList = new ArrayList<>();
        tagList.add(new TagDTO("base", hashOfNewImage));
        metaDataDTO.setTags(tagList);
        HttpEntity<MetaDataServiceDTO> request = null;
        request = new HttpEntity<>(metaDataDTO);
        try {
            ResponseEntity<MetaDataServiceDTO> response = restTemplate.exchange(URL_MDS, HttpMethod.POST, request, MetaDataServiceDTO.class);
        } catch(HttpClientErrorException e) {
            log.info(e.getMessage());
            throw new EventNotCreatedException(e.getMessage());
        }

        // save image using ImageObjectStorageService
        String fileName = readEventDetailsDTO.getMetaData().getSeqId() + "_base.jpg";
        String URL_IOS = IOSConnection + "/images";
        ImageObjectServiceCreateDTO imageObjectServiceCreateDTO = new ImageObjectServiceCreateDTO(fileName, readEventDetailsDTO.getImageBase64Enc());
        HttpEntity<ImageObjectServiceCreateDTO> requestCreate = new HttpEntity<>(imageObjectServiceCreateDTO);
        restTemplate.exchange(URL_IOS, HttpMethod.PUT, requestCreate, Void.class);

        // replicate image using ImageFileService
        this.imageFileService.saveImage(fileName, readEventDetailsDTO.getImageBase64Enc());
    }

    public void createTag(TagDataDTO tagDataDTO, String seqId) throws EventNotUpdatedException {
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
                throw new EventNotUpdatedException("Requested sensing event doesn't exist.");
            }
        }

        // save new tag using the MetaDataService
        URL_MDS = MDSConnection + "/events/" + seqId + "/tags";
        HttpEntity<TagDataDTO> request = new HttpEntity<>(tagDataDTO);
        try {
            ResponseEntity<TagDataDTO> response = restTemplate.exchange(URL_MDS, HttpMethod.POST, request, TagDataDTO.class);
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
    public void updateEvent(ReadEventDetailsDTO readEventDetailsDTO) throws EventNotUpdatedException {
        String URL_MDS = MDSConnection + "/events/" + readEventDetailsDTO.getMetaData().getSeqId();
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
                throw new EventNotUpdatedException("Requested sensing event doesn't exist.");
            }
        }
        metaDataServiceDTO = responseMDS.getBody();

        if(metaDataServiceDTO.getSensingEventId() != readEventDetailsDTO.getMetaData().getSeqId()) {
            throw new EventNotUpdatedException("The sequence ID of the event is not identical, wrong update request");
        }
        if(!this.compareMetadata(metaDataServiceDTO, readEventDetailsDTO.getMetaData())) {
            throw new EventNotUpdatedException("The metadata did not change.");
        } else {
            // update the MetaData for an event
            URL_MDS = MDSConnection + "/events";
            copyMetaDataFromEntityToDTO(metaDataServiceDTO, readEventDetailsDTO.getMetaData());
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


    public void deleteTag(String seqId, String tagName) throws EventNotFoundException {
        RestTemplate restTemplate = new RestTemplate();
        String fileName = seqId + "_" + tagName + ".jpg";
        String URL_MDS = MDSConnection + "/events/" + seqId + "/tags/" + tagName;

        // delete metadata using the MetadataService
        restTemplate.delete(URL_MDS);
        this.deleteImages(fileName);
    }

    private void deleteImages(String fileName) throws EventNotFoundException {
        RestTemplate restTemplate = new RestTemplate();
        String URL_IOS = IOSConnection + "/images/" + fileName;

        // delete primary image using ImageObjectStorageService
        restTemplate.delete(URL_IOS);

        // delete backup image using ImageFileService
        this.imageFileService.deleteImage(fileName);
    }

    /*
     * Support functions
     * */
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

    private void copyMetaDataFromEntityToDTO(MetaDataServiceDTO metaDataServiceDTO, MetaDataEntity metaDataEntity) {
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

    private boolean compareMetadata(MetaDataServiceDTO metaDataServiceDTO, MetaDataEntity metaDataEntity) {
        if(metaDataServiceDTO.getName() == metaDataEntity.getName()) {
            return true;
        }
        if(metaDataServiceDTO.getDeviceIdentifier() == metaDataEntity.getDeviceId()) {
            return true;
        }
        if(metaDataServiceDTO.getTimestamp() == (ZonedDateTime.of(metaDataEntity.getDatetime(), ZoneId.systemDefault()).toInstant().toEpochMilli())) {
            return true;
        }
        if(metaDataServiceDTO.getLongitude() == metaDataEntity.getLongitude()) {
            return true;
        }
        if(metaDataServiceDTO.getLatitude() == metaDataEntity.getLatitude()) {
            return true;
        }
        if(metaDataServiceDTO.getFrameNum() == metaDataEntity.getFrameNum()) {
            return true;
        }
        if(metaDataServiceDTO.getPlaceIdent() == metaDataEntity.getPlaceIdent()) {
            return true;
        }
        if(metaDataServiceDTO.getEventFrames() == metaDataEntity.getSeqNumFrames()) {
            return true;
        }
        return false;
    }
}
