package flyffbot.gui.components;

import javax.swing.*;
import java.awt.*;

public class EmptyItem extends JPanel {
    public EmptyItem(int width, int height){
        setLayout(new FlowLayout());
        setPreferredSize(new Dimension(width, height));
    }
}
