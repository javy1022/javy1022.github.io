import requests
from flask import Flask, jsonify, redirect, url_for, json, request

app = Flask(__name__)


yelp_api_key = "P5t7BttvGrXWg8bb79sSf_HpQ_B-S2HShUbc8nwlp4I3DFaPKVpuXMuo3sIhELcn-xxnPpLuilHqnfaQLXwU_DZ4tALO0UZ0Wd9j3YLSEp89rb6SEUxwDFwckOgvY3Yx"
YELP_API_HOST = "https://api.yelp.com/v3"
BUSINESS_SEARCH_PATH = "/businesses/search" 

yelp_api_headers = {
'Authorization': 'Bearer %s' % yelp_api_key,
}

@app.route('/')
def homepage():
    return app.send_static_file("index.html")


@app.route('/<string:keyword>/<string:lat_string>/<string:lng_string>/<string:location>/<string:category>/<int:distance>')
def get_keyword(keyword,lat_string,lng_string,location,category,distance):
    url =  YELP_API_HOST + BUSINESS_SEARCH_PATH
    yelp_api_headers = {'Authorization': 'Bearer %s' % yelp_api_key,}
    url_params = {	'term': keyword,
					'latitude': float(lat_string),
                    'longitude': float(lng_string),
					'categories': category,				
					'radius': distance
					}
    
    
    if category == "Default":
         url_params["categories"] = "all"
     
    elif category == "Food": 
         url_params["categories"] = "food"
    
    elif category == "Arts & Entertainment": 
         url_params["categories"] = "arts"  
    
    elif category == "Health & Medical": 
         url_params["categories"] = "health" 
    
    elif category == "Hotels & Travel": 
         url_params["categories"] = "hotelstravel"          
    
    elif category == "Professional Services": 
         url_params["categories"] = "professional"       
  
    response = requests.get(url, headers=yelp_api_headers, params=url_params)
    response_json = response.json()
    return  response_json
 
    
if __name__ == "__main__":
    app.run()