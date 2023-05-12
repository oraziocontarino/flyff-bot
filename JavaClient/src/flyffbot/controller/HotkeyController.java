package flyffbot.controller;

import flyffbot.dto.UpdateHexKeyCodeRequestDto;
import flyffbot.dto.UpdateHotkeyActiveRequest;
import flyffbot.dto.UpdateHotkeyDelayRequestDto;
import flyffbot.services.EventsServiceImpl;
import flyffbot.services.HotkeyServiceImpl;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("hotkeys")
public class HotkeyController {
    @Autowired
    private HotkeyServiceImpl hotkeyService;


    @PostMapping("/{pipelineId}")
    public ResponseEntity<Void> create(@PathVariable long pipelineId) {
        hotkeyService.addHotkey(pipelineId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/key/{keyIndex}")
    public ResponseEntity<Void> updateHexValue(
            @PathVariable long id,
            @PathVariable int keyIndex, //0=first key, 1=second key
            @RequestBody UpdateHexKeyCodeRequestDto body
    ){
        hotkeyService.updateHexKeyCode(id, keyIndex, body.getHexKeyCode());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/delay")
    public ResponseEntity<Void> updateDelay(@PathVariable long id, @RequestBody UpdateHotkeyDelayRequestDto body){
        hotkeyService.updateDelay(id, body.getDelayMs());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/active")
    public ResponseEntity<Void> updateActive(@PathVariable long id, @RequestBody UpdateHotkeyActiveRequest body){
        hotkeyService.updateActive(id, body.isActive());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotKeys(@PathVariable long id){
        hotkeyService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
