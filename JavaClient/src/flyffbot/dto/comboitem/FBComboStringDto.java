package flyffbot.dto.comboitem;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper=true)
public class FBComboStringDto extends FBComboItemDto<String> {
    public FBComboStringDto(String label, String value){
        super(label, value);
    }
}
