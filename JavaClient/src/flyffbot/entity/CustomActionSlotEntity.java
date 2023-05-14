package flyffbot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "customActionSlot")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomActionSlotEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private String hexKeyCode0;
    private String hexKeyCode1;
    private long castTimeMs;
    private long pipelineId;
    private long lastTimeExecutedMs;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "pipelineId", referencedColumnName = "id", insertable = false, updatable = false)
//    private PipelineEntity pipeline;
}
