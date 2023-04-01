var express = require("express");
var path = require("path");
var cookieParser = require("cookie-parser");
var logger = require("morgan");
const cors = require("cors");

var event_search_router = require("./routes/event-search");
var auto_complete_router = require("./routes/auto-complete");
var event_details_router = require("./routes/event-details");
var artists_info_spotify_router = require("./routes/artists-info-spotify");
var venue_detail_router = require("./routes/venue-detail");

var app = express();
app.use(cors());

app.use(logger("dev"));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, "public/dist/hw8")));

app.use("/", event_search_router);
app.use("/", auto_complete_router);
app.use("/", event_details_router);
app.use("/", artists_info_spotify_router);
app.use("/", venue_detail_router);

app.get("*", (req, res) => {
  res.sendFile(path.join(__dirname, "public/dist/hw8/index.html"));
});

module.exports = app;
