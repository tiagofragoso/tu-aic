const loadApi = require("./api");
const loadMinIO = require("./minio");

module.exports = async (app) => {
    loadApi(app);
    await loadMinIO();
    return app;
};
