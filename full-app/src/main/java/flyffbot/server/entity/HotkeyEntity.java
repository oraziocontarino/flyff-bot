package flyffbot.server.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity(name = "hotkey")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotkeyEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private String hexKeyCode0;
    private String hexKeyCode1;
    private long delayMs;
    private boolean active;
    private long lastTimeExecutedMs;
    private long pipelineId;
    private boolean executing;
}
