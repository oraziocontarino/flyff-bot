package flyffbot.gui;

import flyffbot.configuration.DepInjComponentData;
import flyffbot.exceptions.PipeConfigNotFound;
import flyffbot.gui.components.GlobalHotKeysRow;
import flyffbot.gui.components.JFBPanel;
import flyffbot.gui.components.pipe.FBPipePanel;
import flyffbot.services.KeyDownHookService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class FBFrame extends JFrame{
    @Value("${release.title}")
    private String title;
    @Value("${pipe-config.max}")
    private int maxPipes;

    @Value("${pipe-config.min}")
    private int minPipes;

    @Autowired
    private DepInjComponentData services;

    private GlobalHotKeysRow globalHotKeysRow;
    private List<JFBPanel> uiPipePanels;

    @PostConstruct
    public void init(){
        initGui();
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
            addJFramePipe(i, false);
        }

        // Set JFrame style
        updateJFrameSize(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //TODO: on shutdown remove all scheduler invoking scheduler.shutdown();
        setVisible(true);
    }


    public void togglePause(String pipeId) {
        val pipePanel = findPipePanelById(pipeId);
        pipePanel.togglePause();
    }

    public void useActionSlot(String pipeId, List<String> keys) {
        val pipePanel = findPipePanelById(pipeId);
        pipePanel.useActionSlot(keys);
    }

    public void addPipe() {
        if(uiPipePanels.size() < maxPipes){
            addJFramePipe(uiPipePanels.size(), true);
        }
    }

    public void removePipe() {
        if(uiPipePanels.size() == minPipes){
            return;
        }
        val index = services.getUserConfigService().countPipes() - 1;
        val removedPipeId = services.getUserConfigService().removeLastPipe();
        val pipeToRemove = uiPipePanels.get(index);
        remove(pipeToRemove);
        uiPipePanels.remove(index);
        services.getKeyDownHookService().removeKeyBinding(removedPipeId);
        updateJFrameSize(true);
    }

    private FBPipePanel findPipePanelById(String pipeId){
        return (FBPipePanel) uiPipePanels.stream().filter(pipe -> pipe.getPipeId().equals(pipeId))
                .findFirst()
                .orElseThrow(()-> new PipeConfigNotFound("UI Pipe not found: "+pipeId));
    }

    private void addJFramePipe(int nextPipeIndex, boolean doResize){
        var cfg = services.getUserConfigService().createIfNotExistsPipe(nextPipeIndex);
        var pipe = new FBPipePanel(cfg.getId(), services);

        pipe.setBorder(BorderFactory.createTitledBorder("Pipe "+(nextPipeIndex+1)));

        // Add pipe to visible pipe list
        uiPipePanels.add(pipe);

        // Adding pipe to JFrame
        add(pipe);

        // Resize JFrame (width)
        updateJFrameSize(doResize);
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

    public void initHooks(KeyDownHookService keyDownHookService) {
        services.setKeyDownHookService(keyDownHookService);
        val list = services.getUserConfigService().getPipeList();
        for(var i = 0; i < list.size(); i++){
            val item = list.get(i);
            // Add HotKeys
            services.getKeyDownHookService().addKeyBinding(item.getId(), i);
        }
    }

    public void useCustomActionSlot(String pipeId) {
        val pipePanel = findPipePanelById(pipeId);
        pipePanel.useCustomActionSlot();
    }
}
