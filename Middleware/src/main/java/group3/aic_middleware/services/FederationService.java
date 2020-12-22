package group3.aic_middleware.services;

import group3.aic_middleware.exceptions.DropboxLoginException;
import group3.aic_middleware.exceptions.DuplicateEventException;
import group3.aic_middleware.exceptions.EventNotCreatedException;
import group3.aic_middleware.exceptions.EventNotFoundException;
import group3.aic_middleware.entities.ImageEntity;
import group3.aic_middleware.restData.*;
import group3.aic_middleware.entities.MetaDataEntity;
import lombok.extern.log4j.Log4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
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

    public ReadEventDTO readEvent(String seqId) throws EventNotFoundException {
        String fileName = "";
        String URL_MDS = MDSConnection + "/events/" + seqId;
        RestTemplate restTemplate = new RestTemplate();
        ReadEventDTO readEventDTO = new ReadEventDTO();
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

        readEventDTO.setMetaData(metaDataEntity);
        readEventDTO.setImageBase64Enc(this.recoveryService.recoverImage(fileName, metaDataEntity));

        return readEventDTO;
    }

    public void saveEvent(ReadEventDTO readEventDTO) throws EventNotCreatedException {
        RestTemplate restTemplate = new RestTemplate();
        String URL_MDS = MDSConnection + "/events/" + readEventDTO.getMetaData().getSeqId();
        int hashOfNewImage = this.hashingService.getHash(readEventDTO.getImageBase64Enc());
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
        copyMetaDataFromEntityToDTO(metaDataDTO, readEventDTO.getMetaData());
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
        String fileName = readEventDTO.getMetaData().getSeqId() + "_base.jpg";
        String URL_IOS = IOSConnection + "/images";
        ImageObjectServiceCreateDTO imageObjectServiceCreateDTO = new ImageObjectServiceCreateDTO(fileName, readEventDTO.getImageBase64Enc());
        HttpEntity<ImageObjectServiceCreateDTO> requestCreate = new HttpEntity<>(imageObjectServiceCreateDTO);
        restTemplate.exchange(URL_IOS, HttpMethod.PUT, requestCreate, Void.class);

        // replicate image using ImageFileService
        this.imageFileService.saveImage(fileName, readEventDTO.getImageBase64Enc());
    }

    public void deleteEvent(String seqId) throws EventNotFoundException {
        RestTemplate restTemplate = new RestTemplate();
        String fileName = seqId + ".jpg";
        String URL_IOS = IOSConnection + "/images/" + fileName;
        String URL_MDS = MDSConnection + "/events/" + seqId;
        MetaDataServiceDTO metaDataDTO;

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

        // delete primary image using ImageObjectStorageService
        restTemplate.delete(URL_IOS);

        // delete backup image using ImageFileService
        this.imageFileService.deleteImage(fileName);
    }


    public void deleteTag(String seqId, String tagName) throws EventNotFoundException {
        RestTemplate restTemplate = new RestTemplate();
        String fileName = seqId + "_" + tagName + ".jpg";
        String URL_IOS = IOSConnection + "/images/" + fileName;
        String URL_MDS = MDSConnection + "/events/" + seqId + "/tags/" + tagName;

        // delete metadata using the MetadataService
        restTemplate.delete(URL_MDS);

        // delete primary image using ImageObjectStorageService
        restTemplate.delete(URL_IOS);

        // delete backup image using ImageFileService
        this.imageFileService.deleteImage(fileName);
    }


    public List<ReadEventDTO> readEvents() throws EventNotFoundException {
        ArrayList<ReadEventDTO> eventList = new ArrayList<>();
        String fileName = "";
        String URL_MDS = MDSConnection + "/events";
        RestTemplate restTemplate = new RestTemplate();
        MetaDataEntity metaDataEntity = new MetaDataEntity();

        // querry images
        ResponseEntity<List<MetaDataServiceDTO>> responseMDS = restTemplate.exchange(
                    URL_MDS, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<MetaDataServiceDTO>>() {
                    });

        Iterator<MetaDataServiceDTO> it = responseMDS.getBody().iterator();
        while(it.hasNext()) {
            MetaDataServiceDTO metaDataDTO = it.next();
            fileName = metaDataDTO.getSensingEventId() + ".jpg";
            copyMetaDataFromDTOToEntity(metaDataEntity, metaDataDTO);

            String URL_IOS = IOSConnection + "/images/" + fileName;

            ResponseEntity<ImageObjectServiceLoadDTO> responseIOS = restTemplate.exchange(
                    URL_IOS, HttpMethod.GET, null,
                    new ParameterizedTypeReference<ImageObjectServiceLoadDTO>(){});
            ImageObjectServiceLoadDTO imageIOS = responseIOS.getBody();

            ImageEntity imageIFS = this.imageFileService.readImage(fileName);
            int hashedImageIFS = this.hashingService.getHash(imageIFS.getBase64EncodedImage());
            int hashedImageIOS = this.hashingService.getHash(imageIOS.getBase64Image());
            if(!this.hashingService.compareHash(hashedImageIFS, hashedImageIOS)) {
                log.info("Saved images are not identical.");
                throw new EventNotFoundException("Saved images are not identical.");
            }

            ReadEventDTO readEventDTO = new ReadEventDTO();
            readEventDTO.setImageBase64Enc(imageIOS.getBase64Image());
            readEventDTO.setMetaData(metaDataEntity);
            eventList.add(readEventDTO);
        }

        return eventList;
    }

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

    // Stage 2:
    // Update
}
