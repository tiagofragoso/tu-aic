const { body, param, validationResult } = require("express-validator");

// Automatically run validators in order to have a standardized error response
const useValidators = (validators) => async (req, res, next) => {
    await Promise.all(validators.map((validator) => validator.run(req)));

    const errors = validationResult(req);

    // If no errors exist, proceed
    if (errors.isEmpty()) {
        return next();
    }

    // Otherwise, return 422 with the error array
    return res.status(422).json(errors.array());
};

const store = useValidators([
    body("name")
        .exists({ checkNull: true, checkFalsy: true }) // make sure empty strings are not accepted
        .withMessage("'name' field must be provided")
        .bail(),
    body("image_file")
        .exists({ checkNull: true, checkFalsy: true }) // make sure empty strings are not accepted
        .withMessage("'image_file' must be provided (in base64)")
        .bail(),
]);

const get = useValidators([
    param("name")
        .exists({ checkNull: true, checkFalsy: true }) // make sure empty strings are not accepted
        .withMessage("Image name must be provided")
        .bail(),
]);

module.exports = { store, get };
