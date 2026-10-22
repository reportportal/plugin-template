const http = require('http');
const sirv = require('sirv');

const contentPath = './build/public';
const headers = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Methods': '*',
  'Access-Control-Allow-Headers': '*',
  'Cache-Control': 'no-cache, no-store, must-revalidate, max-age=0',
};
const serve = sirv(contentPath, {
  dev: true,
  setHeaders: (res) => {
    Object.entries(headers).forEach(([name, value]) => res.setHeader(name, value));
  },
});

http
  .createServer((req, res) => {
    console.log(`Request URL: ${req.url}`);
    serve(req, res);
  })
  .listen(9090, () => {
    console.log(`Serving files from ${contentPath}`);
  });
