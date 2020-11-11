const { minioClient } = require("../config");
const Base64Service = require("../services/base64");
const { streamToBuffer } = require("../util/stream");

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

    static async storeImageBase64(name, image) {
        console.log(`Storing image ${name}`);

        // Create a binary Buffer from the base64 string
        const imageBuffer = Base64Service.toBuffer(image);

        try {
            await this._storeImage(name, imageBuffer);
        } catch (err) {
            throw new Error();
        }
    }

    static async getImage(name) {
        console.log(`Getting image ${name}`);
        try {

            const [imageData, imageMetadata] = await Promise.all([
                this._getImageData(name),
                this._getImageMetadata(name),
            ]);

            const { lastModified } = imageMetadata;

            return {
                imageFile: imageData,
                lastModified,
            };

        } catch (err) {
            if (err.code === "NoSuchKey" || err.code === "NotFound") { // MinIO slang for "This file does not exist"
                return {
                    imageFile: "The requested file does not exist",
                };
            } else {
                return {
                    imageFile: null,
                };
            }
        }
    }

    static async _getImageData(name) {
        // Get the stream from the StorageService
        const stream = await minioClient.getObject("images", name);

        // Read the full stream and save contents to a Buffer
        const buffer = await streamToBuffer(stream);

        // Encode the Buffer contents to base64
        return Base64Service.fromBuffer(buffer);
    }

    static _getImageMetadata(name) {
        return minioClient.statObject("images", name);
    }

    static _storeImage(name, image) {
        return minioClient.putObject("images", name, image);
    }

}

module.exports = StorageService;
