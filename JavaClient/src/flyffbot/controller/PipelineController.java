package flyffbot.controller;

import flyffbot.dto.PipelineWithHotkeysAndCustomActionSlots;
import flyffbot.dto.UpdateSelectedWindowRequestDto;
import flyffbot.dto.nativeapi.WindowItem;
import flyffbot.services.CustomActionSlotServiceImpl;
import flyffbot.services.HotkeyServiceImpl;
import flyffbot.services.PipelineServiceImpl;
import flyffbot.services.nativeservices.NativeGetWindowListService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PipelineController {
    @Autowired
    private PipelineServiceImpl pipelineService;
    @Autowired
    private HotkeyServiceImpl hotkeyService;
    @Autowired
    private CustomActionSlotServiceImpl customActionSlotService;

    @Autowired
    private NativeGetWindowListService nativeGetWindowListService;

    @GetMapping("/configuration")
    public ResponseEntity<List<PipelineWithHotkeysAndCustomActionSlots>> fetchConfiguration(){
        return ResponseEntity.ok(
                pipelineService.findAllPipes().stream().map(pipe ->
                        PipelineWithHotkeysAndCustomActionSlots.builder()
                                .pipeline(pipe)
                                .hotkeys(hotkeyService.findByPipelineId(pipe.getId()))
                                .customActionSlotEntities(customActionSlotService.findByPipelineId(pipe.getId()))
                                .build()
                ).collect(Collectors.toList())
        );
    }

    @GetMapping("/windows")
    public ResponseEntity<List<WindowItem>> fetchWindowList(){
        val response = nativeGetWindowListService.execute();
        return ResponseEntity.ok(response.getData());
    }

    @PutMapping("/windows")
    public ResponseEntity<Boolean> updateSelectedWindow(@RequestBody UpdateSelectedWindowRequestDto args){
        pipelineService.saveSelectedWindowHwnd(args.getConfigurationId(), args.getHwnd());
        return ResponseEntity.ok(true);
    }

}
