
import threading
import time
from ServerUtils import isServerReady, runServer, SERVER_URL
from ClientUtils import runUi

def runPeriodicCheck(window):
    for x in range(10):
        if isServerReady():
            window.load_url(SERVER_URL)
            return
        else:
            time.sleep(1)
    window.destroy()
    print("Periodic check completed!")


serverThread = threading.Thread(target=runServer)
serverThread.start()
runUi(runPeriodicCheck)
print("App terminated!")