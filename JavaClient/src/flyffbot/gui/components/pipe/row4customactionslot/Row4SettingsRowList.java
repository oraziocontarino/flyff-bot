package flyffbot.gui.components.pipe.row4customactionslot;

import flyffbot.configuration.DepInjComponentData;
import flyffbot.dto.comboitem.FBComboStringDto;
import flyffbot.entity.CustomActionSlotSkillDto;
import flyffbot.gui.GuiConstants;
import flyffbot.gui.listeners.CustomActionSlotListener;
import flyffbot.interfaces.UICallback;
import lombok.val;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Row4SettingsRowList extends JScrollPane {
    private String pipeId;
    private DepInjComponentData services;
    private JPanel content;
    private List<Row4SettingsRow> rows;
    private CustomActionSlotListener listener;


    public Row4SettingsRowList(String pipeId, DepInjComponentData services) {
        super();
        this.pipeId = pipeId;
        this.services = services;
        initContent();


        setViewportView(content);
        getViewport().setPreferredSize(new Dimension(
                GuiConstants.rowWidth - (GuiConstants.boxPadding*4) - GuiConstants.scrollbarWidth,
                (GuiConstants.rowHeight*7) - GuiConstants.boxPadding
        ));
        setAutoscrolls(true);
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        setBorder(null);
    }


    public void addCustomActionSlotSkill() {
        val cfg = services.getUserConfigService().addCustomActionSlotSkill(pipeId);
        addCustomActionSlotSkillByCfg(cfg);
    }

    public void removeCustomActionSlotSkill() {
        if(rows.size() == 0){
            return;
        }
        services.getUserConfigService().removeCustomActionSlotSkill(pipeId).ifPresent(cid -> {
            val size = rows.size();
            val toRemove = rows.get(size - 1);
            content.remove(toRemove);
            rows.remove(size - 1);
            updateContentSize(rows.size());
        });
    }


    private void initContent(){
        val cfgList = services.getUserConfigService()
                .findPipeById(pipeId)
                .getCustomActionSlotSkills();
        val size = cfgList.size();

        content = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, GuiConstants.padding));
        content.setPreferredSize(new Dimension(
                GuiConstants.rowWidth - (GuiConstants.boxPadding*4) - GuiConstants.scrollbarWidth,
                (GuiConstants.rowHeight75 + GuiConstants.padding)*size + GuiConstants.boxPadding
        ));
        rows = new ArrayList<>();
        listener = new CustomActionSlotListener(){

            @Override
            public void onKey0Change(String pipeId, String cid, String value) {
                services.getUserConfigService().updateCASKey0(pipeId, cid, value);
            }

            @Override
            public void onKey1Change(String pipeId, String cid, String value) {
                services.getUserConfigService().updateCASKey1(pipeId, cid, value);
            }

            @Override
            public void onCastTimeChange(String pipeId, String cid, int value) {
                services.getUserConfigService().updateCASCastTime(pipeId, cid, value);
            }
        };


        cfgList.forEach(this::addCustomActionSlotSkillByCfg);
        updateContentSize(size);
    }

    private void updateContentSize(int count){
        content.setPreferredSize(new Dimension(
                GuiConstants.rowWidth - (GuiConstants.boxPadding*4) - GuiConstants.scrollbarWidth,
                (GuiConstants.rowHeight75 + GuiConstants.padding)*count + GuiConstants.boxPadding
        ));
        setViewportView(content);
    }

    private void addCustomActionSlotSkillByCfg(CustomActionSlotSkillDto cfg){
        val item = new Row4SettingsRow(pipeId, cfg, listener);
        rows.add(item);
        content.add(item);
        updateContentSize(rows.size());
    }

    private UICallback<FBComboStringDto> buildListener(){
        return payload -> {
//            log.debug("hello wolrd!");
        };
    }
}
