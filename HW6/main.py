
import requests
from flask import Flask, jsonify
app = Flask(__name__)

@app.route('/')    
def test():
    url = https://api.artsy.net/api/tokens/xapp_token
    api_authenticate = {'client_id': '5263ac83d3faa4643a80', 'client_secret': 'f510da3282c88d7acacb8a4c7a2e613a' }
    x = requests.post(url, api_authenticate)
    return 'x'

if __name__ == "__main__":
   app.run(host='127.0.0.1', port=8080, debug=True)
   
