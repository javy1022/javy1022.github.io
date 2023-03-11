var express = require('express');
var axios = require('axios');
var router = express.Router();
/* GET users listing. */
router.get('/search/auto-complete', function(req, res, next) {
  res.send('respond with a resource');
});

module.exports = router;
