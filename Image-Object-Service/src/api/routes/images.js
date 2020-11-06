const { Router } = require("express");
const StorageService = require("../../services/storage");
const Base64Service = require("../../services/base64");
const { streamToBuffer } = require("../../util/stream");

const router = Router();

router.get("/:name", async (req, res, _next) => {
    // Validate request (maybe move this to a middleware later)
    const { name } = req.params;
    if (!name) {
        res.status(400).send();
        return;
    }

    try {
        // Get the stream from the StorageService
        const stream = await StorageService.getImage(name);

        // Read the full stream and save contents to a Buffer
        const buffer = await streamToBuffer(stream);

        // Encode the Buffer contents to base64
        const b64_image = Base64Service.fromBuffer(buffer);

        res.send({ image_file: b64_image });
    } catch (err) {
        if (err.code === "NoSuchKey") { // MinIO slang for "This file does not exist"
            res.status(404).send("The requested file does not exist");
        } else {
            res.status(500).send();
        }
    }
});

router.post("/", async (req, res, _next) => {
    // Validate request (maybe move this to a middleware later)
    const { name, image_file } = req.body;
    if (!name || !image_file) {
        res.status(400).send();
        return;
    }

    // Create a binary Buffer from the base64 string
    const imageBuffer = Base64Service.toBuffer(image_file);

    try {
        // Store the contents of the Buffer
        await StorageService.storeImage(name, imageBuffer);
    } catch (err) {
        console.error(err);
        res.status(500).send();
        return;
    }

    res.status(201).send();
});

module.exports = router;
