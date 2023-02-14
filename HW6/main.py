import requests
from flask import Flask, jsonify, redirect, url_for, json, request, send_from_directory


app = Flask(__name__)



@app.route('/')
def homepage():
    return app.send_static_file("index.html")


if __name__ == "__main__":
    app.run()