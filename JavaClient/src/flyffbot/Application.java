package flyffbot;


import flyffbot.exceptions.SaveLoadException;
import flyffbot.services.EventsServiceImpl;
import flyffbot.services.KeyDownHookService;
import flyffbot.services.SocketMessageSenderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.hibernate.engine.query.spi.HQLQueryPlan;
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
import java.util.concurrent.TimeUnit;

@ComponentScan(basePackages = "flyffbot")
@Slf4j
@SpringBootApplication
public class Application {
    @Value("${auto-save.folder-name}")
    private String folderName;

    @Autowired
    private KeyDownHookService keyDownHookService;

    @Autowired
    private EventsServiceImpl eventsService;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;
    @Autowired
    private SocketMessageSenderServiceImpl socketMessageSenderService;

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class).headless(false).run(args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void afterSpringBootStartUp() {
        log.info("Native API - Initializing...");
        initNativeApi();
        log.info("Native API - Initialization completed!");

        log.info("Hotkeys scheduler - Initializing...");
        eventsService.initHotkeysScheduler();
        log.info("Hotkeys scheduler - Initialization completed!");

        log.info("Window list scheduler - Initializing...");
        scheduledExecutorService.scheduleAtFixedRate(
                socketMessageSenderService::sendWindowList,
                0,
                1,
                TimeUnit.SECONDS
        );
        log.info("Window list scheduler - Initialization completed!");
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
