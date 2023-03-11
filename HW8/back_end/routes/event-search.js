const TICKETMASTER_API_KEY = "PbITZwAJ32tYq1Codl5AhoRCENI3fPfo"
const TICKETMASTER_HOST = "https://app.ticketmaster.com"
const EVENT_SEARCH_PATH = "/discovery/v2/events.json"
const EVENT_DETAIL_PATH = "/discovery/v2/events/"
const VENUE_DETAIL_PATH = "/discovery/v2/venues"

const MUSIC_SEGMENT_ID = "KZFzniwnSyZfZ7v7nJ"
const SPORTS_SEGMENT_ID  = "KZFzniwnSyZfZ7v7nE"
const ARTS_THEATRE_SEGMENT_ID   = "KZFzniwnSyZfZ7v7na"
const FILM_SEGMENT_ID  = "KZFzniwnSyZfZ7v7nn"
const MISCELLANEOUS_SEGMENT_ID = "KZFzniwnSyZfZ7v7n1"
const DISTANCE_UNIT = "miles"
const GEOHASH_PRECISION = 7


var express = require('express');
var axios = require('axios');
var router = express.Router();
var geohash = require('ngeohash');

router.get('/search/event-search', function(req, res, next) {
  res.header('Access-Control-Allow-Origin', '*');   
  let gh = geohash.encode(req.query.lat, req.query.lng, GEOHASH_PRECISION);

  const url_params = {
	apikey: TICKETMASTER_API_KEY,
	keyword: req.query.keyword,
	segmentId: "",
	radius: req.query.distance,
	unit: DISTANCE_UNIT,
	geoPoint: gh
  }

  if (req.query.category == "Music") {
	url_params["segmentId"] = MUSIC_SEGMENT_ID;
	} else if (req.query.category == "Sports") {
	url_params["segmentId"] = SPORTS_SEGMENT_ID;
	} else if (req.query.category == "Arts & Theatre") {
	url_params["segmentId"] = ARTS_THEATRE_SEGMENT_ID;
	} else if (req.query.category == "Film") {
	url_params["segmentId"] = FILM_SEGMENT_ID;
	} else if (req.query.category == "Miscellaneous") {
	url_params["segmentId"] = MISCELLANEOUS_SEGMENT_ID;
	}


  const instance = axios.create({
	baseURL: TICKETMASTER_HOST,
	timeout: 1000
  });


 instance.get(EVENT_SEARCH_PATH, {params: url_params} )
	.then(function (response) {
		//console.log("Ok");
		//console.log(response.data);
		//return res.send(response.data);
		return res.send(response.data);
	})
	.catch(function (error) {
		//console.log("Error");
	    //console.log(error.response.data);
		return res.send(error.response.data);
	});
});

module.exports = router;
