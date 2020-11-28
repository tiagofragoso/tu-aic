package group3.aic_middleware.services;

import group3.aic_middleware.exceptions.DeviceNotFoundException;
import group3.aic_middleware.exceptions.DropboxLoginException;
import group3.aic_middleware.exceptions.ImageNotCreatedException;
import group3.aic_middleware.exceptions.ImageNotFoundException;
import group3.aic_middleware.entities.ImageEntity;
import group3.aic_middleware.restData.*;
import group3.aic_middleware.entities.MetaDataEntity;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class FederationService {

    HashingService hashingService = new HashingService();
    ImageFileService imageFileService = new ImageFileService();

    public FederationService() throws NoSuchAlgorithmException, ImageNotFoundException, ImageNotCreatedException, DropboxLoginException, JSONException, IOException {
    }

    public ImageObjectDTO readImage(String seqId) throws ImageNotFoundException {
        String fileName = "";
        String URL_MDS = "localhost:8080/event/" + seqId;
        RestTemplate restTemplate = new RestTemplate();
        MetaDataEntity metaDataEntity = new MetaDataEntity();

        // 27.11.2020: check existence of an image using MetaDataService
        ResponseEntity<MetaDataServiceDTO> responseMDS = restTemplate.exchange(
                URL_MDS, HttpMethod.GET, null,
                new ParameterizedTypeReference<MetaDataServiceDTO>(){});
        MetaDataServiceDTO metaDataDTO = responseMDS.getBody();

        if(metaDataDTO == null) {
            throw new ImageNotFoundException("Requested image does not exist.");
        } else {
            fileName = metaDataDTO.getSensingEventId() + ".jpg";
            copyMetaDataFromDTOToEntity(metaDataEntity, metaDataDTO);
        }

        String URL_IOS = "localhost:8000/images/" + fileName;

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
            throw new ImageNotFoundException("Saved images are not identical.");
        }

        // 27.11.2020: encapsulate requested image and return it
        ImageObjectDTO imageObjectDTO = new ImageObjectDTO();
        imageObjectDTO.setImage(new ImageEntity(imageIOS.getBase64Image()));
        imageObjectDTO.setMetaData(metaDataEntity);

        return imageObjectDTO;
    }

    public void saveImage(ImageObjectDTO imageObjectDTO) throws ImageNotCreatedException {
        int seuid = -1;
        RestTemplate restTemplate = new RestTemplate();
        String URL_MDS = "localhost:8080/event/" + imageObjectDTO.getMetaData().getSeqId();
        int hashOfNewImage = this.hashingService.getHash(imageObjectDTO.getImage().getBase64EncodedImage());
        // 27.11.2020: check existence of an image using MetaDataService and request old hash
        ResponseEntity<MetaDataServiceDTO> responseMDS = restTemplate.exchange(
                URL_MDS, HttpMethod.GET, null,
                new ParameterizedTypeReference<MetaDataServiceDTO>(){});
        MetaDataServiceDTO metaDataDTO = responseMDS.getBody();

        if(metaDataDTO != null) {
            if(this.hashingService.compareHash(hashOfNewImage, metaDataDTO.getTags().iterator().next().getImageHash())) {
                throw new ImageNotCreatedException("Image already exists.");
            }
        } else {
            metaDataDTO = new MetaDataServiceDTO();
        }

        // 27.11.2020: save metadata using the MetadataService
        URL_MDS = "localhost:8080/event";
        copyMetaDataFromEntityToDTO(metaDataDTO, imageObjectDTO.getMetaData());
        ArrayList<TagDTO> tagList = new ArrayList<>();
        tagList.add(new TagDTO("base", hashOfNewImage));
        metaDataDTO.setTags(tagList);
        HttpEntity<MetaDataServiceDTO> request = new HttpEntity<>(metaDataDTO);
        ResponseEntity<MetaDataServiceDTO> response = restTemplate.exchange(URL_MDS, HttpMethod.POST, request, MetaDataServiceDTO.class);
        MetaDataServiceDTO metaDataServiceDTO = response.getBody();

        // 27.11.2020: save image using ImageObjectStorageService
        String fileName = imageObjectDTO.getMetaData().getSeqId() + ".jpg";
        String URL_IOS = "localhost:8000/images";
        ImageObjectServiceCreateDTO imageObjectServiceCreateDTO = new ImageObjectServiceCreateDTO(fileName, imageObjectDTO.getImage().getBase64EncodedImage());
        HttpEntity<ImageObjectServiceCreateDTO> requestCreate = new HttpEntity<>(imageObjectServiceCreateDTO);
        restTemplate.exchange(URL_IOS, HttpMethod.PUT, requestCreate, Void.class);

        // 27.11.2020: replicate image using ImageFileService
        this.imageFileService.saveImage(imageObjectDTO);
    }

    public void deleteImage(String seqId) throws ImageNotFoundException {
        int seuid = -1;
        RestTemplate restTemplate = new RestTemplate();
        String fileName = seqId + ".jpg";
        String URL_IOS = "localhost:8000/images/" + fileName;

        // 27.11.2020: check existence of an image using ImageObjectStorageService
        ResponseEntity<ImageObjectServiceLoadDTO> response = restTemplate.exchange(
                URL_IOS, HttpMethod.GET, null,
                new ParameterizedTypeReference<ImageObjectServiceLoadDTO>(){});

        ImageObjectServiceLoadDTO imageIOS = response.getBody();
        if(imageIOS == null) {
            throw new ImageNotFoundException("Requested image doesn't exist.");
        }

        // TODO: delete metadata using the MetadataService


        // 27.11.2020: delete image using ImageObjectStorageService
        restTemplate.delete(URL_IOS);

        // 27.11.2020: delete image using ImageFileService
        this.imageFileService.deleteImage(fileName);
    }

    // TODO ? Stage 2 ?
    public List<ImageObjectDTO> readImagesForDevice(String id) throws ImageNotFoundException, DeviceNotFoundException {
        String deviceName = "";
        // check existence of a device using MetaDataService / ImageObjectStorageService
        if(deviceName == "") {
            throw new DeviceNotFoundException("Requested device doesn't exist.");
        }
        // query images using ImageObjectStorageService (primary)
        // query images using ImageObjectStorageService (secondary)
        // encapsulate obtained images, create ArrayList out of them and return the List of image DTOs
        return new ArrayList<ImageObjectDTO>();
    }

    private void copyMetaDataFromDTOToEntity(MetaDataEntity metaDataEntity, MetaDataServiceDTO metaDataServiceDTO) {
        metaDataEntity.setSeqId(metaDataServiceDTO.getSensingEventId());
        metaDataEntity.setName(metaDataServiceDTO.getName());
        metaDataEntity.setDeviceId(metaDataServiceDTO.getDeviceIdentifier());
        metaDataEntity.setDatetime(LocalDateTime.ofInstant(Instant.ofEpochMilli(metaDataServiceDTO.getTimestamp()), ZoneId.systemDefault()));
        metaDataEntity.setLongitude(metaDataServiceDTO.getLongitude());
        metaDataEntity.setLatitude(metaDataServiceDTO.getLatitude());
    }

    private void copyMetaDataFromEntityToDTO(MetaDataServiceDTO metaDataServiceDTO, MetaDataEntity metaDataEntity) {
        metaDataServiceDTO.setSensingEventId(metaDataEntity.getSeqId());
        metaDataServiceDTO.setName(metaDataEntity.getName());
        metaDataServiceDTO.setDeviceIdentifier(metaDataEntity.getDeviceId());
        metaDataServiceDTO.setTimestamp(ZonedDateTime.of(metaDataEntity.getDatetime(), ZoneId.systemDefault()).toInstant().toEpochMilli());
        metaDataServiceDTO.setLongitude(metaDataEntity.getLongitude());
        metaDataServiceDTO.setLatitude(metaDataEntity.getLatitude());
    }

    // Stage 2:
    // Recovery Service
    // Logging Service???
}
