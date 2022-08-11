package flyffbot.gui.components.pipe.row1statushotkeys;

import flyffbot.configuration.DepInjComponentData;
import flyffbot.entity.BindingRowDto;
import flyffbot.gui.GuiConstants;
import flyffbot.gui.components.JFBPanel;
import lombok.val;

import javax.swing.*;
import java.awt.*;

public class BotStatusBox extends JFBPanel {
    private String boxTitle;
    private JLabel value;
    private JLabel casValue;

    public BotStatusBox(String pipeId, DepInjComponentData services) {
        super("", pipeId);
        val layout = new FlowLayout(FlowLayout.LEFT, 0, 0);
        layout.setAlignOnBaseline(true);
        setLayout(layout);
        setBorder(BorderFactory.createTitledBorder("Bot status"));
        setPreferredSize(new Dimension(
                GuiConstants.rowWidth35 - GuiConstants.boxPadding,
                (GuiConstants.rowHeight75*2) + (GuiConstants.boxPadding * 2)
        ));

        val pipe = services.getUserConfigService().findPipeById(pipeId);
        val isPaused = pipe.isPaused();
        val activeActionsCount = pipe.getBindingRows().stream().filter(BindingRowDto::isActive).count();

        if(isPaused){
            this.value = new JLabel("Bindings: paused");
        }else if(activeActionsCount > 0){
            this.value = new JLabel("Bindings: running...");
        }else {
            this.value = new JLabel("Bindings: idle");
        }

        casValue = new JLabel("CAS: idle");
        add(this.value);
        add(casValue);
    }


    public void updateValue(String value) {
        this.value.setText(value);
    }
    public void updateCASValue(String value) {
        this.casValue.setText(value);
    }
}
