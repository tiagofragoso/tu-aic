const express = require("express");
const minio = require("minio");
const app = express();
const port = 8000;

const minoClient = new minio.Client({
    endPoint: "minio",
    port: 9000,
    useSSL: false,
    accessKey: "minio",
    secretKey: "minio123",
});

app.get("/", async (req, res) => {
    try {
        const buckets = await minoClient.listBuckets();
        res.send(JSON.stringify(buckets));
    } catch (err) {
        console.error(err);
        res.send("Error fetching buckets. Check the console");
    }
});

app.listen(port, () => {
    console.log(`Example app listening at- http://localhost:${port}`);
});
