const express = require("express");

const loadApp = require("./loaders");
const { port } = require("./config");

const startServer = async () => {

    // Create express app
    const app = express();

    // Apply loaders
    await loadApp(app);

    // Start listening for requests
    app.listen(port, () => {
        console.log(`Example app listening at- http://localhost:${port}`);
    });
};

startServer();
