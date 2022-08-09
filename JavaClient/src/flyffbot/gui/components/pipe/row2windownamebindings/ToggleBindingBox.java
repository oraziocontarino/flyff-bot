package flyffbot.gui.components.pipe.row2windownamebindings;

import flyffbot.gui.GuiConstants;
import flyffbot.gui.components.JFBPanel;
import flyffbot.gui.listeners.WindowNameRowEventsListener;

import javax.swing.*;
import java.awt.*;

public class ToggleBindingBox extends JFBPanel {
    private JButton addButton;
    private JButton removeButton;

    public ToggleBindingBox(String pipeId, String title, WindowNameRowEventsListener events) {
        super("", pipeId);
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setBorder(BorderFactory.createTitledBorder(title));
        setPreferredSize(new Dimension(
                GuiConstants.rowWidth35 - GuiConstants.boxPadding,
                GuiConstants.rowHeightLg + GuiConstants.boxPadding
        ));

        addButton = new JButton("+");
        addButton.addActionListener(e -> events.onAdd());

        removeButton = new JButton("-");
        removeButton.addActionListener(e -> events.onRemove());

        add(addButton);
        add(removeButton);
    }
}
