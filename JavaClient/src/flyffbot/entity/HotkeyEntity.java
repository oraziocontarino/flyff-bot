package flyffbot.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;

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

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "pipelineId", referencedColumnName = "id", insertable = false, updatable = false)
//    private PipelineEntity pipeline;
}
