const morgan = require("morgan");

const logger = require("../util/logger");

module.exports = (app) => {
    // log successful requests with "debug" level
    app.use(morgan("tiny", {
        skip: (_req, res) => res.statusCode >= 400,
        stream: {
            write: (str) => { logger.debug(str) },
        },
    }));

    // log unsuccessful requests with "error" level
    app.use(morgan("tiny", {
        skip: (_req, res) => res.statusCode < 400,
        stream: {
            write: (str) => { logger.error(str) },
        },
    }));
    return app;
};
