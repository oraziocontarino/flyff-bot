package flyffbot.gui.components.pipe.row4customactionslot;

import flyffbot.entity.CustomActionSlotSkillDto;
import flyffbot.exceptions.CastTimeConfigException;
import flyffbot.gui.GuiConstants;
import flyffbot.gui.components.EmptyItem;
import flyffbot.gui.components.JFBComboBoxString;
import flyffbot.gui.components.JFBPanel;
import flyffbot.gui.components.filters.IntegerFilter;
import flyffbot.gui.listeners.CustomActionSlotListener;
import flyffbot.gui.listeners.JFBTextFieldListener;
import flyffbot.utils.Utils;
import lombok.val;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.util.Optional;

public class Row4SettingsRow extends JFBPanel {
    public Row4SettingsRow(String pipeId, CustomActionSlotSkillDto cfg, CustomActionSlotListener listener) {
        super(cfg.getId(), pipeId);
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JFBComboBoxString key0 = new JFBComboBoxString(
                Utils.buildAltCtrlList(),
                cfg.getHexKeyCode0(),
                comboItem -> listener.onKey0Change(pipeId, cfg.getId(), comboItem.getValue())
        );
        key0.setPreferredSize(new Dimension(GuiConstants.customActionSlotColumnWidth, GuiConstants.rowHeight75));

        JFBComboBoxString key1 = new JFBComboBoxString(
                Utils.build09List(),
                cfg.getHexKeyCode1(),
                comboItem -> listener.onKey1Change(pipeId, cfg.getId(), comboItem.getValue())
        );
        key1.setPreferredSize(new Dimension(GuiConstants.customActionSlotColumnWidth, GuiConstants.rowHeight75));

        val castTime = new JTextField();
        castTime.setPreferredSize(new Dimension(GuiConstants.customActionSlotColumnWidth, GuiConstants.rowHeight75));
        castTime.setText(cfg.getCastTime().toString());
        ((PlainDocument) castTime.getDocument()).setDocumentFilter(new IntegerFilter());
        castTime.getDocument().addDocumentListener((JFBTextFieldListener) evt -> {
            Optional<Integer> newValue;
            try{
                newValue = Optional.of(Integer.parseInt(castTime.getText()));
            }catch (Exception ignored){
                newValue = Optional.empty();
            }

            newValue.ifPresent(value -> {
                try {
                    listener.onCastTimeChange(pipeId, id, value);
                }catch (Exception e){
                    throw new CastTimeConfigException("Error occurred while reading cast time on skill: "+cfg.getId(), e);
                }
            });
        });

        add(key0);
        add(new EmptyItem(GuiConstants.padding, GuiConstants.rowHeight50));
        add(key1);
        add(new EmptyItem(GuiConstants.padding * 4, GuiConstants.rowHeight50));
        add(castTime);
    }
}
