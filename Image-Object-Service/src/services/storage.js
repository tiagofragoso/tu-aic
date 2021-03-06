const { minioClient, defaultBucket } = require("../config");
const Base64Service = require("../services/base64");
const { streamToBuffer } = require("../util/stream");
const logger = require("../util/logger");

class FileNotFoundError extends Error {}

class StorageServiceInternalError extends Error {}

class StorageService {
    constructor() {}

    static async init() {
        try {
            const exists = await minioClient.bucketExists(defaultBucket);
            if (!exists) {
                logger.debug(`Creating ${defaultBucket} bucket`);
                await minioClient.makeBucket(defaultBucket);
            } else {
                logger.debug(`${defaultBucket} bucket already exists`);
            }
        } catch (err) {
            throw new StorageServiceInternalError();
        }
    }

    static async storeImageBase64(name, image) {
        logger.debug(`Storing image ${name}`);

        // Create a binary Buffer from the base64 string
        const imageBuffer = Base64Service.toBuffer(image);

        try {
            await this._storeImage(name, imageBuffer);
        } catch (err) {
            throw new StorageServiceInternalError();
        }
    }

    static async getImage(name) {
        logger.debug(`Getting image ${name}`);
        try {

            const [imageData, imageMetadata] = await Promise.all([
                this._getImageData(name),
                this._getImageMetadata(name),
            ]);

            const { lastModified } = imageMetadata;

            return {
                image_file: imageData,
                last_modified: lastModified,
            };

        } catch (err) {
            if (err.code === "NoSuchKey" || err.code === "NotFound") { // MinIO slang for "This file does not exist"
                throw new FileNotFoundError();
            } else {
                throw new StorageServiceInternalError();
            }
        }
    }

    static deleteImage(name) {
        logger.debug(`Deleting image ${name}`);
        try {
            return minioClient.removeObject(defaultBucket, name);
        } catch (err) {
            throw new StorageServiceInternalError();
        }
    }

    static async _getImageData(name) {
        // Get the stream from MinIO
        const stream = await minioClient.getObject(defaultBucket, name);

        // Read the full stream and save contents to a Buffer
        const buffer = await streamToBuffer(stream);

        // Encode the Buffer contents to base64
        return Base64Service.fromBuffer(buffer);
    }

    static _getImageMetadata(name) {
        return minioClient.statObject(defaultBucket, name);
    }

    static _storeImage(name, image) {
        return minioClient.putObject(defaultBucket, name, image);
    }

}

StorageService.errors = { FileNotFoundError, StorageServiceInternalError };

module.exports = StorageService;
