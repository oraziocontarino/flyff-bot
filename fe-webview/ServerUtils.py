import subprocess
import requests
from multiprocessing import Process

SERVER_URL = 'http://localhost:8899'


def deployServer():
    return subprocess.Popen(['java', '-jar', './server/LocalServer.jar'])

def isServerReady():
    try:
        response = requests.get(SERVER_URL+'/server/health', timeout=3)
        if response.status_code == 200:
            return True
        else:
            return False
    except requests.exceptions.RequestException as e:
        return False
