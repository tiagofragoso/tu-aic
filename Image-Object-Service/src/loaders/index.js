const loadApi = require("./api");
const loadMinIO = require("./minio");
const loadLogger = require("./logger");
const logger = require("../util/logger");

module.exports = async (app) => {
    loadLogger(app); // always load logger before API routes
    loadApi(app);
    try {
        await loadMinIO();
    } catch (err) {
        logger.error("MinIO fatal error");
        logger.error(err);
        process.exit(-1);
    }
    return app;
};
