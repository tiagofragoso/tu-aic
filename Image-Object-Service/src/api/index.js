const { Router } = require("express");
const imagesRouter = require("./routes/images");

const app = Router();
app.use("/images", imagesRouter);

module.exports = app;
