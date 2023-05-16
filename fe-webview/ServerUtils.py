import subprocess
import requests

SERVER_URL = 'http://localhost:8899'

def runServer():
    subprocess.call(['java', '-jar', './server/LocalServer.jar'])
    print("LocalServer Process Terminated!")

def isServerReady():
    try:
        response = requests.get(SERVER_URL+'/server/health', timeout=3)
        if response.status_code == 200:
            return True
        else:
            return False
    except requests.exceptions.RequestException as e:
        return False
    print("isServerReady Process Terminated!")