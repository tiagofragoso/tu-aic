const loadApi = require("./api");
const loadMinIO = require("./minio");
const loadLogger = require("./logger");

module.exports = async (app) => {
    loadLogger(app); // always load logger before API routes
    loadApi(app);
    await loadMinIO();
    return app;
};
