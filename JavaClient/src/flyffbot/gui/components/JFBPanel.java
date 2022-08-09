package flyffbot.gui.components;

import lombok.Data;

import javax.swing.*;

@Data
public abstract class JFBPanel extends JPanel {
    protected String id;
    protected String pipeId;

    public JFBPanel(String id, String pipeId){
        super();
        this.id = id;
        this.pipeId = pipeId;
    }
}
