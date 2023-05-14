package flyffbot.controller;

import flyffbot.configuration.WebSocketConfig;
import flyffbot.dto.pipeline.ConfigurationDto;
import flyffbot.dto.pipeline.UpdateSelectedWindowRequestDto;
import flyffbot.services.ConfigurationServiceImpl;
import flyffbot.services.PipelineServiceImpl;
import flyffbot.services.SocketMessageSenderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class PipelineController {
    @Value("${socket.topics.updated-configuration}")
    private String updatedConfigurationTopic;
    @Autowired
    private PipelineServiceImpl pipelineService;
    @Autowired
    private ConfigurationServiceImpl configurationService;

    @Autowired
    private SocketMessageSenderServiceImpl socketMessageSenderService;

    @MessageMapping("/get-configuration")
    @SendTo(WebSocketConfig.UPDATED_CONFIGURATIONS_TOPIC)
    public List<ConfigurationDto> retrieveConfiguration() {
        return configurationService.retrieveConfiguration();
    }

    @MessageMapping("/put-selected-window")
    @SendTo(WebSocketConfig.UPDATED_CONFIGURATIONS_TOPIC)
    public List<ConfigurationDto> updateSelectedWindow(UpdateSelectedWindowRequestDto args) {
        pipelineService.saveSelectedWindowHwnd(args.getPipelineId(), args.getHwnd());
        return configurationService.retrieveConfiguration();
    }

}