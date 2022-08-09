package flyffbot.utils;

import flyffbot.dto.comboitem.FBComboStringDto;
import lombok.val;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Utils {
    public static <T> Optional<Integer> findIndex(List<T> list, Predicate<T> predicate){
        for(int i = 0; i < list.size(); i++){
            if(Optional.of(list.get(i)).filter(predicate).isPresent()){
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    public static String toHexString(int hexValue){
        return hexValue == -1 ? "" : "0x"+Integer.toHexString(hexValue);
    }


    public static List<FBComboStringDto> buildAltCtrlList(){
        return List.of(
                Utils.buildComboKeyItem("...", -1),
                Utils.buildComboKeyItem("CTRL", KeyEvent.VK_CONTROL),
                Utils.buildComboKeyItem("ALT", KeyEvent.VK_ALT)
        );
    }

    public static List<FBComboStringDto> build09List() {
        return List.of(
                Utils.buildComboKeyItem("1", KeyEvent.VK_1),
                Utils.buildComboKeyItem("2", KeyEvent.VK_2),
                Utils.buildComboKeyItem("3", KeyEvent.VK_3),
                Utils.buildComboKeyItem("4", KeyEvent.VK_4),
                Utils.buildComboKeyItem("5", KeyEvent.VK_5),
                Utils.buildComboKeyItem("6", KeyEvent.VK_6),
                Utils.buildComboKeyItem("7", KeyEvent.VK_7),
                Utils.buildComboKeyItem("8", KeyEvent.VK_8),
                Utils.buildComboKeyItem("9", KeyEvent.VK_9),
                Utils.buildComboKeyItem("0", KeyEvent.VK_0)
        );
    }

    private static FBComboStringDto buildComboKeyItem(String label, Integer vkKeyEvent){
        return FBComboStringDto.builder()
                .label(label)
                .value(toHexString(vkKeyEvent))
                .build();
    }
}
