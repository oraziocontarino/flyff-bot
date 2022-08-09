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
    private DepInjComponentData services;
    private JLabel leftLabel;
    private JLabel leftValue;
    private JLabel rightLabel;
    private JLabel rightValue;

    private JPanel contentWrapper;

    public GlobalHotKeysRow(DepInjComponentData services){
        super("", "");
        this.services = services;
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        contentWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        contentWrapper.setBorder(BorderFactory.createTitledBorder("Global HotKeys"));

        initLeft("Add pipe: ", "Alt + A");

        initRight("Remove pipe: ", "Alt + D");

        contentWrapper.setPreferredSize(new Dimension(GuiConstants.rowWidth, GuiConstants.rowHeightLg));
        updateSize();

        add(contentWrapper);
    }

    private void initLeft(String label, String value){
        leftLabel = new JLabel(label);
        leftValue = new JLabel(value);

        val wrapper = buildWrapper(leftLabel, leftValue);
        contentWrapper.add(wrapper);
    }

    private void initRight(String label, String value){
        rightLabel = new JLabel(label);
        rightValue = new JLabel(value);

        val wrapper = buildWrapper(rightLabel, rightValue);
        contentWrapper.add(wrapper);
    }

    private JPanel buildWrapper(JLabel label, JLabel value){
        val wrapper = new JPanel();
        val wrapperLayout = new FlowLayout(FlowLayout.LEFT,0,0);
        wrapper.setLayout(wrapperLayout);

        wrapper.add(label);
        wrapper.add(value);

        wrapper.setPreferredSize(new Dimension((GuiConstants.rowWidth/2) - GuiConstants.boxPadding, GuiConstants.rowHeight50));
        return wrapper;
    }

    public void updateSize() {
        val i = services.getUserConfigService().countPipes();
        setPreferredSize(new Dimension(GuiConstants.rowWidth*i, GuiConstants.rowHeightLg));
    }
}
