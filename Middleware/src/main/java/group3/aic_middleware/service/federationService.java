package group3.aic_middleware.service;

import group3.aic_middleware.restData.ImageObjectEntity;

public class federationService {

    public static ImageObjectEntity  readImage(String id){
        // TODO: Return actual mocked image
        return new ImageObjectEntity();
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
