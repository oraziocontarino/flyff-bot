package flyffbot.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BindingRowDto {
    private String id;
    private String hexKeyCode0;
    private String hexKeyCode1;
    private Integer delay;
    private boolean active;
    private long lastTimeExecuted;
}
