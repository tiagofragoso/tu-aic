const loadApi = require("./api");
const loadMinIO = require("./minio");
const loadLogger = require("./logger");

module.exports = async (app) => {
    loadLogger(app); // always load logger before API routes
    loadApi(app);
    try {
        await loadMinIO();
    } catch (err) {
        console.error("MinIO fatal error");
        console.error(err);
        process.exit(-1);
    }
    return app;
};
