import webview


def runUi(runPeriodicCheck):
    myWindow = webview.create_window(
        'FlyffBot',
        'index.html',
        width=1050,
        height=650,
        resizable=False
    )
    webview.start(runPeriodicCheck, myWindow)
    print("WebView Process Terminated!")