import requests
   
def main():
    url = 'https://api.artsy.net/api/tokens/xapp_token'
    api_authenticate = {'client_id': '5263ac83d3faa4643a80', 'client_secret': 'f510da3282c88d7acacb8a4c7a2e613a' }
    x = requests.post(url, api_authenticate)
    print(x.text)

if __name__ == "__main__":
   main()
   
