package flyffbot;


import com.formdev.flatlaf.FlatLightLaf;
import flyffbot.configuration.DepInjComponentData;
import flyffbot.gui.FBFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

import javax.swing.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@SpringBootApplication
@ComponentScan(basePackages = "flyffbot")
@Slf4j
public class Application {
    @Value("${release.title}")
    private String title;

    @Value("${pipe-config.min}")
    private int minPipes;

    @Value("${pipe-config.max}")
    private int maxPipes;

    @Value("${auto-save.folder-name}")
    private String folderName;

    @Autowired
    private DepInjComponentData services;

    private static FBFrame fbFrame;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            fbFrame = new FBFrame();
            fbFrame.showLoader();
        }catch (Exception e){
            log.debug("Error while setting material theme: ", e);
        }
        new SpringApplicationBuilder(Application.class).headless(false).run(args);
    }

    @Bean
    public static ScheduledExecutorService buildScheduler(){
        return Executors.newScheduledThreadPool(20);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void afterSpringBootStartUp(){
        fbFrame.initFrame(title, minPipes, maxPipes, folderName, services);
    }
}