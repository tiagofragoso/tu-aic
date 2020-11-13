package group3.aic_middleware.service;

import group3.aic_middleware.exceptions.DeviceNotFoundException;
import group3.aic_middleware.exceptions.ImageNotCreatedException;
import group3.aic_middleware.exceptions.ImageNotFoundException;
import group3.aic_middleware.restData.ImageObjectDTO;

import java.security.NoSuchAlgorithmException;
import java.util.*;

public class FederationService {

    HashingService hashingService = new HashingService();

    public FederationService() throws NoSuchAlgorithmException {
    }

    public ImageObjectDTO readImage(String imageName) throws ImageNotFoundException {
        int seuid = -1;
        // TODO: check existence of an image using MetaDataService / ImageObjectStorageService
        if(seuid == -1) {
            throw new ImageNotFoundException("Requested image doesn't exist.");
        }
        // TODO: query an image using ImageObjectStorageService (primary)
        // TODO: query an image using ImageObjectStorageService (secondary)
        // TODO: encapsulate requested image and return it
        int hashedImage = this.hashingService.getHash(imageName);
        System.out.println(hashedImage);
        return new ImageObjectDTO();
    }

    public void saveImage(ImageObjectDTO imageObjectDTO) throws ImageNotCreatedException {
        int seuid = -1;
        int oldHash = -1;
        int hashedImage = this.hashingService.getHash(imageObjectDTO.getImageEntity().getBase64EncodedImage());
        // TODO: check existence of an image using MetaDataService / ImageObjectStorageService
        if(seuid != -1) {
            // TODO: request old hash from MetadataService
            if(this.hashingService.compareHash(hashedImage, oldHash)) {
                throw new ImageNotCreatedException("Image already exists.");
            }
        }
        // TODO: save metadata using the MetadataService
        // TODO: save image using ImageObjectStorageService
        // TODO: replicate image using ImageFileService
    }

    public void deleteImage(String imageName) throws ImageNotFoundException {
        int seuid = -1;
        // TODO: check existence of an image using MetaDataService / ImageObjectStorageService
        if(seuid == -1) {
            throw new ImageNotFoundException("Requested image doesn't exist.");
        }
        // TODO: delete metadata using the MetadataService
        // TODO: delete image using ImageObjectStorageService
        // TODO: delete image using ImageFileService
    }

    public List<ImageObjectDTO> readImagesForDevice(String id) throws ImageNotFoundException, DeviceNotFoundException {
        String deviceName = "";
        // TODO: check existence of a device using MetaDataService / ImageObjectStorageService
        if(deviceName == "") {
            throw new DeviceNotFoundException("Requested device doesn't exist.");
        }
        // TODO: query images using ImageObjectStorageService (primary)
        // TODO: query images using ImageObjectStorageService (secondary)
        // TODO: encapsulate obtained images, create ArrayList out of them and return the List of image DTOs
        return new ArrayList<ImageObjectDTO>();
    }
    // Communicates with the other microservices

    // MetaDataService (MDS)
    // ImageObjectStorageService (IOSS)
    // ImageFileService (IFS)

    // X - Hashing Service
    // X - Replication Service (= requests are replicated to the IFS and the IOSS and vice versa)


    // Stage 2:
    // Recovery Service
    // Logging Service???
}
