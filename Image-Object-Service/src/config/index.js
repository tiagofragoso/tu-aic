// .env configuration etc.
const minio = require("minio");

const minioClient = new minio.Client({
    endPoint: "minio",
    port: 9000,
    useSSL: false,
    accessKey: "minio",
    secretKey: "minio123",
});

module.exports = {
    port: 8000,
    minioClient,
};
