package flyffbot.controller;

import flyffbot.configuration.WebSocketConfig;
import flyffbot.dto.hotkey.*;
import flyffbot.dto.pipeline.ConfigurationDto;
import flyffbot.services.ConfigurationServiceImpl;
import flyffbot.services.HotkeyServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class HotkeyController {
    @Value("${socket.topics.updated-configuration}")
    private String updatedConfigurationTopic;
    @Autowired
    private HotkeyServiceImpl hotkeyService;

    @Autowired
    private ConfigurationServiceImpl configurationService;

    @MessageMapping("/post-hotkey")
    @SendTo(WebSocketConfig.UPDATED_CONFIGURATIONS_TOPIC)
    public List<ConfigurationDto> updateSelectedWindow(CreateHotkeyDto request) {
        hotkeyService.addHotkey(request.getPipelineId());
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
        hotkeyService.updateDelay(request.getId(), request.getDelayMs());
        return configurationService.retrieveConfiguration();
    }

    @MessageMapping("/put-hotkey-active")
    @SendTo(WebSocketConfig.UPDATED_CONFIGURATIONS_TOPIC)
    public List<ConfigurationDto> updateActive(UpdateActiveRequestDto request) {
        hotkeyService.updateActive(request.getId(), request.isActive());
        return configurationService.retrieveConfiguration();
    }


    @MessageMapping("/delete-hotkey")
    @SendTo(WebSocketConfig.UPDATED_CONFIGURATIONS_TOPIC)
    public List<ConfigurationDto> deleteHotkey(DeleteHotkeyDto request) {
        hotkeyService.deleteById(request.getId());
        return configurationService.retrieveConfiguration();
    }
}
