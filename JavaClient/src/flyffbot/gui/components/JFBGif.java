package flyffbot.gui.components;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class JFBGif extends JPanel {
    public JFBGif(String resourcePath, Dimension iconSize){
        super();
        setLayout(null);
        try {
            val stream = getClass().getResourceAsStream(resourcePath);
            assert stream != null;
            val image = Toolkit.getDefaultToolkit().createImage(IOUtils.toByteArray(stream));
            val loader = new ImageIcon(image);
            val label = new JLabel(loader);
            val size = new Rectangle(0,0,(int) iconSize.getWidth(), (int) iconSize.getHeight());
            label.setBounds(size);
            setBounds(size);
            add(label);
        }catch (Exception e){
            log.error("Error occurred while setting icon: "+resourcePath, e);
        }
    }
}
