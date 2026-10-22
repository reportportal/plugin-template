const http = require('http');
const path = require('path');
const sirv = require('sirv');

const contentPath = path.join(__dirname, 'build/public');
const assets = sirv(contentPath, {
  dev: true,
  maxAge: 0,
  setHeaders: (res) => {
    res.setHeader('Access-Control-Allow-Origin', '*');
  },
});

const server = http.createServer((req, res) => {
  console.log(`Request URL: ${req.url}`);
  assets(req, res, () => {
    res.statusCode = 404;
    res.end('Not found');
  });
});

server.listen(9090, () => {
  console.log(`Serving files from ${contentPath}`);
});
