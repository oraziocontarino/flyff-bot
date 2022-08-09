package flyffbot.gui.components.pipe.row2windownamebindings;

import flyffbot.configuration.DepInjComponentData;
import flyffbot.dto.comboitem.FBComboStringDto;
import flyffbot.gui.GuiConstants;
import flyffbot.gui.components.JFBComboBoxString;
import flyffbot.gui.components.JFBPanel;
import flyffbot.gui.listeners.WindowNameRowEventsListener;
import lombok.val;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WindowNameBox extends JFBPanel {
    private DepInjComponentData services;
    private WindowNameRowEventsListener events;
    private JFBComboBoxString input;
    private boolean isLoadingCombo;

    public WindowNameBox(String pipeId, String title, DepInjComponentData services, WindowNameRowEventsListener events) {
        super("", pipeId);
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setBorder(BorderFactory.createTitledBorder(title));
        setPreferredSize(new Dimension(
                GuiConstants.rowWidth65 - GuiConstants.boxPadding,
                GuiConstants.rowHeightLg + GuiConstants.boxPadding
        ));

        this.services = services;
        this.events = events;
        isLoadingCombo = false;
        initComboBox();
        add(input);
    }

    private void initComboBox(){
        val options = new ArrayList<FBComboStringDto>();
        options.add(FBComboStringDto.builder().label("No valid windows found").value("").build());

        input = new JFBComboBoxString(options, "", selected -> {
            if(!isLoadingCombo) {
                events.onNameChange(pipeId, selected.getValue());
            }
        });
        input.setSelectedIndex(0);

        services.getScheduler().scheduleAtFixedRate(
                ()-> {
                    this.isLoadingCombo = true;
                    events.onNameRefresh(pipeId);
                    this.isLoadingCombo = false;
                },
                1,
                3,
                TimeUnit.SECONDS
        );
        input.setPreferredSize(new Dimension(
                GuiConstants.rowWidth65 - (GuiConstants.boxPadding * 3),
                GuiConstants.rowHeight - (GuiConstants.boxPadding * 2)
        ));
    }

    public String updateOptions(List<FBComboStringDto> options) {
        return input.updateOptions(options).getValue();
    }
}
