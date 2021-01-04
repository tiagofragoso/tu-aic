package group3.aic_middleware.services;

import group3.aic_middleware.entities.ImageEntity;
import group3.aic_middleware.exceptions.EventNotCreatedException;
import group3.aic_middleware.exceptions.EventNotFoundException;
import group3.aic_middleware.restData.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import lombok.extern.log4j.Log4j;

import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

@Service
@Log4j
public class RecoveryService {

    private HashingService hashingService = new HashingService();
    private ImageFileService imageFileService = new ImageFileService();

    String MDSConnection = "http://metadata-service:8080";
    String IOSConnection = "http://image-object-service:8000";

    public RecoveryService() throws NoSuchAlgorithmException {
    }

    public String recoverImage(String fileName, int hashStored) {
        RestTemplate restTemplate = new RestTemplate();
        String retImage = "";
        String URL_IOS = IOSConnection + "/images/" + fileName;
        ImageObjectServiceCreateDTO imageObjectServiceCreateDTO = null;

        // query an image using ImageObjectStorageService (primary)
        // if not exists then recover it into primary storage
        ResponseEntity<ImageObjectServiceLoadDTO> responseIOS = null;
        try {
            responseIOS = restTemplate.exchange(
                    URL_IOS, HttpMethod.GET, null,
                    new ParameterizedTypeReference<ImageObjectServiceLoadDTO>(){}
                    );
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.info("Requested sensing event doesn't exist in Image Object Storage.");
                try {
                    retImage = this.imageFileService.readImage(fileName).getBase64EncodedImage();
                    imageObjectServiceCreateDTO = new ImageObjectServiceCreateDTO(fileName, retImage);
                    HttpEntity<ImageObjectServiceCreateDTO> requestCreate = new HttpEntity<>(imageObjectServiceCreateDTO);
                    URL_IOS = IOSConnection + "/images";
                    restTemplate.exchange(URL_IOS, HttpMethod.PUT, requestCreate, Void.class);
                } catch (EventNotFoundException ex) {
                    log.info("Requested sensing event doesn't exist in Image File Storage.");
                    return "";
                }
            }
        }
        ImageObjectServiceLoadDTO imageIOS = responseIOS.getBody();
        URL_IOS = IOSConnection + "/images";

        // query an image using ImageFileStorageService (secondary/backup)
        if(imageIOS != null) {
            retImage = imageIOS.getBase64Image();
            if(hashStored == this.hashingService.getHash(retImage)) {
                try {
                    this.imageFileService.saveImage(fileName, retImage);
                } catch (EventNotCreatedException e) {
                    e.printStackTrace();
                    log.info("Requested sensing event couldn't be recovered in Image File Storage: " + e.getMessage());
                }
            } else {
                try {
                    retImage = this.imageFileService.readImage(fileName).getBase64EncodedImage();
                    imageObjectServiceCreateDTO = new ImageObjectServiceCreateDTO(fileName, retImage);
                    HttpEntity<ImageObjectServiceCreateDTO> requestCreate = new HttpEntity<>(imageObjectServiceCreateDTO);
                    restTemplate.exchange(URL_IOS, HttpMethod.PUT, requestCreate, Void.class);
                } catch (EventNotFoundException e) {
                    log.info("Requested sensing event couldn't be recovered from Image File Storage: " + e.getMessage());
                    return "";
                }
            }
        }

        return retImage;
    }

    public String getEventStatus(MetaDataServiceDTO metaDataDTO) {
        String fileName = metaDataDTO.getSensingEventId() + "_base.jpg";
        String URL_IOS = IOSConnection + "/images/" + fileName;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ImageObjectServiceLoadDTO> responseIOS = null;
        ImageEntity imageIFS = null;

        try {
            responseIOS = restTemplate.exchange(
                    URL_IOS, HttpMethod.GET, null,
                    new ParameterizedTypeReference<ImageObjectServiceLoadDTO>(){});
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.info("Requested sensing event doesn't exist in Image Object Storage.");
                try {
                    imageIFS = this.imageFileService.readImage(fileName);
                    int hashedImageIFS = this.hashingService.getHash(imageIFS.getBase64EncodedImage());

                    // compare the hash value of an image from IFS with the stored hash in Metadata Storage
                    if(hashedImageIFS == this.getHashValue(metaDataDTO)) {
                        return "FAULTY";
                    } else {
                        log.info("Requested sensing event is corrupted in Image File Storage.");
                        return "MISSING";
                    }
                } catch (EventNotFoundException ex) {
                    log.info("Requested sensing event doesn't exist in any Storage.");
                    return "MISSING";
                }
            }
        }

        ImageObjectServiceLoadDTO imageIOS = responseIOS.getBody();

        int hashedImageIOS = this.hashingService.getHash(imageIOS.getBase64Image());

        // compare the hash value of an image from IOS with the stored hash in Metadata Storage
        if(hashedImageIOS == this.getHashValue(metaDataDTO)) {
            return "CORRECT";
        } else {
            log.info("Requested sensing event is corrupted in Image Object Storage.");
            return "FAULTY";
        }
    }

    public String getEventStatus(ReadEventsDTO metaDataDTO) {
        String fileName = metaDataDTO.getSeqId() + "_base.jpg";
        String URL_IOS = IOSConnection + "/images/" + fileName;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ImageObjectServiceLoadDTO> responseIOS = null;
        ImageEntity imageIFS = null;

        try {
            responseIOS = restTemplate.exchange(
                URL_IOS, HttpMethod.GET, null,
                new ParameterizedTypeReference<ImageObjectServiceLoadDTO>(){});
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.info("Requested sensing event doesn't exist in Image Object Storage.");
                try {
                    imageIFS = this.imageFileService.readImage(fileName);
                    int hashedImageIFS = this.hashingService.getHash(imageIFS.getBase64EncodedImage());

                    // compare the hash value of an image from IFS with the stored hash in Metadata Storage
                    if(hashedImageIFS == this.getHashValue(metaDataDTO)) {
                        return "FAULTY";
                    } else {
                        log.info("Requested sensing event is corrupted in Image File Storage.");
                        return "MISSING";
                    }
                } catch (EventNotFoundException ex) {
                    log.info("Requested sensing event doesn't exist in any Storage.");
                    return "MISSING";
                }
            }
        }

        ImageObjectServiceLoadDTO imageIOS = responseIOS.getBody();

        int hashedImageIOS = this.hashingService.getHash(imageIOS.getBase64Image());

        // compare the hash value of an image from IOS with the stored hash in Metadata Storage
        if(hashedImageIOS == this.getHashValue(metaDataDTO)) {
            return "CORRECT";
        } else {
            log.info("Requested sensing event is corrupted in Image Object Storage.");
            return "FAULTY";
        }
    }

    public int getHashValue(MetaDataServiceDTO metaDataServiceDTO) {
        Iterator<TagDTO> it = metaDataServiceDTO.getTags().iterator();
        while(it.hasNext()) {
            TagDTO tagDTO = it.next();
            if(tagDTO.getTagName().equals("base")) {
                return tagDTO.getImageHash();
            }
        }
        return -1;
    }

    private int getHashValue(ReadEventsDTO metaDataServiceDTO) {
        Iterator<SimpleTagDTO> it = metaDataServiceDTO.getTags().iterator();
        while(it.hasNext()) {
            SimpleTagDTO tagDTO = it.next();
            if(tagDTO.getTagName().equals("base")) {
                return tagDTO.getImageHash();
            }
        }
        return -1;
    }

}
