package flyffbot.services;

import flyffbot.entity.HotkeyEntity;
import flyffbot.exceptions.HotkeyNotFound;
import flyffbot.repositories.HotkeyRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class HotkeyServiceImpl {
    @Autowired
    private HotkeyRepository repository;

    @Autowired
    private EventsServiceImpl eventsService;

    public HotkeyEntity addHotkey(long pipelineId) {
        val hotkey = repository.save(
                HotkeyEntity.builder()
                        .pipelineId(pipelineId)
                        .hexKeyCode0("")
                        .hexKeyCode1("")
                        .delayMs(500L)
                        .active(false)
                        .lastTimeExecutedMs(0L)
                        .build()
        );
        eventsService.scheduleSingleHotkey(hotkey);
        return hotkey;
    }

    public void updateDelay(Long hotkeyId, Long delayMs) {
        repository.updateDelayMsById(hotkeyId, delayMs);
        val updated = repository.findById(hotkeyId)
                .orElseThrow();
        log.debug("[1/3] - Hotkey delay update - rescheduling");
        eventsService.cancelSingleHotkey(hotkeyId);
        log.debug("[2/3] - Hotkey delay update - cancelled previews task with old hotkey config");
        eventsService.scheduleSingleHotkey(updated);
        log.debug("[3/3] - Hotkey delay update - scheduled new task with updated hotkey config");
    }
    public void updateActive(Long hotkeyId, boolean active) {
        repository.updateActiveById(hotkeyId, active);
        if(active){
            val updated = repository.findById(hotkeyId)
                    .orElseThrow();
            eventsService.scheduleSingleHotkey(updated);
            log.debug("Hotkey scheduled after active update");
        } else {
            eventsService.cancelSingleHotkey(hotkeyId);
            log.debug("Hotkey cancelled after active update");
        }
    }

    public void updateHexKeyCode(Long id, int keyIndex, String hexValue){
        switch (keyIndex) {
            case 0:
                repository.updateHexKeyCode0ById(id, hexValue);
                break;
            case 1:
                repository.updateHexKeyCode1ById(id, hexValue);
                break;
            default:
                throw new HotkeyNotFound("Invalid key index, allowed: 0=first key, 1=second key");
        }
    }

    public void deleteById(long id) {
        repository.deleteById(id);
        eventsService.cancelSingleHotkey(id);
        log.debug("Hotkey delete - task removed");
    }

    public void updateLastTimeExecutedMs(long id, long lastTimeExecutedMs){
        repository.updateLastTimeExecutedMs(id, lastTimeExecutedMs);
    }
    public void updateExecuting(Long id, boolean executing) {
        repository.updateExecuting(id, executing);
    }

    public Optional<HotkeyEntity> findById(long id) {
        return repository.findById(id);
    }

    public List<HotkeyEntity> findAllActive() {
        return repository.findAllActive();
    }

    public List<HotkeyEntity> findByPipelineId(long pipelineId) {
        return repository.findByPipelineId(pipelineId);
    }

    public void disableAll(){
        repository.disableAll();
    }
}
