package flyffbot.gui.components.pipe.row1statushotkeys;

import flyffbot.configuration.DepInjComponentData;
import flyffbot.gui.GuiConstants;
import flyffbot.gui.components.JFBPanel;
import lombok.val;

import java.awt.*;

public class Row1StatusHotKeys extends JFBPanel {
    private DepInjComponentData services;
    private BotStatusBox status;
    private HotKeysBox hotkeys;

    public Row1StatusHotKeys(String pipeId, DepInjComponentData services) {
        super("", pipeId);
        this.services = services;
        val layout = new FlowLayout(FlowLayout.LEFT, 0, 0);
        layout.setAlignOnBaseline(true);
        setLayout(layout);

        setPreferredSize(new Dimension(
                GuiConstants.rowWidth - (GuiConstants.boxPadding*2),
                (GuiConstants.rowHeight75*3) + GuiConstants.padding
        ));

        status = new BotStatusBox(pipeId, services);

        val index = services.getUserConfigService().findIndex(pipeId);
        hotkeys = new HotKeysBox(
                pipeId,
                "Pipe HotKeys",
                "Toggle pause: Shift + " + ((index * 3) + 1),
                "Use action slot: Shift + " + ((index * 3) + 2),
                "Use custom action slot: Shift + " + ((index * 3) + 3)
        );
        add(hotkeys);
        add(status);
    }

    public void updateBotStatus(String value) {
        status.updateValue(value);
    }
    public void updateBotCASStatus(String value) {
        status.updateCASValue(value);
    }
}
