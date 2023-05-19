package flyffbot.server.actions;

import flyffbot.server.entity.HotkeyEntity;
import flyffbot.server.enums.KeyStatus;
import flyffbot.server.services.HotkeyServiceImpl;
import flyffbot.server.services.PipelineServiceImpl;
import flyffbot.server.services.nativeservices.NativeSendKeyService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Slf4j
public class HotkeyActionRunner implements Runnable {
    private long id;
    private PipelineServiceImpl pipelineService;
    private HotkeyServiceImpl hotkeyService;
    private NativeSendKeyService nativeSendKeyService;
    private ScheduledExecutorService executorService;

    private AtomicReference<Map<Long, ScheduledFuture<?>>> scheduledTasks;

    @Override
    public void run() {
        val hotkeyOpt = hotkeyService.findById(id);
        if(hotkeyOpt.isEmpty()){
            log.info("Hotkey removed from runners chain {}", hotkeyOpt);
            return;
        }

        val hotkey = hotkeyOpt.get();
        val pipeline = pipelineService.findPipeById(hotkey.getPipelineId());
        val now = System.currentTimeMillis();
        val delta = now - hotkey.getLastTimeExecutedMs();

        // hotkey.isActive should always be true
        // Rest controller remove from executor inactive hotkeys!
        if(!hotkey.isActive()){
            log.error("Current action is running in executor but is inactive! Must be removed from executor!");
            return;
        }

        if(delta < hotkey.getDelayMs()){
            log.error("Current schedule is running in executor before delayed time! Should never happen!");
            scheduleNextRun(hotkey);
            return;
        }

        if(pipeline.isPaused() || StringUtils.isBlank(pipeline.getSelectedWindowHwnd())){
            scheduleNextRun(hotkey);
            return;
        }

        log.debug("Checking hotkey: {}", hotkey);
        // Lock for next schedule scan
        hotkeyService.updateExecuting(hotkey.getId(), true);

        val hwnd = pipeline.getSelectedWindowHwnd();
        val keys = Stream.of(hotkey.getHexKeyCode0(), hotkey.getHexKeyCode1())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());

        nativeSendKeyService.execute(KeyStatus.DOWN, hwnd, keys);

        nativeSendKeyService.execute(KeyStatus.UP, hwnd, keys);

        hotkeyService.updateLastTimeExecutedMs(hotkey.getId(), now);

        // Unlock for next schedule scan
        hotkeyService.updateExecuting(hotkey.getId(), false);
        log.debug("total time: {}ms", System.currentTimeMillis() - now);

        scheduleNextRun(hotkey);
    }

    public void scheduleNextRun(HotkeyEntity hotkey) {
        scheduledTasks.getAndUpdate(map -> {
            map.put(
                    hotkey.getId(),
                    executorService.schedule(this, hotkey.getDelayMs(), TimeUnit.MILLISECONDS)
            );
            return map;
        });
    }
}
