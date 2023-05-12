package flyffbot;


import flyffbot.exceptions.SaveLoadException;
import flyffbot.services.EventsServiceImpl;
import flyffbot.services.KeyDownHookService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.hibernate.engine.query.spi.HQLQueryPlan;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@ComponentScan(basePackages = "flyffbot")
@Slf4j
@SpringBootApplication
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
    private KeyDownHookService keyDownHookService;

    @Autowired
    private EventsServiceImpl eventsService;

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class).headless(false).run(args);
    }

    @Bean
    public static ScheduledExecutorService buildScheduler(){
        return Executors.newScheduledThreadPool(20);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void afterSpringBootStartUp() {
        LogManager.getLogger(HQLQueryPlan.class);
        log.info("Native API - Initializing...");
        initNativeApi();
        log.info("Native API - Initialization completed!");

        log.info("Hotkeys Scheduler - Initializing...");
        eventsService.initHotkeysScheduler();
        log.info("Hotkeys Scheduler - Initialization completed!");

        log.info("Hotkeys Scheduler - Initializing...");
        eventsService.initHotkeysScheduler();
        log.info("Hotkeys Scheduler - Initialization completed!");
    }

    private void initNativeApi(){
        val absDirectories = Paths.get(folderName).toAbsolutePath().toString();

        // Check / Create directories to cfg file
        val directories = new File(absDirectories);
        if(!directories.exists() && !directories.mkdirs()){
            throw new SaveLoadException("Save - Unable to find/create directories to: "+directories.getAbsolutePath());
        }

        try(val inputStream = Application.class.getClassLoader().getResourceAsStream("main.exe")){
            val tmp = Paths.get(folderName, "flyff-bot-native-api.exe").toAbsolutePath();
            assert inputStream != null;
            Files.copy(inputStream, tmp, StandardCopyOption.REPLACE_EXISTING);
            IOUtils.closeQuietly(inputStream);
            log.debug("Flyff bot native api copied to: {}", tmp.toAbsolutePath());
        }catch (Exception e){
            log.error("Error occurred while coping native api to temp folder", e);
        }
    }
}