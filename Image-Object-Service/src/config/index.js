require("dotenv").config();
const minio = require("minio");

const minioClient = new minio.Client({
    endPoint: process.env.MINIO_ENDPOINT,
    port: parseInt(process.env.MINIO_PORT, 10),
    useSSL: false,
    accessKey: process.env.MINIO_ACCESS_KEY,
    secretKey: process.env.MINIO_SECRET_KEY,
});

module.exports = {
    port: process.env.PORT,
    minioClient,
    defaultBucket: process.env.MINIO_BUCKET_NAME,
};
