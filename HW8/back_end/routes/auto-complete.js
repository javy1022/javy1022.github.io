const TICKETMASTER_API_KEY = "PbITZwAJ32tYq1Codl5AhoRCENI3fPfo"
const TICKETMASTER_HOST = "https://app.ticketmaster.com"
const SUGGEST_PATH = "/discovery/v2/suggest"


var express = require('express');
var axios = require('axios');
var router = express.Router();
/* GET users listing. */
router.get('/search/auto-complete', function(req, res, next) {
  res.header('Access-Control-Allow-Origin', '*'); 
  
  const url_params = {
    apikey: TICKETMASTER_API_KEY,
    keyword: req.query.keyword   
  }

  const instance = axios.create({
    baseURL: TICKETMASTER_HOST,
    timeout: 1000
    });

    instance.get(SUGGEST_PATH , {params: url_params} )
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
