const bodyParser = require("body-parser");
const routes = require("../api");

module.exports = (app) => {
    app.use(bodyParser.json({ limit: "50mb" }));
    app.use(routes);
    return app;
};
