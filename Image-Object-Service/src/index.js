const express = require("express");

const loadApp = require("./loaders");
const { port } = require("./config");

const startServer = async () => {

    // Create express app
    const app = express();

    app.get("/health", (_, res) => {
        res.send("I'm alive");
    });

    // Apply loaders
    await loadApp(app);

    // Start listening for requests
    app.listen(port, () => {
        // eslint-disable-next-line no-console
        console.log(`App listening in port ${port}`);
    });
};

startServer();
