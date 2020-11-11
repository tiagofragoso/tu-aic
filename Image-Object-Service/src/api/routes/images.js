const { Router } = require("express");
const StorageService = require("../../services/storage");

const { FileNotFoundError, StorageServiceInternalError } = StorageService.errors;

const router = Router();

router.get("/:name", async (req, res, _next) => {
    // Validate request (maybe move this to a middleware later)
    const { name } = req.params;
    if (!name) {
        res.status(400).send();
        return;
    }

    try {
        // Retrieve image data and metadata from MinIO
        const response = await StorageService.getImage(name);
        res.send(response);
    } catch (err) {
        if (err instanceof FileNotFoundError) {
            res.status(404).send("The requested file does not exist");
        } else if (err instanceof StorageServiceInternalError) {
            res.status(500).send("An error occurred while retrieving the image");
        } else {
            res.status(500).send("An error occurred");
        }
    }
});

router.put("/", async (req, res, _next) => {
    // Validate request (maybe move this to a middleware later)
    const { name, imageFile } = req.body;
    if (!name || !imageFile) {
        res.status(400).send();
        return;
    }

    try {
        // Store the base64 image
        await StorageService.storeImageBase64(name, imageFile);
        res.status(200).send();
    } catch (err) {
        if (err instanceof StorageServiceInternalError) {
            res.status(500).send("An error occurred while storing the image");
        } else {
            res.status(500).send("An error occurred");
        }
    }
});

module.exports = router;
