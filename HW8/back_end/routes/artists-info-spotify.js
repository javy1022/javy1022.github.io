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
/* Reference source: https://github.com/thelinmichael/spotify-web-api-node */
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

function refresh_access_token() {
  return spotifyApi.clientCredentialsGrant().then(
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
}

router.get("/search/artists", function (req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");

  const query = req.query.q;

  function searchArtists() {
    spotifyApi
      .searchArtists(query)
      .then(function (response) {
        return res.send(response.body);
      })
      .catch(function (error) {
        if (error.statusCode === 401 && error.message.includes("The access token expired")) {
          refresh_access_token().then(() => searchArtists());
        } else {
          return res.status(error.statusCode).json({ error: error.message });
        }
      });
  }

  searchArtists();
});

router.get("/search/artists-id/:artist_id", function (req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");

  const artist_id = req.params.artist_id;

  function getArtistAlbums() {
    spotifyApi
      .getArtistAlbums(artist_id, { limit: 3 })
      .then(function (response) {
        return res.send(response.body);
      })
      .catch(function (error) {
        if (error.statusCode === 401 && error.message.includes("The access token expired")) {
          refresh_access_token().then(() => getArtistAlbums());
        } else {
          return res.status(error.statusCode).json({ error: error.message });
        }
      });
  }

  getArtistAlbums();
});

module.exports = router;
