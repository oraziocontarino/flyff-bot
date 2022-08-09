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

public class Row3BindingRows extends JFBPanel {
    private DepInjComponentData services;
    private BindingRowEventsListener events;
    private List<FBBindingRow> stack;

    public Row3BindingRows(String pipeId, DepInjComponentData services, BindingRowEventsListener events) {
        super("", pipeId);
        this.services = services;
        this.events = events;
        val layout = new FlowLayout(FlowLayout.LEFT);
        layout.setAlignOnBaseline(true);
        setLayout(layout);


        stack = services.getUserConfigService().findPipeById(pipeId)
                .getBindingRows()
                .stream()
                .map(cfg -> new FBBindingRow(pipeId, services, cfg, events))
                .collect(Collectors.toList());

        stack.forEach(this::add);

        updateSize();
    }

    private void updateSize() {
        setPreferredSize(new Dimension(
                GuiConstants.rowWidth - (GuiConstants.boxPadding*2),
                (GuiConstants.rowHeight*(stack.size() - 1)) + (GuiConstants.boxPadding * 2)
        ));
    }

    public void addBindingRow() {
        val cfg = services.getUserConfigService().addBindingRow(pipeId);
        Optional.ofNullable(stack).ifPresent(list -> {
            val row = new FBBindingRow(pipeId, services, cfg, events);
            list.add(row);
            add(row);
        });
        updateSize();
    }

    public void removeBindingRow(String bindingRowId) {
        Utils.findIndex(stack, item -> item.getId().equals(bindingRowId)).ifPresent(i -> {
            val row = stack.get(i);
            stack.remove(i.intValue());
            remove(row);
        });
        updateSize();
    }

    public void updateScheduler(String cid, int value) {
        stack.stream().filter(item -> item.getId().equals(cid))
                .findFirst()
                .ifPresent(item -> item.updateScheduler(value));

    }

    public int getContentHeight(){
        if(stack.isEmpty()){
            return 0;
        }

        return stack.get(0).getPreferredSize().height * stack.size();
    }
}
