package flyffbot.gui.components.pipe.row1statushotkeys;

import flyffbot.gui.GuiConstants;
import flyffbot.gui.components.JFBPanel;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import javax.swing.*;
import java.awt.*;
import java.util.stream.Stream;

@Slf4j
public class HotKeysBox extends JFBPanel {
    public HotKeysBox(String pipeId, String title, String value0, String value1) {
        super("", pipeId);
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setBorder(BorderFactory.createTitledBorder(title));
        val boxWidth = GuiConstants.rowWidth65 - GuiConstants.boxPadding;
        setPreferredSize(new Dimension(
                boxWidth,
                (GuiConstants.rowHeight75*2) + (GuiConstants.boxPadding * 2)
        ));

        Stream.of(value0, value1).forEach(value -> {
            JLabel jElement = new JLabel(value);
            jElement.setPreferredSize(new Dimension(
                    boxWidth - (GuiConstants.boxPadding*2),
                    GuiConstants.textHeight
            ));
            add(jElement);
        });
    }
}
