var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
const cors = require('cors');

var event_search_router = require('./routes/event-search');
var auto_complete_router = require('./routes/auto-complete');

var app = express();
app.use(cors());

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', event_search_router );
app.use('/', auto_complete_router);

module.exports = app;
