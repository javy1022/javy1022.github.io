import requests
from flask import Flask, jsonify, redirect, url_for, json, request
app = Flask(__name__)

@app.route('/')    
def home():
   return redirect(url_for('static', filename='index.html'))   
    
@app.route('/search_input', methods=['GET','POST'])  
def search_input():
    if request.method == 'POST':
        test = request.form['input']
        return redirect(url_for('load_image', x=test))
    

@app.route('/load_image/<x>', methods=['GET','POST'])  
def load_image(x):
    
        return "aloha"
        
    
@app.route('/search_title', methods=['GET','POST'])  
def search_title():
    if request.method == 'POST':
        test = request.form['input']
        return redirect(url_for('title', x=test))
        

@app.route('/detail_title', methods=['GET','POST'])  
def detail_title():
    if request.method == 'POST':
        test = request.form['input']
        test1 = request.form['input1']
        return redirect(url_for('get', x=test, y=test1))
        
@app.route('/get/<x>/<y>', methods=['GET','POST'])  
def get(x,y):
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
        
    artist_id_list = []
    for x in filtered_result:
        if(x["og_type"] == 'artist'):
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
    for i in range(len(artist_result)) :
        artist_result_json = artist_result[i].json()
        artist_name_list.append(artist_result_json["name"])
        
    return  jsonify(artist_name_list);
            
        



@app.route('/title/<x>',  methods=['GET','POST'])  
def title(x):
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
    
    title_list = []
    for x in filtered_result:
        if(x["og_type"] == 'artist'):
            title_list.append(x["title"] )
            
    return jsonify(title_list) 
    
    
    
@app.route('/detail_nation', methods=['GET','POST'])  
def detail_nation():
    if request.method == 'POST':
        test = request.form['input']
        test1 = request.form['input1']
        return redirect(url_for('nation', x=test, y=test1))
        
@app.route('/nation/<x>/<y>', methods=['GET','POST'])  
def nation(x,y):
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
        
    artist_id_list = []
    for x in filtered_result:
        if(x["og_type"] == 'artist'):
            artist_url_string = json.dumps(x["_links"]["self"]["href"])
            artist_id = artist_url_string.split("artists/")[1]
            artist_id = artist_id[:-1]
            artist_id_list.append(artist_id )
            
    #func 3         
    artist_result = []
    for i in range(len(artist_id_list)) :
        artist_url = "https://api.artsy.net/api/artists/{}".format(artist_id_list[i])
        artist_result.append(requests.get(artist_url , headers={"X-XAPP-Token": token}))
    
    artist_nationality_list = []   
    for i in range(len(artist_result)) :
        artist_result_json = artist_result[i].json()
        artist_nationality_list.append(artist_result_json["nationality"])
        
    return  jsonify(artist_nationality_list);
            
@app.route('/detail_bio', methods=['GET','POST'])  
def detail_bio():
    if request.method == 'POST':
        test = request.form['input']
        test1 = request.form['input1']
        return redirect(url_for('bio', x=test, y=test1))
        
@app.route('/bio/<x>/<y>', methods=['GET','POST'])  
def bio(x,y):
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
        
    artist_id_list = []
    for x in filtered_result:
        if(x["og_type"] == 'artist'):
            artist_url_string = json.dumps(x["_links"]["self"]["href"])
            artist_id = artist_url_string.split("artists/")[1]
            artist_id = artist_id[:-1]
            artist_id_list.append(artist_id )
            
    #func 3         
    artist_result = []
    for i in range(len(artist_id_list)) :
        artist_url = "https://api.artsy.net/api/artists/{}".format(artist_id_list[i])
        artist_result.append(requests.get(artist_url , headers={"X-XAPP-Token": token}))
    
    artist_biography_list = []   
    for i in range(len(artist_result)) :
        artist_result_json = artist_result[i].json()
        artist_biography_list.append(artist_result_json["biography"])
        
    return jsonify(artist_biography_list); 
    
@app.route('/output_title', methods=['GET','POST'])  
def output_title():
    test = request.form.get('input')
    return jsonify(test) ;
    
if __name__ == "__main__":
     app.run()
   
