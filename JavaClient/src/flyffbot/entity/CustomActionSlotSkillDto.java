package flyffbot.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomActionSlotSkillDto {
    private String id;
    private String hexKeyCode0;
    private String hexKeyCode1;
    private Integer castTime;
}
