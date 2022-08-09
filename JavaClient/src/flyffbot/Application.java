package flyffbot;


import com.formdev.flatlaf.FlatLightLaf;
import flyffbot.gui.FBFrame;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@SpringBootApplication
@ComponentScan(basePackages = "flyffbot")
@Slf4j
public class Application {
    @Autowired
    private static FBFrame fbFrame;

    public static void main(String[] args) {
        try {
            log.debug("tempFolder: {}", System.getProperty("java.io.tmpdir"));
            initNativeApi();
            UIManager.setLookAndFeel(new FlatLightLaf());
        }catch (Exception e){
            log.debug("Error while setting material theme: ", e);
        }
        new SpringApplicationBuilder(Application.class).headless(false).run(args);
    }

    @Bean
    public static ScheduledExecutorService buildScheduler(){
        return Executors.newScheduledThreadPool(20);
    }

    private static void initNativeApi(){
        try(val inputStream = Application.class.getClassLoader().getResourceAsStream("main.exe")){
            val path = System.getProperty("java.io.tmpdir");
            val tmp = Paths.get(path, "flyff-bot-native-api.exe").toAbsolutePath();
            assert inputStream != null;
            Files.copy(inputStream, tmp, StandardCopyOption.REPLACE_EXISTING);
            IOUtils.closeQuietly(inputStream);
            log.debug("Flyff bot native api copied to: {}", tmp.toAbsolutePath());
        }catch (Exception e){
            log.error("Error occurred while coping native api to temp folder", e);
        }
    }
}