package flyffbot.gui.components.pipe.row3bindingrows;

import flyffbot.configuration.DepInjComponentData;
import flyffbot.gui.GuiConstants;
import flyffbot.gui.components.JFBPanel;
import flyffbot.gui.listeners.BindingRowEventsListener;
import flyffbot.utils.Utils;
import lombok.val;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Row3ScrollablePanel extends JScrollPane {
    private Row3BindingRows content;

    public Row3ScrollablePanel(String pipeId, DepInjComponentData services, BindingRowEventsListener events) {
        super();
        initContent(pipeId, services, events);

        setViewportView(content);
        getViewport().setPreferredSize(new Dimension(
                GuiConstants.rowWidth - (GuiConstants.boxPadding*7),
                (GuiConstants.rowHeight*3) + GuiConstants.boxPadding
        ));
        setAutoscrolls(true);
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        setBorder(BorderFactory.createTitledBorder("Binding rows"));
    }

    private void initContent(String pipeId, DepInjComponentData services, BindingRowEventsListener events) {
        content = new Row3BindingRows(pipeId, services, events);
    }

    public void addBindingRow() {
        content.addBindingRow();
        setViewportView(content);
    }

    public void removeBindingRow(String bindingRowId) {
        content.removeBindingRow(bindingRowId);
        setViewportView(content);
    }

    public void updateScheduler(String cid, int value) {
        content.updateScheduler(cid, value);
        setViewportView(content);
    }
}
