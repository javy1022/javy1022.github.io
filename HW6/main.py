import requests
from flask import Flask, jsonify, redirect, url_for, json, request

app = Flask(__name__)


api_key = "P5t7BttvGrXWg8bb79sSf_HpQ_B-S2HShUbc8nwlp4I3DFaPKVpuXMuo3sIhELcn-xxnPpLuilHqnfaQLXwU_DZ4tALO0UZ0Wd9j3YLSEp89rb6SEUxwDFwckOgvY3Yx"
API_HOST = "https://api.yelp.com/v3"
BUSINESS_SEARCH_PATH = "/businesses/search" 

headers = {
'Authorization': 'Bearer %s' % api_key,
}

@app.route('/')
def homepage():
    return app.send_static_file("index.html")


@app.route('/<string:keyword>/<string:location>/<string:category>/<int:distance>')
def get_keyword(keyword,location,category,distance):
    url =  API_HOST + BUSINESS_SEARCH_PATH
    headers = {'Authorization': 'Bearer %s' % api_key,}
    
    url_params = {	'term': keyword,
					'location': location,	
					'category': category,				
					'radius': distance
					}
    response = requests.get(url, headers=headers, params=url_params)
    response_json = response.json()
    #response_dump = json.dumps(response_json)
    
    
    
    return  response_json
    #return response_json
    
if __name__ == "__main__":
    app.run()