import time
from ServerUtils import isServerReady, deployServer, SERVER_URL
from ClientUtils import runUi

def runPeriodicCheck(window):
    for x in range(10):
        if isServerReady():
            window.load_url(SERVER_URL)
            return
        else:
            time.sleep(1)
    window.destroy()
    print("--- Periodic check completed!")


print("--- Starting local server...")
localServerProcess = deployServer()
print("--- Starting web view...")
runUi(runPeriodicCheck)
print("--- Terminating local server...")
localServerProcess.terminate()
localServerProcess.wait(5)
print("--- App terminated!")