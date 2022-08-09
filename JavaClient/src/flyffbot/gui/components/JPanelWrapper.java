package flyffbot.gui.components;
import javax.swing.*;
import java.awt.*;

public class JPanelWrapper extends JPanel{
    public JPanelWrapper(JComponent component, int width, int height){
        super();
        setLayout(new BorderLayout());
        add(component, BorderLayout.CENTER);
        setPreferredSize(new Dimension(width, height));
    }
}
