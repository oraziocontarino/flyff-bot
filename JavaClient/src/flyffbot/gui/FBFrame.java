package flyffbot.gui;

import flyffbot.Application;
import flyffbot.configuration.DepInjComponentData;
import flyffbot.exceptions.PipeConfigNotFound;
import flyffbot.exceptions.SaveLoadException;
import flyffbot.gui.components.GlobalHotKeysRow;
import flyffbot.gui.components.JFBPanel;
import flyffbot.gui.components.pipe.FBPipePanel;
import flyffbot.gui.listeners.KeyDownHookListener;
import flyffbot.services.KeyDownHookService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class FBFrame extends JFrame{
    @Value("${release.title}")
    private String title;
    @Value("${pipe-config.max}")
    private int maxPipes;

    @Value("${pipe-config.min}")
    private int minPipes;

    @Value("${auto-save.folder-name}")
    private String folderName;

    @Autowired
    private DepInjComponentData services;

    private GlobalHotKeysRow globalHotKeysRow;
    private List<JFBPanel> uiPipePanels;

    private KeyDownHookService keyDownHookService;

    @PostConstruct
    public void init(){
        keyDownHookService = new KeyDownHookService(buildKeyDownEvents());
        initNativeApi();
        initGui();
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

    private void initGui(){
        setLayout(new FlowLayout(FlowLayout.LEFT, GuiConstants.padding, GuiConstants.padding));
        setTitle(title);
        val userService = services.getUserConfigService();
        userService.findAllPipes().forEach(pipe -> userService.updatePause(pipe.getId(), true));

        globalHotKeysRow = new GlobalHotKeysRow(services);

        add(globalHotKeysRow);

        uiPipePanels = new ArrayList<>();

        val size = services.getUserConfigService().countPipes();
        for(var i = 0; i < size; i++){
            initializeNewPipe(i, false);
        }

        // Set JFrame style
        updateJFrameSize(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //TODO: on shutdown remove all scheduler invoking scheduler.shutdown();
        setVisible(true);
    }

    private FBPipePanel findPipePanelById(String pipeId){
        return (FBPipePanel) uiPipePanels.stream().filter(pipe -> pipe.getPipeId().equals(pipeId))
                .findFirst()
                .orElseThrow(()-> new PipeConfigNotFound("UI Pipe not found: "+pipeId));
    }

    private void initializeNewPipe(int nextPipeIndex, boolean doResize){
        var cfg = services.getUserConfigService().createIfNotExistsPipe(nextPipeIndex);
        var pipe = new FBPipePanel(cfg.getId(), services);

        pipe.setBorder(BorderFactory.createTitledBorder("Pipe "+(nextPipeIndex+1)));

        // Add pipe to visible pipe list
        uiPipePanels.add(pipe);

        // Adding pipe to JFrame
        add(pipe);

        // Resize JFrame (width)
        updateJFrameSize(doResize);

        keyDownHookService.addKeyBinding(pipe.getPipeId(), nextPipeIndex);
    }
    private void updateJFrameSize(boolean doResize){
        if(doResize) {
            this.setSize(
                    (GuiConstants.padding * 5) + ((GuiConstants.rowWidth + GuiConstants.padding)* uiPipePanels.size()),
                    GuiConstants.frameHeight
            );
            globalHotKeysRow.updateSize();
        }
    }

    private KeyDownHookListener buildKeyDownEvents() {
        return new KeyDownHookListener() {
            @Override
            public void onAddPipe() {
                if(uiPipePanels.size() < maxPipes){
                    initializeNewPipe(uiPipePanels.size(), true);
                }
            }

            @Override
            public Optional<String> onRemovePipe() {
                if(uiPipePanels.size() == minPipes){
                    return Optional.empty();
                }
                val index = services.getUserConfigService().countPipes() - 1;
                val removedPipeId = services.getUserConfigService().removeLastPipe();
                val pipeToRemove = uiPipePanels.get(index);
                remove(pipeToRemove);
                uiPipePanels.remove(index);
                updateJFrameSize(true);
                return Optional.of(removedPipeId);
            }

            @Override
            public void onTogglePause(String pipeId) {
                val pipePanel = findPipePanelById(pipeId);
                pipePanel.togglePause();
            }

            @Override
            public void onCustomActionSlot(String pipeId) {
                val pipePanel = findPipePanelById(pipeId);
                pipePanel.useCustomActionSlot();
            }
        };
    }
}
