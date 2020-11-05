package group3.aic_middleware.service;

import group3.aic_middleware.exceptions.ImageNotCreatedException;
import group3.aic_middleware.exceptions.ImageNotFoundException;
import group3.aic_middleware.restData.ImageObjectEntity;

public class FederationService {

    public ImageObjectEntity readImage(String imageName) throws ImageNotFoundException {
        // TODO: Return actual mocked image
        return new ImageObjectEntity();
    }

    public void saveImage(ImageObjectEntity imageObjectEntity) throws ImageNotCreatedException {
        throw new ImageNotCreatedException("test");
    }

    public void deleteImage(String imageName) throws ImageNotFoundException {
        throw new ImageNotFoundException("test");
    }
    // Communicates with the other microservices

    // MetaDataService (MDS)
    // ImageObjectStorageService (IOSS)
    // ImageFileService (IFS)

    // Hashing Service
    // Replication Service (= requests are replicated to the IFS and the IOSS and vice versa)


    // Stage 2:
    // Recovery Service
    // Logging Service???
}
