package flyffbot.server.services;

import flyffbot.server.actions.HotkeyActionRunner;
import flyffbot.server.entity.HotkeyEntity;
import flyffbot.server.enums.KeyStatus;
import flyffbot.server.services.nativeservices.NativeSendKeyService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class EventsServiceImpl {
    @Autowired
    private PipelineServiceImpl pipelineService;
    @Autowired
    private HotkeyServiceImpl hotkeyService;
    @Autowired
    private CustomActionSlotServiceImpl customActionSlotService;

    @Autowired
    private NativeSendKeyService nativeSendKeyService;

    @Autowired
    private ScheduledExecutorService executorService;

    @Autowired
    private SocketMessageSenderServiceImpl socketMessageSenderService;

    private AtomicReference<Map<Long, ScheduledFuture<?>>> scheduledTasks;

    @PostConstruct
    private void postConstruct(){
        scheduledTasks = new AtomicReference<>(new HashMap<>());
    }
    public void scheduleSingleHotkey(HotkeyEntity entity) {
        scheduledTasks.getAndUpdate(map -> {
            map.putIfAbsent(entity.getId(), buildHotkeyRunnerCompletableFuture(entity));
            return map;
        });
    }

    public void cancelSingleHotkey(long hotkeyId) {
        scheduledTasks.getAndUpdate(map -> {
            Optional.ofNullable(map.remove(hotkeyId))
                    .ifPresent(ref -> ref.cancel(true));
            return map;
        });
    }
    private ScheduledFuture<?> buildHotkeyRunnerCompletableFuture(HotkeyEntity entity) {
        val task = new HotkeyActionRunner(
                entity.getId(),
                pipelineService,
                hotkeyService,
                nativeSendKeyService,
                executorService,
                scheduledTasks
        );
        return executorService.schedule(task, entity.getDelayMs(), TimeUnit.MILLISECONDS);
    }

    public void scheduleCustomActionSlot(long pipelineId){
        val pipeline = pipelineService.findPipeById(pipelineId);
        if(pipeline.isCustomActionSlotRunning()){
            return;
        }
        pipelineService.updateCustomActionSlotRunning(pipelineId, true);
        socketMessageSenderService.sendUpdatedConfiguration();
        val actions = customActionSlotService.findByPipelineId(pipelineId);
        val hwnd = pipeline.getSelectedWindowHwnd();
        for(val cas : actions){
            val keys = Stream.of(cas.getHexKeyCode0(), cas.getHexKeyCode1())
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList());

            nativeSendKeyService.execute(KeyStatus.DOWN, hwnd, keys);
            nativeSendKeyService.execute(KeyStatus.UP, hwnd, keys);
            safeSleep(cas.getCastTimeMs(), "Error occurred while waiting cast time for custom action slot" + cas);
        }
        pipelineService.updateCustomActionSlotRunning(pipelineId, false);
        socketMessageSenderService.sendUpdatedConfiguration();
    }

    private void safeSleep(long time, String error){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e){
            log.error(error, e);
        }
    }
}
