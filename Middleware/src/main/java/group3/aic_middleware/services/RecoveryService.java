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

    public RecoveryService() throws NoSuchAlgorithmException {
    }

    /**
     * Function recovers the given image either in the IOS or IFS. For the control purpose of the current state of the image
     * (whether it is corrupted or not) we use the hash value obtained from meta data storage.
     *
     * @param fileName image to be recovered
     * @param hashStored hash value stored for given image in meta data storage
     *
     * @return recovered image in base64 encoding
     */
    public String recoverImage(String fileName, String hashStored) {
        RestTemplate restTemplate = new RestTemplate();
        String retImage = "";
        String URL_IOS = FederationService.IOSConnection + "/images/" + fileName;
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
                    URL_IOS = FederationService.IOSConnection + "/images";
                    restTemplate.exchange(URL_IOS, HttpMethod.PUT, requestCreate, Void.class);
                } catch (EventNotFoundException ex) {
                    log.info("Requested sensing event doesn't exist in Image File Storage.");
                    return "";
                }
            }
        }
        ImageObjectServiceLoadDTO imageIOS = responseIOS.getBody();
        URL_IOS = FederationService.IOSConnection + "/images";

        // query an image using ImageFileStorageService (secondary/backup)
        if(imageIOS != null) {
            retImage = imageIOS.getBase64Image();
            if(hashStored.equals(this.hashingService.getHash(retImage))) {
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

    /**
     * Function provides a status for a sensing event. The procedure is as follows: first it check the existence of an
     * image in IOS. If it is saved in IOS and the hash value of the IOS image is the same as the hash value from meta
     * data storage then status is CORRECT, otherwise FAULTY. If the image is not found in IOS, check the IFS. I fit is
     * IFS and hash values equals then status is FAULTY, otherwise the status is MISSING and we can not recover the image.
     *
     * @param metaDataDTO meta data of a sensing event we are requesting the status information for
     *
     * @return status of a sensing event: CORRECT / FAULTY / MISSING
     */
    public String getEventStatus(MetaDataServiceDTO metaDataDTO) {
        String fileName = metaDataDTO.getSensingEventId() + "_base.jpg";
        String URL_IOS = FederationService.IOSConnection + "/images/" + fileName;
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
                    String hashedImageIFS = this.hashingService.getHash(imageIFS.getBase64EncodedImage());

                    // compare the hash value of an image from IFS with the stored hash in Metadata Storage
                    if(hashedImageIFS.equals(this.getHashValue(metaDataDTO, "base"))) {
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

        String hashedImageIOS = this.hashingService.getHash(imageIOS.getBase64Image());

        // compare the hash value of an image from IOS with the stored hash in Metadata Storage
        if(hashedImageIOS.equals(this.getHashValue(metaDataDTO, "base"))) {
            return "CORRECT";
        } else {
            log.info("Requested sensing event is corrupted in Image Object Storage.");
            return "FAULTY";
        }
    }

    /**
     * Function provides a status for a sensing event. The procedure is the same as above, but the source DTO has changed.
     *
     * @param readEventsDTO meta data of a sensing event we are requesting the status information for
     *
     * @return status of a sensing event: CORRECT / FAULTY / MISSING
     */
    public String getEventStatus(ReadEventsDTO readEventsDTO) {
        String fileName = readEventsDTO.getSeqId() + "_base.jpg";
        String URL_IOS = FederationService.IOSConnection + "/images/" + fileName;
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
                    String hashedImageIFS = this.hashingService.getHash(imageIFS.getBase64EncodedImage());

                    // compare the hash value of an image from IFS with the stored hash in Metadata Storage
                    if(hashedImageIFS.equals(this.getHashValue(readEventsDTO, "base"))) {
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

        String hashedImageIOS = this.hashingService.getHash(imageIOS.getBase64Image());

        // compare the hash value of an image from IOS with the stored hash in Metadata Storage
        if(hashedImageIOS.equals(this.getHashValue(readEventsDTO, "base"))) {
            return "CORRECT";
        } else {
            log.info("Requested sensing event is corrupted in Image Object Storage.");
            return "FAULTY";
        }
    }

    /**
     * Function looks for a hash value of a tag with the given tagName in the provided metaDataServiceDTO
     *
     * @param metaDataServiceDTO meta data to search
     * @param tagName name of the tag we are looking for
     *
     * @return hash value of an image of a given tag
     */
    public String getHashValue(MetaDataServiceDTO metaDataServiceDTO, String tagName) {
        Iterator<TagDTO> it = metaDataServiceDTO.getTags().iterator();
        while(it.hasNext()) {
            TagDTO tagDTO = it.next();
            if(tagDTO.getTagName().equals(tagName)) {
                return tagDTO.getImageHash();
            }
        }
        return "";
    }

    /**
     * Function looks for a hash value of a tag with the given tagName in the provided readEventsDTO
     *
     * @param readEventsDTO meta data to search
     * @param tagName name of the tag we are looking for
     *
     * @return hash value of an image of a given tag
     */
    private String getHashValue(ReadEventsDTO readEventsDTO, String tagName) {
        Iterator<SimpleTagDTO> it = readEventsDTO.getTags().iterator();
        while(it.hasNext()) {
            SimpleTagDTO tagDTO = it.next();
            if(tagDTO.getTagName().equals(tagName)) {
                return tagDTO.getImageHash();
            }
        }
        return "";
    }

}
