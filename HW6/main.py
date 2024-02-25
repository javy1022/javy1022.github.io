import requests
from flask import Flask
from geolib import geohash as gh

TICKETMASTER_API_KEY = "your api key"
TICKETMASTER_HOST = "https://app.ticketmaster.com"
EVENT_SEARCH_PATH = "/discovery/v2/events.json"
EVENT_DETAIL_PATH = "/discovery/v2/events/"
VENUE_DETAIL_PATH = "/discovery/v2/venues"

MUSIC_SEGMENT_ID = "KZFzniwnSyZfZ7v7nJ"
SPORTS_SEGMENT_ID  = "KZFzniwnSyZfZ7v7nE"
ARTS_THEATRE_SEGMENT_ID   = "KZFzniwnSyZfZ7v7na"
FILM_SEGMENT_ID  = "KZFzniwnSyZfZ7v7nn"
MISCELLANEOUS_SEGMENT_ID = "KZFzniwnSyZfZ7v7n1"
DISTANCE_UNIT = "miles"

GEOHASH_PRECISION = 7

app = Flask(__name__)

@app.route('/')
def homepage():
    return app.send_static_file("index.html")

@app.route('/<string:keyword>/<string:lat_string>/<string:lng_string>/<string:category>/<string:distance>')
def get_search_result(keyword,lat_string,lng_string,category,distance):
    request_url = TICKETMASTER_HOST + EVENT_SEARCH_PATH
    geohash = gh.encode(lat_string, lng_string, GEOHASH_PRECISION)
    
    url_params = {	'apikey': TICKETMASTER_API_KEY,
                    'keyword': keyword,
				'segmentId': "",
                    'radius': distance,
                    'unit': DISTANCE_UNIT,
                    'geoPoint': geohash
				 }
         
    if category == "Music": 
         url_params["segmentId"] = MUSIC_SEGMENT_ID
    
    elif category == "Sports": 
         url_params["segmentId"] = SPORTS_SEGMENT_ID
   
    elif category == "Arts & Theatre": 
         url_params["segmentId"] = ARTS_THEATRE_SEGMENT_ID
    
    elif category == "Film": 
         url_params["segmentId"] = FILM_SEGMENT_ID
    
    elif category == "Miscellaneous": 
         url_params["segmentId"] = MISCELLANEOUS_SEGMENT_ID        
      
  
    response = requests.get(request_url, params=url_params)
    response_json = response.json()
    return  response_json
    
@app.route('/event-detail/<string:id>')
def get_event_detail(id):
     request_url =  TICKETMASTER_HOST + EVENT_DETAIL_PATH + id;
     url_params = {'apikey': TICKETMASTER_API_KEY}
     response = requests.get(request_url, params=url_params)
     response_json = response.json()
     return  response_json

@app.route('/venue-detail/<string:venue>')
def get_venue_detail(venue):
     request_url =  TICKETMASTER_HOST + VENUE_DETAIL_PATH;
     url_params = {'apikey': TICKETMASTER_API_KEY, 'keyword': venue}
     response = requests.get(request_url, params=url_params)
     response_json = response.json()
     return  response_json


if __name__ == "__main__":
    app.run()