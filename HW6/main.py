import requests
from flask import Flask, jsonify, redirect, url_for, json, request, send_from_directory

TICKETMASTER_API_KET = "PbITZwAJ32tYq1Codl5AhoRCENI3fPfo"

MUSIC_SEGMENT_ID = "KZFzniwnSyZfZ7v7nJ"
SPORTS_SEGMENT_ID  = "KZFzniwnSyZfZ7v7nE"
ARTS_THEATRE_SEGMENT_ID   = "KZFzniwnSyZfZ7v7na"
FILM_SEGMENT_ID  = "KZFzniwnSyZfZ7v7nn"
MISCELLANEOUS_SEGMENT_ID = "KZFzniwnSyZfZ7v7n1"
DISTANCE_UNIT = "miles"

app = Flask(__name__)



@app.route('/')
def homepage():
    return app.send_static_file("index.html")

@app.route('/<string:keyword>/<string:lat_string>/<string:lng_string>/<string:category>/<int:distance>')
def get_search_result(keyword,lat_string,lng_string,category,distance):
    return str(distance)

if __name__ == "__main__":
    app.run()