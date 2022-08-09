package flyffbot.gui.components.pipe.row3bindingrows;

import flyffbot.configuration.DepInjComponentData;
import flyffbot.dto.comboitem.FBComboStringDto;
import flyffbot.entity.BindingRowDto;
import flyffbot.exceptions.DelayConfigException;
import flyffbot.gui.GuiConstants;
import flyffbot.gui.components.EmptyItem;
import flyffbot.gui.components.JFBComboBoxString;
import flyffbot.gui.components.JFBPanel;
import flyffbot.gui.components.JPanelWrapper;
import flyffbot.gui.components.filters.IntegerFilter;
import flyffbot.gui.listeners.BindingRowEventsListener;
import flyffbot.gui.listeners.JFBTextFieldListener;
import flyffbot.utils.Utils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Getter
@Slf4j
public class FBBindingRow extends JFBPanel {
    private DepInjComponentData services;
    private final JLabel keyLabel;
    private JComboBox<FBComboStringDto> key0Input;
    private JComboBox<FBComboStringDto> key1Input;
    private final JLabel delayLabel;
    private JTextField delayInput;
    private JCheckBox activeCheckbox;

    private final BindingRowEventsListener events;
    private ScheduledFuture<?> future;

    public FBBindingRow(
            String pipeId,
            DepInjComponentData services,
            BindingRowDto cfg,
            BindingRowEventsListener events
    ){
        super(cfg.getId(), pipeId);
        this.services = services;
        this.events = events;
        future = services.getScheduler().scheduleAtFixedRate(
                ()-> events.onDispatchKeyStroke(pipeId, getId()),
                1000,
                cfg.getDelay(),
                TimeUnit.MILLISECONDS
        );

        val layout = new FlowLayout(FlowLayout.LEFT, 0, 0);
        layout.setAlignment(FlowLayout.LEADING);
        setLayout(layout);


        keyLabel = new JLabel("Key:");
        add(keyLabel);
        add(new EmptyItem(GuiConstants.padding, GuiConstants.rowHeight50));

        add(initKey0ComboBox(cfg)); //65px
        add(new EmptyItem(GuiConstants.padding, GuiConstants.rowHeight50));

        add(initKey1ComboBox(cfg)); //40px
        add(new EmptyItem(GuiConstants.padding, GuiConstants.rowHeight50));

        delayLabel = new JLabel("Delay (ms):");
        add(delayLabel);
        add(new EmptyItem(GuiConstants.padding, GuiConstants.rowHeight50));

        add(initDelayInput(cfg));
        add(new EmptyItem(GuiConstants.padding, GuiConstants.rowHeight50));

        this.add(initActiveInput(cfg));
    }

    public void updateScheduler(int newDelay){
        log.debug(getId() + " - Changing scheduler tipe to: "+newDelay+" ms");
        // Cancel scheduler
        future.cancel(true);
        // Schedule again (reuse)
        future = services.getScheduler().scheduleAtFixedRate(
                ()-> events.onDispatchKeyStroke(pipeId, getId()),
                1000,
                newDelay,
                TimeUnit.MILLISECONDS
        );
    }
    private JPanelWrapper initKey0ComboBox(BindingRowDto cfg){
        val options = Utils.buildAltCtrlList();
        key0Input = new JFBComboBoxString(options, cfg.getHexKeyCode0(), selected -> events.onKey0Change(
                pipeId,
                id,
                selected.getValue()
        ));
        return new JPanelWrapper(key0Input, 65, GuiConstants.rowHeight - (GuiConstants.boxPadding * 2));
    }
    private JPanelWrapper initKey1ComboBox(BindingRowDto cfg){
        val options = Utils.build09List();
        key1Input = new JFBComboBoxString(options, cfg.getHexKeyCode1(), selected -> events.onKey1Change(
                pipeId,
                id,
                selected.getValue()
        ));
        return new JPanelWrapper(key1Input, 40, GuiConstants.rowHeight - (GuiConstants.boxPadding * 2));
    }
    private JPanelWrapper initDelayInput(BindingRowDto cfg){
        delayInput = new JTextField("" + cfg.getDelay(), 5);
        ((PlainDocument) delayInput.getDocument()).setDocumentFilter(new IntegerFilter());
        delayInput.getDocument().addDocumentListener((JFBTextFieldListener) evt -> {
            Optional<Integer> newValue;
            try{
                newValue = Optional.of(Integer.parseInt(delayInput.getText()));
            }catch (Exception ignored){
                newValue = Optional.empty();
            }

            newValue.ifPresent(value -> {
                try {
                    events.onDelayChange(pipeId, id, value);
                }catch (Exception e){
                    throw new DelayConfigException("Error occurred while reading delay on binding row: "+cfg.getId(), e);
                }
            });
        });
        return new JPanelWrapper(delayInput, 55, GuiConstants.rowHeight - (GuiConstants.boxPadding * 2));
    }
    private Component initActiveInput(BindingRowDto cfg) {
        activeCheckbox = new JCheckBox("Active", cfg.isActive());
        activeCheckbox.addItemListener(e-> events.onActiveChange(
                pipeId,
                id,
                e.getStateChange() == ItemEvent.SELECTED
        ));
        activeCheckbox.setSize(50, 30);
        return activeCheckbox;
    }
}
