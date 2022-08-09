package flyffbot.entity;

import flyffbot.enums.PipeStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PipeDto {
    private String id;
    private WindowNameRowDto windowNameRow;
    private boolean isPaused;
    private boolean isCustomActionSlotRunning;
    private List<BindingRowDto> bindingRows;
    private List<CustomActionSlotSkillDto> customActionSlotSkills;
}
