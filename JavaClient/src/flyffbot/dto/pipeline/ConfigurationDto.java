package flyffbot.dto.pipeline;

import flyffbot.entity.CustomActionSlotEntity;
import flyffbot.entity.HotkeyEntity;
import flyffbot.entity.PipelineEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationDto {
    private PipelineEntity pipeline;
    private List<HotkeyEntity> hotkeys;
    private List<CustomActionSlotEntity> customActionSlots;
}
