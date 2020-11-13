package group3.aic_middleware.service;

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
        int hashedImage = this.hashingService.getHash(imageName);
        System.out.println(hashedImage);
        // TODO: Return actual mocked image
        return new ImageObjectDTO();
    }

    public void saveImage(ImageObjectDTO imageObjectDTO) throws ImageNotCreatedException {
        int hashedImage = this.hashingService.getHash(imageObjectDTO.getImageEntity().getBase64EncodedImage());
        int oldHash = -1;
        // request old hash from MetadataService
        if(this.hashingService.compareHash(hashedImage, oldHash)) {
            throw new ImageNotCreatedException("Image already exists.");
        }
    }

    public void deleteImage(String imageName) throws ImageNotFoundException {
        // check existence of an image using MetaDataService / ImageObjectStorageService
        throw new ImageNotFoundException("test");
    }

    public List<ImageObjectDTO> readImagesForDevice(String id) throws ImageNotFoundException {
        return new ArrayList<ImageObjectDTO>();
    }
    // Communicates with the other microservices

    // MetaDataService (MDS)
    // ImageObjectStorageService (IOSS)
    // ImageFileService (IFS)

    // X - Hashing Service
    // Replication Service (= requests are replicated to the IFS and the IOSS and vice versa)


    // Stage 2:
    // Recovery Service
    // Logging Service???
}
