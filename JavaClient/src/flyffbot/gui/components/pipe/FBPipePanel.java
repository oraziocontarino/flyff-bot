package flyffbot.gui.components.pipe;

import flyffbot.configuration.DepInjComponentData;
import flyffbot.entity.BindingRowDto;
import flyffbot.enums.KeyStatus;
import flyffbot.exceptions.SaveLoadException;
import flyffbot.gui.GuiConstants;
import flyffbot.gui.components.JFBPanel;
import flyffbot.gui.components.pipe.row1statushotkeys.Row1StatusHotKeys;
import flyffbot.gui.components.pipe.row2windownamebindings.Row2WindowNameBindings;
import flyffbot.gui.components.pipe.row3bindingrows.Row3ScrollablePanel;
import flyffbot.gui.components.pipe.row4customactionslot.Row4CustomActionSlot;
import flyffbot.gui.components.pipe.row4customactionslot.Row4SettingsDialog;
import flyffbot.gui.listeners.BindingRowEventsListener;
import flyffbot.gui.listeners.PipeSettingsListener;
import flyffbot.gui.listeners.WindowNameRowEventsListener;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class FBPipePanel extends JFBPanel {
    private DepInjComponentData services;
    private Row1StatusHotKeys row1StatusHotKeys;
    private Row2WindowNameBindings row2WindowNameBindings;
    private Row3ScrollablePanel row3BindingRows;
    private Row4CustomActionSlot row4CustomActionSlot;
    private Row4SettingsDialog row4SettingsDialog;
    private List<JComponent> stack;

    private WindowNameRowEventsListener windowNameRowEvents;
    private BindingRowEventsListener bindingRowEvents;

    private PipeSettingsListener pipeSettingsListener;

    public FBPipePanel(
            String pipeId,
            DepInjComponentData services
    ) {
        super(pipeId, pipeId);
        this.services = services;
        windowNameRowEvents = buildWindowNameRowEventsListener();
        bindingRowEvents = buildBindingRowEvents();
        pipeSettingsListener = buildPipeSettingsListener();

        // Set JPanel style
        val layout = new FlowLayout(FlowLayout.LEFT, 0, 0);
        layout.setAlignOnBaseline(true);
        this.setLayout(layout);
        this.setPreferredSize(new Dimension(
                GuiConstants.rowWidth,
                GuiConstants.frameHeight - (GuiConstants.rowHeightLg*2) - (GuiConstants.boxPadding*2)
        ));

        // Init bot status info row
        row1StatusHotKeys = new Row1StatusHotKeys(pipeId, services);

        // Init Pipe Header / label
        row2WindowNameBindings = new Row2WindowNameBindings(pipeId, services, windowNameRowEvents);

        row3BindingRows = new Row3ScrollablePanel(pipeId, services, bindingRowEvents);

        row4CustomActionSlot = new Row4CustomActionSlot(pipeId, services, pipeSettingsListener);

        row4SettingsDialog = new Row4SettingsDialog(pipeId, services, pipeSettingsListener);

        stack = new ArrayList<>();
        stack.add(row1StatusHotKeys);
        stack.add(row2WindowNameBindings);
        stack.add(row3BindingRows);
        stack.add(row4CustomActionSlot);
        stack.add(row4SettingsDialog); //TODO: restore it and remove content!
        //stack.add(new Row4SettingsDialogContent(pipeId, services, pipeSettingsListener));

        // Add key-bindings rows
        stack.forEach(this::add);
    }

    private WindowNameRowEventsListener buildWindowNameRowEventsListener(){
        val _this = this;
        return new WindowNameRowEventsListener(){
            @Override
            public void onNameChange(String pipeId, String value) {
                try {
                    services.getUserConfigService().saveSelectedWindowHwnd(pipeId, value);
                    onConfigChange();
                }catch (Exception e){
                    throw new SaveLoadException("Error occurred while updating selected window hwnd in pipe: "+pipeId, e);
                }
            }

            @Override
            public void onAdd() {
                row3BindingRows.addBindingRow();
                _this.revalidate();
                _this.repaint();
                onConfigChange();
            }

            @Override
            public void onRemove() {
                services.getUserConfigService().removeLastBindingRow(pipeId).ifPresent(bindingRowId -> {
                    row3BindingRows.removeBindingRow(bindingRowId);
                    _this.revalidate();
                    _this.repaint();
                });
                onConfigChange();
            }

            @Override
            public void onNameRefresh(String pipeId) {
                try {
                    val response = services.getNativeGetWindowListService().execute();
                    if(!response.isSuccess()){
                        throw new SaveLoadException("Unable retrieve updated window list from OS ");
                    }
                    val windowData = services.getUserConfigService().findWindowNameRowData(pipeId);
                    val oldHwnd = windowData.getHwnd();
                    val newHwnd = row2WindowNameBindings.updateWindowList(response.getData());
                    if(!StringUtils.equals(oldHwnd, newHwnd)) {
                        services.getUserConfigService().saveSelectedWindowHwnd(pipeId, newHwnd);
                        onConfigChange();
                    }
                }catch (Exception e){
                    throw new SaveLoadException("Unable to save updated window list in given pipe: "+pipeId, e);
                }
            }
        };
    }

    private BindingRowEventsListener buildBindingRowEvents(){
        return new BindingRowEventsListener(){
            @Override
            public void onKey0Change(String pipeId, String cid, String value) {
                services.getUserConfigService().updateBindingRowKey0(pipeId, cid, value);
                onConfigChange();
            }
            @Override
            public void onKey1Change(String pipeId, String cid, String value) {
                services.getUserConfigService().updateBindingRowKey1(pipeId, cid, value);
                onConfigChange();
            }

            @Override
            public void onDelayChange(String pipeId, String cid, int value) {
                services.getUserConfigService().updateBindingRowDelay(pipeId, cid, value);
                row3BindingRows.updateScheduler(cid, value);
                onConfigChange();
            }

            @Override
            public void onActiveChange(String pipeId, String cid, boolean value) {
                services.getUserConfigService().updateBindingRowActive(pipeId, cid, value);
                onConfigChange();
            }

            @Override
            public void onDispatchKeyStroke(String pipeId, String cid) {
                val pipeCfg = services.getUserConfigService().findPipeById(pipeId);
                val windowCfg = pipeCfg.getWindowNameRow();
                val keyCfg = services.getUserConfigService().findBindingRowById(pipeId, cid);
                val isPaused = pipeCfg.isPaused();

                if(windowCfg.getHwnd().isBlank() || !keyCfg.isActive() || isPaused){
                    return;
                }

                val keys = Stream.of(keyCfg.getHexKeyCode0(), keyCfg.getHexKeyCode1())
                        .filter(item -> !item.isBlank())
                        .collect(Collectors.toList());

                CompletableFuture.supplyAsync(() -> {
                    try {
                        services.getNativeSendKeyService().execute(
                                KeyStatus.DOWN, windowCfg.getHwnd(), keys
                        );
                        Thread.sleep(500);
                        services.getNativeSendKeyService().execute(
                                KeyStatus.UP, windowCfg.getHwnd(), keys
                        );
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                });
            }
        };
    }

    private PipeSettingsListener buildPipeSettingsListener(){
        return new PipeSettingsListener() {
            @Override
            public void showDialog() {
                row1StatusHotKeys.setVisible(false);
                row2WindowNameBindings.setVisible(false);
                row3BindingRows.setVisible(false);
                row4CustomActionSlot.setVisible(false);
                row4SettingsDialog.setVisible(true);
            }

            @Override
            public void saveAndClose() {
                row4SettingsDialog.setVisible(false);
                row1StatusHotKeys.setVisible(true);
                row2WindowNameBindings.setVisible(true);
                row3BindingRows.setVisible(true);
                row4CustomActionSlot.updateCount();
                row4CustomActionSlot.setVisible(true);
            }
        };
    }
    private void onConfigChange(){
        val userService = services.getUserConfigService();
        val pipeData = userService.findPipeById(pipeId);
        val isPaused = pipeData.isPaused();
        val isCASRunning = pipeData.isCustomActionSlotRunning();
        val count = userService.countActiveBindingRowByPipeId(pipeId);

        if(isPaused){
            row1StatusHotKeys.updateBotStatus("Bindings: paused");
        }else if(count == 0){
            row1StatusHotKeys.updateBotStatus("Bindings: idle");
        }else {
            row1StatusHotKeys.updateBotStatus("Bindings: running...");
        }

        row1StatusHotKeys.updateBotCASStatus(isCASRunning ? "CAS: running..." : "CAS: idle");
    }

    public void togglePause() {
        services.getUserConfigService().updateTogglePipe(pipeId);
        onConfigChange();
    }

    public void useCustomActionSlot() {
        val userRepository = services.getUserConfigService();
        val pipe = userRepository.findPipeById(pipeId);
        val hwnd = pipe.getWindowNameRow().getHwnd();
        val hasActiveActions = pipe.getBindingRows()
                .stream().anyMatch(BindingRowDto::isActive);
        val skills = pipe.getCustomActionSlotSkills();
        val oldIsPaused = pipe.isPaused();
        userRepository.updatePause(pipeId, true);
        userRepository.updateCASRunning(pipeId, true);
        onConfigChange();


        services.getScheduler().schedule( () -> {
            try {
                val waitAndRestore = !oldIsPaused && hasActiveActions;
                if(waitAndRestore){
                    // Wait last auto-key action to be processed
                    Thread.sleep(1000);
                }
                // Execute custom action slot with auto-key foce paused
                log.debug("Running custom action slot: {}", skills);
                for(var i = 0; i < skills.size(); i++) {
                    if(i > 0 ){
                        Thread.sleep(skills.get(i - 1).getCastTime() + 250);
                    }
                    log.debug("Running skill {}/{}: {}", skills.get(i), skills.size(), skills.get(i));
                    val currentSkill = skills.get(i);
                    val keys = Stream.of(currentSkill.getHexKeyCode0(), currentSkill.getHexKeyCode1())
                            .filter(item -> !StringUtils.isBlank(item))
                            .collect(Collectors.toList());
                    services.getNativeSendKeyService().execute(KeyStatus.DOWN, hwnd, keys);
                    Thread.sleep(500);
                    services.getNativeSendKeyService().execute(KeyStatus.UP, hwnd, keys);
                }

                // Restore old pipe status
                if(waitAndRestore){
                    Thread.sleep(250);
                    userRepository.updatePause(pipeId, false);
                }
                userRepository.updateCASRunning(pipeId, false);
                onConfigChange();
            }catch (Exception e){
                log.error("Error occurred while processing action slot hotkey", e);
            }
        }, 1, TimeUnit.SECONDS);
    }
}
