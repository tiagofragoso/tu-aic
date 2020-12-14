class Base64Service {
    constructor() {}

    static fromBuffer(buf) {
        return buf.toString("base64");
    }

    static toBuffer(str) {
        return new Buffer.from(str, "base64");
    }

}

module.exports = Base64Service;
