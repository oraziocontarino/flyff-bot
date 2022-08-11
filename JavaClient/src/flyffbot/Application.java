package flyffbot;


import com.formdev.flatlaf.FlatLightLaf;
import flyffbot.gui.FBFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import javax.swing.*;
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
}