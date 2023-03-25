const SPOTIFY_CLIENT_ID = "55146167ec8b49a2a282a1ad573fadb6";
const SPOTIFY_CLIENT_SECRET = "50a824eef3cb4248a39601bf13f42898";

var express = require("express");
var axios = require("axios");
var router = express.Router();

var SpotifyWebApi = require("spotify-web-api-node");

var spotifyApi = new SpotifyWebApi({
  clientId: SPOTIFY_CLIENT_ID,
  clientSecret: SPOTIFY_CLIENT_SECRET,
});

spotifyApi.clientCredentialsGrant().then(
  function (data) {
    console.log("The access token expires in " + data.body["expires_in"]);
    console.log("The access token is " + data.body["access_token"]);

    // Save the access token so that it's used in future calls
    spotifyApi.setAccessToken(data.body["access_token"]);
  },
  function (err) {
    console.log("Something went wrong when retrieving an access token", err);
  }
);

router.get("/search/artists", function (req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");

  const query = req.query.q;

  spotifyApi
    .searchArtists(query)
    .then(function (response) {
      return res.send(response.body);
    })
    .catch(function (error) {
      return res.status(429).json({ error: "Too Many Requests" });
    });
});

module.exports = router;
