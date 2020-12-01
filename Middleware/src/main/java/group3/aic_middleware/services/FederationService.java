package group3.aic_middleware.services;

import group3.aic_middleware.exceptions.DeviceNotFoundException;
import group3.aic_middleware.exceptions.DropboxLoginException;
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

    String MDSConnection = "http://metadata-service:8080";
    String IOSConnection = "http://image-object-service:8000";

    public FederationService() throws NoSuchAlgorithmException, EventNotFoundException, EventNotCreatedException, DropboxLoginException, JSONException, IOException {
    }

    public ReadEventDTO readEvent(String seqId) throws EventNotFoundException {
        String fileName = "";
        String URL_MDS = MDSConnection + "/events/" + seqId;
        RestTemplate restTemplate = new RestTemplate();
        MetaDataEntity metaDataEntity = new MetaDataEntity();

        // 27.11.2020: check existence of an image using MetaDataService
        ResponseEntity<MetaDataServiceDTO> responseMDS = null;
        try {
            responseMDS = restTemplate.exchange(
                    URL_MDS, HttpMethod.GET, null,
                    new ParameterizedTypeReference<MetaDataServiceDTO>() {
                    });
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new EventNotFoundException("Requested sensing event doesn't exist.");
            }
        }

        MetaDataServiceDTO metaDataDTO = responseMDS.getBody();

        fileName = metaDataDTO.getSensingEventId() + ".jpg";
        copyMetaDataFromDTOToEntity(metaDataEntity, metaDataDTO);

        String URL_IOS = IOSConnection + "/images/" + fileName;

        // 27.11.2020: query an image using ImageObjectStorageService (primary)
        ResponseEntity<ImageObjectServiceLoadDTO> responseIOS = restTemplate.exchange(
                URL_IOS, HttpMethod.GET, null,
                new ParameterizedTypeReference<ImageObjectServiceLoadDTO>(){});
        ImageObjectServiceLoadDTO imageIOS = responseIOS.getBody();

        // 27.11.2020: compare images adding Recovery function
        // 27.11.2020: query an image using ImageObjectStorageService (secondary)
        ImageEntity imageIFS = this.imageFileService.readImage(fileName);
        int hashedImageIFS = this.hashingService.getHash(imageIFS.getBase64EncodedImage());
        int hashedImageIOS = this.hashingService.getHash(imageIOS.getBase64Image());
        if(!this.hashingService.compareHash(hashedImageIFS, hashedImageIOS)) {
            throw new EventNotFoundException("Saved images are not identical.");
        }

        // 27.11.2020: encapsulate requested image and return it
        ReadEventDTO readEventDTO = new ReadEventDTO();
        readEventDTO.setImage(new ImageEntity(imageIOS.getBase64Image()));
        readEventDTO.setMetaData(metaDataEntity);

        return readEventDTO;
    }

    public void saveEvent(ReadEventDTO readEventDTO) throws EventNotCreatedException {
        RestTemplate restTemplate = new RestTemplate();
        String URL_MDS = MDSConnection + "/events/" + readEventDTO.getMetaData().getSeqId();
        int hashOfNewImage = this.hashingService.getHash(readEventDTO.getImage().getBase64EncodedImage());
        MetaDataServiceDTO metaDataDTO = null;

        // 27.11.2020: check existence of an image using MetaDataService and request old hash
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
                metaDataDTO = responseMDS.getBody();
                if(this.hashingService.compareHash(hashOfNewImage, metaDataDTO.getTags().iterator().next().getImageHash())) {
                    log.warn("6");
                    throw new EventNotCreatedException("Sensing event already exists.");
                }
            }
        }

        // 27.11.2020: save metadata using the MetadataService
        URL_MDS = MDSConnection + "/events";
        copyMetaDataFromEntityToDTO(metaDataDTO, readEventDTO.getMetaData());
        ArrayList<TagDTO> tagList = new ArrayList<>();
        tagList.add(new TagDTO("base", hashOfNewImage));
        metaDataDTO.setTags(tagList);
        HttpEntity<MetaDataServiceDTO> request = new HttpEntity<>(metaDataDTO);
        ResponseEntity<MetaDataServiceDTO> response = restTemplate.exchange(URL_MDS, HttpMethod.POST, request, MetaDataServiceDTO.class);
        MetaDataServiceDTO metaDataServiceDTO = response.getBody();

        // 27.11.2020: save image using ImageObjectStorageService
        String fileName = readEventDTO.getMetaData().getSeqId() + ".jpg";
        String URL_IOS = IOSConnection + "/images";
        ImageObjectServiceCreateDTO imageObjectServiceCreateDTO = new ImageObjectServiceCreateDTO(fileName, readEventDTO.getImage().getBase64EncodedImage());
        HttpEntity<ImageObjectServiceCreateDTO> requestCreate = new HttpEntity<>(imageObjectServiceCreateDTO);
        restTemplate.exchange(URL_IOS, HttpMethod.PUT, requestCreate, Void.class);

        // 27.11.2020: replicate image using ImageFileService
        this.imageFileService.saveImage(readEventDTO);
    }

    public void deleteEvent(String seqId) throws EventNotFoundException {
        RestTemplate restTemplate = new RestTemplate();
        String fileName = seqId + ".jpg";
        String URL_IOS = IOSConnection + "/images/" + fileName;
        String URL_MDS = MDSConnection + "/events/" + seqId;
        MetaDataServiceDTO metaDataDTO;

        // 27.11.2020: check existence of an image using MetaDataService
        ResponseEntity<MetaDataServiceDTO> responseMDS = null;
        try {
            responseMDS = restTemplate.exchange(
                    URL_MDS, HttpMethod.GET, null,
                    new ParameterizedTypeReference<MetaDataServiceDTO>() {
                    });
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new EventNotFoundException("Requested sensing event doesn't exist.");
            }
        }

        // TODO: delete metadata using the MetadataService


        // 27.11.2020: delete image using ImageObjectStorageService
        restTemplate.delete(URL_IOS);

        // 27.11.2020: delete image using ImageFileService
        this.imageFileService.deleteImage(fileName);
    }

    // TODO ? Stage 2 ?
    public List<ReadEventDTO> readEventsForDevice(String id) throws EventNotFoundException, DeviceNotFoundException {
        String deviceName = "";
        // check existence of a device using MetaDataService / ImageObjectStorageService
        if(deviceName == "") {
            throw new DeviceNotFoundException("Requested device doesn't exist.");
        }
        // query images using ImageObjectStorageService (primary)
        // query images using ImageObjectStorageService (secondary)
        // encapsulate obtained images, create ArrayList out of them and return the List of image DTOs
        return new ArrayList<ReadEventDTO>();
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
    }

    // Stage 2:
    // Recovery Service
    // Logging Service???
}
