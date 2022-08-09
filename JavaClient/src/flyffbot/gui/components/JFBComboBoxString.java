package flyffbot.gui.components;

import flyffbot.dto.comboitem.FBComboStringDto;
import flyffbot.interfaces.UICallback;
import lombok.val;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;

public class JFBComboBoxString extends JComboBox<FBComboStringDto> {
    private List<FBComboStringDto> options;
    public JFBComboBoxString(
            List<FBComboStringDto> options,
            String defaultSelectedValue,
            UICallback<FBComboStringDto> onChangeCallback
    ){
        super(options.toArray(new FBComboStringDto[0]));
        this.options = options;

        this.setRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(
                    JList list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(value instanceof FBComboStringDto){
                    val item = (FBComboStringDto) value;
                    setText(item.getLabel());
                }
                return this;
            }
        });

        this.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED){
                onChangeCallback.run(this.options.get(this.getSelectedIndex()));
            }
        });

        updateSelectedByValue(defaultSelectedValue);
    }

    public FBComboStringDto updateOptions(List<FBComboStringDto> options){
        val oldSelected = this.options.get(this.getSelectedIndex());
        this.removeAllItems();
        this.options = options;

        if(this.options.size() == 0) {
            this.options.add(0, FBComboStringDto.builder().label("No element available").value("").build());
        }

        val selectedIndex = updateSelectedByValue(oldSelected.getValue());
        return this.options.get(selectedIndex);
    }

    private int updateSelectedByValue(String toSelect){
        int selectedIndex = 0;
        for(int i = 0; i < this.options.size(); i++){
            val item = this.options.get(i);
            this.insertItemAt(item, i);
            if(toSelect.equals(item.getValue())){
                selectedIndex = i;
            }
        }
        this.setSelectedIndex(selectedIndex);
        return selectedIndex;
    }
}
