import requests
from flask import Flask, jsonify, redirect, url_for
app = Flask(__name__, static_url_path='', static_folder='static')

@app.route('/')    
def home():
    return redirect(url_for('static', filename='index.html'))   




if __name__ == "__main__":
   app.run(debug=True)
   
