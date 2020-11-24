const { Router } = require("express");
const StorageService = require("../../services/storage");
const { get: get_validator, store: store_validator } = require("../middlewares/validators");

const { FileNotFoundError, StorageServiceInternalError } = StorageService.errors;

const router = Router();

// this validator is a fallback as it is rarely called because:
// GET /images/ will not triger /images/:name route
router.get("/:name", get_validator,
    async (req, res, _next) => {
        const { name } = req.params;

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

router.put("/", store_validator,
    async (req, res, _next) => {
        const { name, image_file } = req.body;

        try {
        // Store the base64 image
            await StorageService.storeImageBase64(name, image_file);
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
