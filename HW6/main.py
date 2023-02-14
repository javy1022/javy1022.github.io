import requests
from flask import Flask, jsonify, redirect, url_for, json, request, send_from_directory
from geolib import geohash as gh

TICKETMASTER_API_KEY = "PbITZwAJ32tYq1Codl5AhoRCENI3fPfo"
TICKETMASTER_HOST = "https://app.ticketmaster.com"
EVENT_SEARCH_PATH = "/discovery/v2/events.json"

MUSIC_SEGMENT_ID = "KZFzniwnSyZfZ7v7nJ"
SPORTS_SEGMENT_ID  = "KZFzniwnSyZfZ7v7nE"
ARTS_THEATRE_SEGMENT_ID   = "KZFzniwnSyZfZ7v7na"
FILM_SEGMENT_ID  = "KZFzniwnSyZfZ7v7nn"
MISCELLANEOUS_SEGMENT_ID = "KZFzniwnSyZfZ7v7n1"
DISTANCE_UNIT = "miles"

GEOHASH_PRECISION = 7

#https://app.ticketmaster.com/discovery/v2/events.json?apikey=PbITZwAJ32tYq1Codl5AhoRCENI3fPfo&keyword=University+of+California&segmentID=KZFzniwnSyZfZ7v7nE&radius=10&units=miles&geoPoint=9q5cs
app = Flask(__name__)



@app.route('/')
def homepage():
    return app.send_static_file("index.html")

@app.route('/<string:keyword>/<string:lat_string>/<string:lng_string>/<string:category>/<string:distance>')
def get_search_result(keyword,lat_string,lng_string,category,distance):
    request_url = TICKETMASTER_HOST + EVENT_SEARCH_PATH
    geohash = gh.encode(lat_string, lng_string, GEOHASH_PRECISION)
    """
    url_params = {	'apikey': TICKETMASTER_API_KEY,
                    'keyword': keyword,
					'segmentId': segment_id,
                    'radius': str(distance),
                    'unit': DISTANCE_UNIT,
                    'geoPoint': geohash
				 }
    
    if category == "Default":
         url_params["segmentId"] = ""
     
    elif category == "Music": 
         url_params["segmentId"] = MUSIC_SEGMENT_ID
    
    elif category == "Arts & Theatre": 
         url_params["segmentId"] = ARTS_THEATRE_SEGMENT_ID
    
    elif category == "Film": 
         url_params["segmentId"] = FILM_SEGMENT_ID
    
    elif category == "Miscellaneous": 
         url_params["segmentId"] = MISCELLANEOUS_SEGMENT_ID        
        
  
    response = requests.get(url, params=url_params)
    response_json = response.json()
    return  response_json
    """
    return str(geohash)

if __name__ == "__main__":
    app.run()