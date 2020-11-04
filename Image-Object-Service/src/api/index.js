const { Router } = require("express");
const imageRoutes = require("./routes/images");

module.exports = () => {
    const app = Router();

    app.use("/images", imageRoutes);

    return app;
};
