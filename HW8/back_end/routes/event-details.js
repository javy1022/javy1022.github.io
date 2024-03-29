const TICKETMASTER_API_KEY = "PbITZwAJ32tYq1Codl5AhoRCENI3fPfo";
const TICKETMASTER_HOST = "https://app.ticketmaster.com";
const EVENT_DETAIL_PATH = "/discovery/v2/events/";

var express = require("express");
var axios = require("axios");
var router = express.Router();

router.get("/search/event-details/:id", function (req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");

  const event_id = req.params.id;
  const url_params = {
    apikey: TICKETMASTER_API_KEY,
  };

  const request_url = TICKETMASTER_HOST + EVENT_DETAIL_PATH + event_id;
  const instance = axios.create({
    baseURL: request_url,
  });

  instance
    .get("", { params: url_params })
    .then(function (response) {
      return res.send(response.data);
    })
    .catch(function (error) {
      if (error.response && error.response.data) {
        return res.send(error.response.data);
      } else {
        console.error(error);
        return res.status(500).send("Unknown error occurred");
      }
    });
});

module.exports = router;
