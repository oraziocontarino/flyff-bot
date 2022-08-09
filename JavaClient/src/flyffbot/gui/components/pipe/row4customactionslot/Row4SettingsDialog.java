package flyffbot.gui.components.pipe.row4customactionslot;

import flyffbot.configuration.DepInjComponentData;
import flyffbot.dto.comboitem.FBComboStringDto;
import flyffbot.gui.GuiConstants;
import flyffbot.gui.components.EmptyItem;
import flyffbot.gui.components.JFBPanel;
import flyffbot.gui.listeners.PipeSettingsListener;
import flyffbot.interfaces.UICallback;
import lombok.val;

import javax.swing.*;
import java.awt.*;

public class Row4SettingsDialog extends JFBPanel {
    private DepInjComponentData services;
    private JLabel headerKey;
    private JLabel headerCastTime;
    private JButton headerAddRow;
    private JButton headerRemoveRow;

    private Row4SettingsRowList bodyRows;
    private JButton footerClose;
    public Row4SettingsDialog(String pipeId, DepInjComponentData services, PipeSettingsListener pipeSettingsListener) {
        super("", pipeId);
        this.services = services;
        val layout = new FlowLayout(FlowLayout.LEFT, 0, GuiConstants.padding);
        layout.setAlignOnBaseline(true);
        setLayout(layout);

        setPreferredSize(new Dimension(
                GuiConstants.rowWidth - (GuiConstants.boxPadding*2),
                (GuiConstants.rowHeightLg*7) + GuiConstants.boxPadding
        ));
        val actionsSize = new Dimension(GuiConstants.customActionSlotActionWidth, GuiConstants.rowHeight75);

        headerKey = new JLabel("Keys");
        headerKey.setPreferredSize(new Dimension((GuiConstants.customActionSlotColumnWidth*2)+GuiConstants.padding, GuiConstants.rowHeight75));
        headerKey.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        headerCastTime = new JLabel("Cast time (ms)");
        headerCastTime.setPreferredSize(new Dimension(GuiConstants.customActionSlotColumnWidth, GuiConstants.rowHeight75));
        headerCastTime.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        headerAddRow = new JButton("+");
        headerAddRow.setPreferredSize(actionsSize);
        headerAddRow.addActionListener(e -> addSkillSettings());

        headerRemoveRow = new JButton("-");
        headerRemoveRow.setPreferredSize(actionsSize);
        headerRemoveRow.addActionListener(e -> removeSkillSettings());

        bodyRows = new Row4SettingsRowList(pipeId, services);

        footerClose = new JButton("Save and close");
        footerClose.addActionListener(e -> pipeSettingsListener.saveAndClose());
        footerClose.setPreferredSize(new Dimension(GuiConstants.customActionSlotColumnWidth*2, GuiConstants.rowHeight75));

        add(headerKey);
        add(new EmptyItem(GuiConstants.padding * 4, GuiConstants.rowHeight50));
        add(headerCastTime);
        add(new EmptyItem(GuiConstants.padding * 4, GuiConstants.rowHeight50));
        add(headerAddRow);
        add(new EmptyItem(GuiConstants.padding, GuiConstants.rowHeight50));
        add(headerRemoveRow);
        add(bodyRows);
        add(new EmptyItem(GuiConstants.rowWidth - (GuiConstants.customActionSlotColumnWidth * 2) - (GuiConstants.padding*6), GuiConstants.rowHeight50));
        add(footerClose);
        setBorder(BorderFactory.createTitledBorder("Configure custom action slot"));
        setVisible(false);
    }

    private void addSkillSettings(){
        bodyRows.addCustomActionSlotSkill();
    }

    private void removeSkillSettings(){
        bodyRows.removeCustomActionSlotSkill();
    }
}
