package flyffbot.gui.components.pipe.row4customactionslot;

import flyffbot.configuration.DepInjComponentData;
import flyffbot.gui.GuiConstants;
import flyffbot.gui.components.JFBPanel;
import flyffbot.gui.listeners.PipeSettingsListener;
import lombok.val;

import javax.swing.*;
import java.awt.*;

public class Row4CustomActionSlot extends JFBPanel {
    private DepInjComponentData services;
    private JLabel label;
    private JButton settingsButton;

    public Row4CustomActionSlot(String pipeId, DepInjComponentData services, PipeSettingsListener pipeSettingsListener) {
        super("", pipeId);
        this.services = services;
        val layout = new BorderLayout();
        setLayout(layout);

        setPreferredSize(new Dimension(
                GuiConstants.rowWidth - (GuiConstants.boxPadding*2),
                GuiConstants.rowHeightLg + GuiConstants.boxPadding
        ));
        setBorder(BorderFactory.createTitledBorder("Custom action slot (CAS)"));

        label = new JLabel();
        updateCount();
        settingsButton = new JButton("Settings");
        settingsButton.addActionListener(e -> pipeSettingsListener.showDialog());

        add(label, BorderLayout.LINE_START);
        add(settingsButton, BorderLayout.LINE_END);
    }

    public void updateCount() {
        val size = services.getUserConfigService().findPipeById(pipeId).getCustomActionSlotSkills().size();
        label.setText("Skills count: " + size);
    }
}
