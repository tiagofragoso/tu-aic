const routes = require("../api");

module.exports = (app) => {
    app.use(routes);
    return app;
};
