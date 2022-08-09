package flyffbot.dto.comboitem;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
abstract class FBComboItemDto<T> {
    protected String label;
    protected T value;
}
