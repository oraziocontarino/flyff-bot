package flyffbot.controller;

import flyffbot.dto.UpdateCastTimeRequestDto;
import flyffbot.dto.UpdateHexKeyCodeRequestDto;
import flyffbot.services.CustomActionSlotServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("custom-action-slots")
public class CustomActionSlotController {
    @Autowired
    private CustomActionSlotServiceImpl service;

    @PostMapping("/{pipelineId}")
    public ResponseEntity<Void> create(@PathVariable long pipelineId) {
        service.addCustomActionSlot(pipelineId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/key/{keyIndex}")
    public ResponseEntity<Void> updateHexValue(
            @PathVariable long id,
            @PathVariable int keyIndex, //0=first key, 1=second key
            @RequestBody UpdateHexKeyCodeRequestDto body
    ){
        service.updateCustomActionSlot(id, keyIndex, body.getHexKeyCode());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/cast-time")
    public ResponseEntity<Void> updateCastTime(@PathVariable long id, @RequestBody UpdateCastTimeRequestDto body){
        service.updateCastTime(id, body.getCastTimeMs());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotKeys(@PathVariable long id){
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
