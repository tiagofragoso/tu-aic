const { minioClient } = require("../config");

class StorageService {
    constructor() {}

    static async init() {
        const exists = await minioClient.bucketExists("images");
        if (!exists) {
            console.log("Creating images bucket");
            await minioClient.makeBucket("images");
        } else {
            console.log("Images bucket already exists");
        }
        // Do something if bucket can't be created
    }

    static async storeImage(name, image) {
        console.log(`Storing image ${name}`);
        // Process image
        await minioClient.putObject("images", name, image);
        // Return result
    }

    static async getImage(name) {
        console.log(`Getting image ${name}`);
        await minioClient.getObject("images", name);
        // Return image
    }

}

module.exports = StorageService;
