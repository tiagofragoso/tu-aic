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

    static storeImage(name, image) {
        console.log(`Storing image ${name}`);
        return minioClient.putObject("images", name, image);
    }

    static getImage(name) {
        console.log(`Getting image ${name}`);
        return minioClient.getObject("images", name);
    }

}

module.exports = StorageService;
