package flyffbot.gui.components.pipe.row1statushotkeys;

import flyffbot.gui.GuiConstants;
import flyffbot.gui.components.JFBPanel;

import javax.swing.*;
import java.awt.*;

public class HotKeysBox extends JFBPanel {
    private String boxTitle;
    private JLabel value0;
    private JLabel value1;
    private JLabel value2;

    public HotKeysBox(String pipeId, String title, String value0, String value1, String value2) {
        super("", pipeId);
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setBorder(BorderFactory.createTitledBorder(title));
        setPreferredSize(new Dimension(
                GuiConstants.rowWidth65 - GuiConstants.boxPadding,
                GuiConstants.rowHeight75*3 + GuiConstants.padding
        ));

        this.value0 = new JLabel(value0);
        this.value1 = new JLabel(value1);
        this.value2 = new JLabel(value2);
        add(this.value0);
        add(this.value1);
        add(this.value2);
    }
}
