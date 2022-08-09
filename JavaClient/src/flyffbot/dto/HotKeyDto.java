package flyffbot.dto;

import flyffbot.enums.EventEnum;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class HotKeyDto {
    private EventEnum event;
    private Set<Integer> keys;
    private String pipeId;
}
