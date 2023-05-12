import webview
import subprocess
import threading


def runServer():
    subprocess.call(['java', '-jar', 'LocalServer.jar'])


def runUi():
    webview.create_window(
        'FlyffBot',
        'http://localhost:8899',
        width=1050,
        height=650,
        resizable=False
    )
    webview.start()


serverThread = threading.Thread(target=runServer)

serverThread.start()
runUi()
