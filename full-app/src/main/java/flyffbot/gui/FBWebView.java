package flyffbot.gui;

import com.sun.javafx.webkit.WebConsoleListener;
import flyffbot.server.LocalServer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import netscape.javascript.JSObject;

import java.util.concurrent.TimeUnit;

@Slf4j
public class FBWebView extends Application {
    private static WebEngine webEngine;

    public static void main(String[] args) {
        try {
            log.info("WebView - starting...");
            FBExecutor.getExecutor().schedule(() -> launch(), 0, TimeUnit.SECONDS);
            log.info("Local server - starting...!");
            LocalServer.run(()-> {
                Platform.runLater(() -> webEngine.load("http://localhost:8899"));
                log.info("Local server - started!");
            });
            log.info("WebView - terminated!");
        } catch (Exception e){
            log.info("Error"+e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) {
        var webView = new javafx.scene.web.WebView();
        webEngine = webView.getEngine();
        WebConsoleListener.setDefaultListener((webView2, message, lineNumber, sourceId) -> {
            log.debug("console.log - sourceId={} message=\n{}", sourceId, message);
        });
        webEngine.loadContent(LoadingScreen.HTML);

        // Optional: Listen to the worker's state property to know when the HTML content is fully loaded
        var worker = webEngine.getLoadWorker();
        worker.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                log.info("HTML content loaded successfully!");
            }
        });

        var scene = new Scene(webView, 1050, 650);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("FlyffBot");
        stage.show();
    }
}