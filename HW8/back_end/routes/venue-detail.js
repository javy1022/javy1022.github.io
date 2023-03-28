const TICKETMASTER_API_KEY = "PbITZwAJ32tYq1Codl5AhoRCENI3fPfo";
const TICKETMASTER_HOST = "https://app.ticketmaster.com";
const VENUE_DETAIL_PATH = "/discovery/v2/venues/"

var express = require("express");
var axios = require("axios");
var router = express.Router();

router.get("/search/venue-detail", function (req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
         
    const url_params = {
      keyword: req.query.venue_name,
      apikey: TICKETMASTER_API_KEY,      
    };
  
    const request_url = TICKETMASTER_HOST + VENUE_DETAIL_PATH;
    const instance = axios.create({
      baseURL: request_url,
      timeout: 1000,
    });
  
    instance
      .get('', { params: url_params })
      .then(function (response) {
        console.log('Response data:', response.data);
        return res.send(response.data);
      })
      .catch(function (error) {
        if (error.response && error.response.data) {
          return res.send(error.response.data);
          } else {
          console.error(error);
          return res.status(500).send('Unknown error occurred');
          }
      });
  
  });

module.exports = router;