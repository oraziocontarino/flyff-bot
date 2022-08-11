package flyffbot.gui.components;

import flyffbot.configuration.DepInjComponentData;
import flyffbot.gui.GuiConstants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import javax.swing.*;
import java.awt.*;

@Getter
@Slf4j
public class GlobalHotKeysRow extends JFBPanel {
    private final DepInjComponentData services;
    private JLabel leftLabel;
    private JLabel leftValue;
    private JLabel rightLabel;
    private JLabel rightValue;

    private final JPanel contentWrapper;

    public GlobalHotKeysRow(DepInjComponentData services){
        super("", "");
        this.services = services;
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        contentWrapper = new JPanel(new GridLayout(1, 2));
        contentWrapper.setBorder(BorderFactory.createTitledBorder("Global HotKeys"));

        contentWrapper.add(new JLabel("Add pipe: Alt + A"));
        contentWrapper.add(new JLabel("Add pipe: Alt + D"));

        contentWrapper.setPreferredSize(new Dimension(GuiConstants.rowWidth, GuiConstants.rowHeightLg));
        updateSize();

        add(contentWrapper);
    }

    public void updateSize() {
        val i = services.getUserConfigService().countPipes();
        setPreferredSize(new Dimension(GuiConstants.rowWidth*i, GuiConstants.rowHeightLg));
    }
}
