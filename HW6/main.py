from flask import Flask, url_for, request, redirect
app = Flask(__name__)


@app.route('/', methods =["GET", "POST"])
def proxy():
   
    return "aloha"




if __name__ == "__main__":
    app.run()