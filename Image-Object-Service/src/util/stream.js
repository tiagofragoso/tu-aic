const concatStream = require("concat-stream");

const streamToBuffer = (stream) => new Promise((resolve, _reject) => {
    const pipe = concatStream((buffer) => resolve(buffer));
    stream.pipe(pipe);
    // TODO: handle errors
});

module.exports = { streamToBuffer };
