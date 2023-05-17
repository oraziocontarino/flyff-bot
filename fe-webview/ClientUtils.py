import webview
from LoadingScreen import screen


def runUi(runPeriodicCheck):
    myWindow = webview.create_window(
        'FlyffBot',
        html=screen,
        width=1050,
        height=650,
        resizable=False
    )
    webview.start(runPeriodicCheck, myWindow)
    print("--- WebView Process Terminated!")