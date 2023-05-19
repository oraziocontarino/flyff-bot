package flyffbot.server.controller;

import flyffbot.server.configuration.WebSocketConfig;
import flyffbot.server.dto.hotkey.*;
import flyffbot.server.dto.pipeline.ConfigurationDto;
import flyffbot.server.services.ConfigurationServiceImpl;
import flyffbot.server.services.EventsServiceImpl;
import flyffbot.server.services.HotkeyServiceImpl;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Slf4j
public class HotkeyController {
    @Value("${socket.topics.updated-configuration}")
    private String updatedConfigurationTopic;
    @Autowired
    private HotkeyServiceImpl hotkeyService;

    @Autowired
    private ConfigurationServiceImpl configurationService;
    @Autowired
    private EventsServiceImpl eventsService;

    @MessageMapping("/post-hotkey")
    @SendTo(WebSocketConfig.UPDATED_CONFIGURATIONS_TOPIC)
    public List<ConfigurationDto> updateSelectedWindow(CreateHotkeyDto request) {
        val hotkey = hotkeyService.addHotkey(request.getPipelineId());
        eventsService.scheduleSingleHotkey(hotkey);
        return configurationService.retrieveConfiguration();
    }

    @MessageMapping("/put-hotkey-hex-key-code")
    @SendTo(WebSocketConfig.UPDATED_CONFIGURATIONS_TOPIC)
    public List<ConfigurationDto> updateHexKeyCode(UpdateHexKeyCodeRequestDto request) {
        hotkeyService.updateHexKeyCode(request.getId(), request.getKeyIndex(), request.getHexKeyCode());
        return configurationService.retrieveConfiguration();
    }

    @MessageMapping("/put-hotkey-delay")
    @SendTo(WebSocketConfig.UPDATED_CONFIGURATIONS_TOPIC)
    public List<ConfigurationDto> updateDelay(UpdateDelayRequestDto request) {
        val updated = hotkeyService.updateDelay(request.getId(), request.getDelayMs());
        log.debug("[1/3] - Hotkey delay update - rescheduling");
        eventsService.cancelSingleHotkey(request.getId());
        log.debug("[2/3] - Hotkey delay update - cancelled previews task with old hotkey config");
        eventsService.scheduleSingleHotkey(updated);
        log.debug("[3/3] - Hotkey delay update - scheduled new task with updated hotkey config");
        return configurationService.retrieveConfiguration();
    }

    @MessageMapping("/put-hotkey-active")
    @SendTo(WebSocketConfig.UPDATED_CONFIGURATIONS_TOPIC)
    public List<ConfigurationDto> updateActive(UpdateActiveRequestDto request) {
        val hotkeyId = request.getId();
        val active = request.isActive();
        val updated = hotkeyService.updateActive(request.getId(), active);
        if(active){
            eventsService.scheduleSingleHotkey(updated);
            log.debug("Hotkey scheduled after active update");
        } else {
            eventsService.cancelSingleHotkey(hotkeyId);
            log.debug("Hotkey cancelled after active update");
        }
        return configurationService.retrieveConfiguration();
    }


    @MessageMapping("/delete-hotkey")
    @SendTo(WebSocketConfig.UPDATED_CONFIGURATIONS_TOPIC)
    public List<ConfigurationDto> deleteHotkey(DeleteHotkeyDto request) {
        val id = request.getId();
        hotkeyService.deleteById(request.getId());
        eventsService.cancelSingleHotkey(id);
        return configurationService.retrieveConfiguration();
    }
}
