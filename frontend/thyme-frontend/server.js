// https://medium.com/@greg.farrow1/nextjs-https-for-a-local-dev-server-98bb441eabd7
const { createServer } = require("https");
const { parse } = require("url");
const next = require("next");
const fs = require("fs");
const { default: sslRedirect } = require("heroku-ssl-redirect");
const dev = process.env.NODE_ENV !== "production";
const app = next({ dev });
const handle = app.getRequestHandler();
const httpsOptions = {
  key: fs.readFileSync("../../util/frontend-certs/selfsigned.key"),
  cert: fs.readFileSync("../../util/frontend-certs/selfsigned.crt"),
};
app.prepare().then(() => {
  createServer(httpsOptions, (req, res) => {
    const parsedUrl = parse(req.url, true);
    handle(req, res, parsedUrl);
  }).use(sslRedirect).listen(3000, (err) => {
    if (err) throw err;
    console.log("> Server started on https://localhost:3000");
  });
});