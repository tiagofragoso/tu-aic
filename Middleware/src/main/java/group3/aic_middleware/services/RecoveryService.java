package group3.aic_middleware.services;

import group3.aic_middleware.entities.ImageEntity;
import group3.aic_middleware.exceptions.EventNotCreatedException;
import group3.aic_middleware.exceptions.EventNotFoundException;
import group3.aic_middleware.restData.ImageObjectServiceCreateDTO;
import group3.aic_middleware.restData.ImageObjectServiceLoadDTO;
import group3.aic_middleware.restData.MetaDataServiceDTO;
import group3.aic_middleware.restData.TagDTO;
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

    private String MDSConnection = "http://metadata-service:8080";
    private String IOSConnection = "http://image-object-service:8000";

    public RecoveryService() throws NoSuchAlgorithmException {
    }

    public String recoverImage(String fileName) {
        RestTemplate restTemplate = new RestTemplate();
        String retImage = "";
        String URL_IOS = IOSConnection + "/images/" + fileName;

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
                URL_IOS = IOSConnection + "/images";
                ImageObjectServiceCreateDTO imageObjectServiceCreateDTO = null;
                try {
                    retImage = this.imageFileService.readImage(fileName).getBase64EncodedImage();
                    } catch (EventNotFoundException ex) {
                    log.info("Requested sensing event doesn't exist in Image File Storage.");
                    ex.printStackTrace();
                }
                imageObjectServiceCreateDTO = new ImageObjectServiceCreateDTO(fileName, retImage);
                HttpEntity<ImageObjectServiceCreateDTO> requestCreate = new HttpEntity<>(imageObjectServiceCreateDTO);
                restTemplate.exchange(URL_IOS, HttpMethod.PUT, requestCreate, Void.class);
            }
        }
        ImageObjectServiceLoadDTO imageIOS = responseIOS.getBody();

        // query an image using ImageFileStorageService (secondary/backup)
        if(imageIOS != null) {
            retImage = imageIOS.getBase64Image();
            try {
                this.imageFileService.saveImage(fileName, retImage);
            } catch (EventNotCreatedException e) {
                e.printStackTrace();
                log.info("Requested sensing event couldn't be recovered in Image File Storage: " + e.getMessage());
            }
        }

        return retImage;
    }

    public String getEventStatus(MetaDataServiceDTO metaDataDTO) throws EventNotFoundException {
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
                    if(hashedImageIFS == this.getHashValue(metaDataDTO)) {
                        return "faulty";
                    } else {
                        log.info("Requested sensing event is corrupted in Image File Storage.");
                        return "missing";
                    }
                } catch (EventNotFoundException ex) {
                    log.info("Requested sensing event doesn't exist in any Storage.");
                    return "missing";
                }
            }
        }

        ImageObjectServiceLoadDTO imageIOS = responseIOS.getBody();
        try {
            imageIFS = this.imageFileService.readImage(fileName);
        } catch (EventNotFoundException ex) {
            log.info("Requested sensing event doesn't exist in Image File Storage.");
            int hashedImageIOS = this.hashingService.getHash(imageIOS.getBase64Image());
            if(hashedImageIOS == this.getHashValue(metaDataDTO)) {
                return "faulty";
            } else {
                log.info("Requested sensing event is corrupted in Image Object Storage.");
                return "missing";
            }
        }

        int hashedImageIFS = this.hashingService.getHash(imageIFS.getBase64EncodedImage());
        int hashedImageIOS = this.hashingService.getHash(imageIOS.getBase64Image());
        if(!this.hashingService.compareHash(hashedImageIFS, hashedImageIOS)) {
            log.info("Saved images are not identical.");
            return "faulty";
        }

        return "valid";
    }

    private int getHashValue(MetaDataServiceDTO metaDataServiceDTO) {
        Iterator<TagDTO> it = metaDataServiceDTO.getTags().iterator();
        while(it.hasNext()) {
            TagDTO tagDTO = it.next();
            if(tagDTO.getTagName() == "base") {
                return tagDTO.getImageHash();
            }
        }
        return -1;
    }

}
