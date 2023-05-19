package flyffbot.server.services;

import flyffbot.server.entity.HotkeyEntity;
import flyffbot.server.exceptions.HotkeyNotFound;
import flyffbot.server.repositories.HotkeyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class HotkeyServiceImpl {
    @Autowired
    private HotkeyRepository repository;

    public HotkeyEntity addHotkey(long pipelineId) {
        return repository.save(
                HotkeyEntity.builder()
                        .pipelineId(pipelineId)
                        .hexKeyCode0("")
                        .hexKeyCode1("")
                        .delayMs(500L)
                        .active(false)
                        .lastTimeExecutedMs(0L)
                        .build()
        );
    }

    public HotkeyEntity updateDelay(Long hotkeyId, Long delayMs) {
        repository.updateDelayMsById(hotkeyId, delayMs);
        return repository.findById(hotkeyId)
                .orElseThrow();
    }
    public HotkeyEntity updateActive(Long hotkeyId, boolean active) {
        repository.updateActiveById(hotkeyId, active);
        return repository.findById(hotkeyId).orElseThrow();
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
