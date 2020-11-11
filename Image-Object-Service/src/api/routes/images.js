const { Router } = require("express");
const StorageService = require("../../services/storage");

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
        // if (err.code === "NoSuchKey") { // MinIO slang for "This file does not exist"
        //     res.status(404).send("The requested file does not exist");
        // } else {
        //     res.status(500).send();
        // }
    }
});

router.post("/", async (req, res, _next) => {
    // Validate request (maybe move this to a middleware later)
    const { name, imageFile } = req.body;
    if (!name || !imageFile) {
        res.status(400).send();
        return;
    }

    try {
        // Store the base64 image
        await StorageService.storeImageBase64(name, imageFile);
    } catch (err) {
        res.status(500).send();
        return;
    }

    res.status(201).send();
});

module.exports = router;
