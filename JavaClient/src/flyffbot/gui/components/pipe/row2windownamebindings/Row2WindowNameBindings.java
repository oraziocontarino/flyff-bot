package flyffbot.gui.components.pipe.row2windownamebindings;

import flyffbot.configuration.DepInjComponentData;
import flyffbot.dto.comboitem.FBComboStringDto;
import flyffbot.dto.nativeapi.WindowItem;
import flyffbot.gui.GuiConstants;
import flyffbot.gui.components.JFBPanel;
import flyffbot.gui.listeners.WindowNameRowEventsListener;
import lombok.val;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class Row2WindowNameBindings extends JFBPanel {
    private DepInjComponentData services;
    private WindowNameBox windowNameBox;
    private ToggleBindingBox toggleBindingBox;

    public Row2WindowNameBindings(String pipeId, DepInjComponentData services, WindowNameRowEventsListener events) {
        super("", pipeId);
        this.services = services;
        val layout = new FlowLayout(FlowLayout.LEFT, 0, 0);
        layout.setAlignOnBaseline(true);
        setLayout(layout);
        setPreferredSize(new Dimension(
                GuiConstants.rowWidth - (GuiConstants.boxPadding*2),
                GuiConstants.rowHeightLg + GuiConstants.boxPadding
        ));

        windowNameBox = new WindowNameBox(pipeId, "Select window name", services, events);

        toggleBindingBox = new ToggleBindingBox(pipeId, "Manage bindings", events);

        add(windowNameBox);
        add(toggleBindingBox);
    }

    public String updateWindowList(List<WindowItem> data) {
        val options = data.stream()
                .map(item -> new FBComboStringDto(item.getTitle(), item.getHwnd()))
                .collect(Collectors.toList());
        return windowNameBox.updateOptions(options);
    }
}
