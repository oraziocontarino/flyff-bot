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

    public FBComboStringDto updateOptions(List<FBComboStringDto> newOptions, String defaultText){
        val oldSelected = this.options.get(this.getSelectedIndex());

        replaceOptionsList(newOptions, defaultText);

        val selectedIndex = updateSelectedByValue(oldSelected.getValue());
        return this.options.get(selectedIndex);
    }

    private void replaceOptionsList(List<FBComboStringDto> newList, String defaultText){
        this.removeAllItems();
        this.options = newList;
        if(options.isEmpty()) {
            options.add(0, FBComboStringDto.builder().label(defaultText).value("").build());
        }
        for(int i = 0; i < options.size(); i++){
            this.insertItemAt(options.get(i), i);
        }
    }

    private int updateSelectedByValue(String toSelect){
        int selectedIndex = 0;
        for(int i = 0; i < options.size(); i++){
            val item = options.get(i);
            if(toSelect.equals(item.getValue())){
                selectedIndex = i;
            }
        }
        this.setSelectedIndex(selectedIndex);
        return selectedIndex;
    }
}
