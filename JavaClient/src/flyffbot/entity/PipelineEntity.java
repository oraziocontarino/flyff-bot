package flyffbot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "pipeline")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PipelineEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private String selectedWindowHwnd;
    private String selectedWindowName;
    private boolean paused;
    private boolean customActionSlotRunning;

//    @OneToMany(mappedBy = "pipeline")
//    private List<HotkeyEntity> hotkeys;
//
//    @OneToMany(mappedBy = "pipeline")
//    private List<CustomActionSlotEntity> customActionSlots;
}
