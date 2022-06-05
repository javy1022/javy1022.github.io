import requests
from flask import Flask, jsonify, redirect, url_for, json, request
app = Flask(__name__, static_url_path='', static_folder='static')

@app.route('/')    
def home():
   return redirect(url_for('static', filename='index.html'))   

@app.route('/search')  
def hello():
    if request.method == 'POST':    
        tt = request.form['values']
        return 'aloha'
    #func 1   
    url = 'https://api.artsy.net/api/tokens/xapp_token'
    api_authenticate = {'client_id': '5263ac83d3faa4643a80', 'client_secret': 'f510da3282c88d7acacb8a4c7a2e613a' }
    data = requests.post(url, api_authenticate)
    data_toJson = data.json()
    token = (data_toJson["token"])
   
    #func 2
    search_url = 'https://api.artsy.net/api/search'
    result = requests.get(search_url, headers={"X-XAPP-Token": token}, params={'q': 'a', 'size': '10'})
    result_toJson = result.json()
    filtered_result = result_toJson["_embedded"]["results"];
    
      
    title_list = []
    image_list = []
    artist_id_list = []
    for x in filtered_result:
        if(x["og_type"] == 'artist'):
            title_list.append(x["title"] )
            image_list.append(x["_links"]["thumbnail"]["href"])
            
            
            artist_url_string = json.dumps(x["_links"]["self"]["href"])
            artist_id = artist_url_string.split("artists/")[1]
            artist_id = artist_id[:-1]
            artist_id_list.append(artist_id )
            
    
   
    
    #func 3         
    artist_result = []
    for i in range(len(artist_id_list)) :
        artist_url = "https://api.artsy.net/api/artists/{}".format(artist_id_list[i])
        artist_result.append(requests.get(artist_url , headers={"X-XAPP-Token": token}))
        
    artist_name_list = []   
    artist_birthday_list = [] 
    artist_deathday_list = [] 
    artist_nationality_list = [] 
    artist_biography_list = [] 
    
    for i in range(len(artist_result)) :
        artist_result_json = artist_result[i].json()
        artist_name_list.append(artist_result_json["name"])
        artist_birthday_list.append(artist_result_json["birthday"])
        artist_deathday_list.append(artist_result_json["deathday"])   
        artist_nationality_list.append(artist_result_json["nationality"])  
        artist_biography_list.append(artist_result_json["biography"]) 
    
    return jsonify(image_list)
    
@app.route('/search_input', methods=['GET','POST'])  
def search_input():
    if request.method == 'POST':
        test = request.form['input']
        return redirect(url_for('load_image', x=test))
    

@app.route('/load_image/<x>', methods=['GET','POST'])  
def load_image(x):
       #func 1   
        url = 'https://api.artsy.net/api/tokens/xapp_token'
        api_authenticate = {'client_id': '5263ac83d3faa4643a80', 'client_secret': 'f510da3282c88d7acacb8a4c7a2e613a' }
        data = requests.post(url, api_authenticate)
        data_toJson = data.json()
        token = (data_toJson["token"])
      #func 2
        search_url = 'https://api.artsy.net/api/search'
        result = requests.get(search_url, headers={"X-XAPP-Token": token}, params={'q': x, 'size': '10'})
        result_toJson = result.json()
        filtered_result = result_toJson["_embedded"]["results"];
    
      
        image_list = []
        artist_id_list = []
        for x in filtered_result:
            if(x["og_type"] == 'artist'):
                image_list.append(x["_links"]["thumbnail"]["href"])
     
	       
        return jsonify(image_list)

if __name__ == "__main__":
    app.run()
   
