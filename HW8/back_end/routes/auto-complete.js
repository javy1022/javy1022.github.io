const TICKETMASTER_API_KEY = "PbITZwAJ32tYq1Codl5AhoRCENI3fPfo";
const TICKETMASTER_HOST = "https://app.ticketmaster.com";
const SUGGEST_PATH = "/discovery/v2/suggest";

var express = require("express");
var axios = require("axios");
var router = express.Router();

// Create a rate limiter that allows 5 requests per second, with a burst limit of 1 message
const rateLimit = require("express-rate-limit");
const limiter = rateLimit({
  windowMs: 1000, 
  max: 5,
  maxConcurrency: 1,
  handler: function (req, res, next) {
    return res.status(429).json({ error: "Too many requests, please try again later." });
  },
});

/* GET users listing. */
router.get("/search/auto-complete", limiter, function (req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");

  const url_params = {
    apikey: TICKETMASTER_API_KEY,
    keyword: req.query.keyword,
  };

  const instance = axios.create({
    baseURL: TICKETMASTER_HOST,
    timeout: 1000,
  });

  instance
    .get(SUGGEST_PATH, { params: url_params })
    .then(function (response) {
      return res.send(response.data);
    })
    .catch(function (error) {
      return res.status(429).json({ error: "Too Many Requests" });
    });
});

module.exports = router;
