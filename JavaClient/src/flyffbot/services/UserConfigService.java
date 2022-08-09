package flyffbot.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import flyffbot.entity.BindingRowDto;
import flyffbot.entity.CustomActionSlotSkillDto;
import flyffbot.entity.PipeDto;
import flyffbot.entity.WindowNameRowDto;
import flyffbot.enums.PipeStatus;
import flyffbot.exceptions.BindingRowNotFound;
import flyffbot.exceptions.PipeConfigNotFound;
import flyffbot.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@Slf4j
public class UserConfigService {

    @Autowired
    private ObjectMapper ob;
    @Autowired
    private SaveLoadService saveLoadService;

    @Getter
    private List<PipeDto> pipeList;

    @Getter
    @Setter
    private String lastSavedCfg;

    @PostConstruct
    public void init(){
        try {
            pipeList = saveLoadService.loadConfiguration();
        }catch (Exception e){
            throw new PipeConfigNotFound("Unable to read configuration", e);
        }
    }

    public PipeDto findPipeById(String pipeId){
        return pipeList.stream()
                .filter(item -> item.getId().equals(pipeId))
                .findFirst()
                .orElseThrow(()-> new PipeConfigNotFound("Unable to add binding row, pipe "+pipeId+" not found"));
    }
    public BindingRowDto addBindingRow(String pipeId) {
        val newElementData = BindingRowDto.builder()
                .id(UUID.randomUUID().toString())
                .hexKeyCode0("")
                .hexKeyCode1(Utils.toHexString(KeyEvent.VK_1))
                .delay(500)
                .active(false)
                .lastTimeExecuted(0)
                .build();
        findPipeById(pipeId)
                .getBindingRows()
                .add(newElementData);

        // Save updated configuration
        autoSave("addBindingRow", pipeId);
        return newElementData;
    }

    public Optional<String> removeLastBindingRow(String pipeId) {
        val pipe = findPipeById(pipeId);
        val bindingRows = pipe.getBindingRows();
        if(bindingRows.isEmpty()){
            return Optional.empty();
        }
        val removedId = bindingRows.get(bindingRows.size() - 1).getId();
        bindingRows.remove(bindingRows.size() - 1);

        // Save updated configuration
        autoSave("removeLastBindingRow", pipeId);
        return Optional.of(removedId);
    }

    public WindowNameRowDto findWindowNameRowData(String pipeId) {
        return findPipeById(pipeId).getWindowNameRow();
    }

    public List<PipeDto> findAllPipes() {
        return pipeList;
    }

    public void updateBindingRowKey0(String pipeId, String id, String value) {
        updateBindingRowFieldById(
                pipeId,
                id,
                toUpdate-> toUpdate.setHexKeyCode0(value)
        );

        // Save updated configuration
        autoSave("updateBindingRowKey0", pipeId);
    }
    public void updateBindingRowKey1(String pipeId, String id, String value) {
        updateBindingRowFieldById(
                pipeId,
                id,
                toUpdate-> toUpdate.setHexKeyCode1(value)
        );

        // Save updated configuration
        autoSave("updateBindingRowKey1", pipeId);
    }
    public void updateBindingRowDelay(String pipeId, String id, Integer value) {
        updateBindingRowFieldById(
                pipeId,
                id,
                toUpdate-> toUpdate.setDelay(value)
        );

        // Save updated configuration
        autoSave("updateBindingRowDelay", pipeId);
    }
    public void updateBindingRowActive(String pipeId, String id, boolean value) {
        updateBindingRowFieldById(
                pipeId,
                id,
                toUpdate-> toUpdate.setActive(value)
        );

        // Save updated configuration
        autoSave("updateBindingRowActive", pipeId);
    }
    public void saveSelectedWindowHwnd(String pipeId, String hwnd) {
        findPipeById(pipeId).getWindowNameRow().setHwnd(hwnd);

        // Save updated configuration
        autoSave("saveSelectedWindowHwnd", pipeId);
    }
    public BindingRowDto findBindingRowById(String pipeId, String cid){
        return findPipeById(pipeId)
                .getBindingRows()
                .stream().filter(item -> item.getId().equals(cid))
                .findFirst()
                .orElseThrow(()->new BindingRowNotFound("Binding row configuration not found: "+pipeId+" - "+cid));
    }

    public long countActiveBindingRowByPipeId(String pipeId){
        return findPipeById(pipeId)
                .getBindingRows()
                .stream()
                .filter(BindingRowDto::isActive)
                .count();
    }

    public int findIndex(String pipeId) {
        for(int i = 0; i < pipeList.size(); i++){
            if(pipeId.equals(pipeList.get(i).getId())){
                return i;
            }
        }

        throw new PipeConfigNotFound("Unable to find pipe with id: " + pipeId);
    }

    public void updateTogglePipe(String pipeId) {
        val pipeData = findPipeById(pipeId);
        pipeData.setPaused(!pipeData.isPaused());

        // Save updated configuration
        autoSave("updateTogglePipe", pipeId);
    }

    public PipeDto createIfNotExistsPipe(int index) {
        if(index < pipeList.size()){
            return pipeList.get(index);
        }else {
            val newPipe = saveLoadService.readDefaultPipe();
            pipeList.add(newPipe);

            // Save updated configuration
            autoSave("createIfNotExistsPipe", newPipe.getId());
            return newPipe;
        }
    }

    public int countPipes() {
        return pipeList.size();
    }

    public String removeLastPipe() {
        val index = pipeList.size()-1;
        val pipeToRemove = pipeList.get(index);
        val removedPipeId = pipeToRemove.getId();
        pipeList.remove(index);

        // Save updated configuration
        autoSave("removeLastPipe", removedPipeId);

        return removedPipeId;
    }
    public CustomActionSlotSkillDto addCustomActionSlotSkill(String pipeId) {
        val toInsert = CustomActionSlotSkillDto.builder()
                .id(UUID.randomUUID().toString())
                .hexKeyCode0("")
                .hexKeyCode1("0x31")
                .castTime(1000)
                .build();
        findPipeById(pipeId)
                .getCustomActionSlotSkills()
                .add(toInsert);

        autoSave("addCustomActionSlotSkill", pipeId);
        return toInsert;
    }

    public Optional<String> removeCustomActionSlotSkill(String pipeId) {
        val skills = findPipeById(pipeId).getCustomActionSlotSkills();
        if(skills.size() == 0){
            return Optional.empty();
        }
        val index = skills.size() - 1;
        val removedId = skills.get(index).getId();
        skills.remove(index);

        autoSave("addCustomActionSlotSkill", pipeId);
        return Optional.of(removedId);
    }

    public void updateCASKey0(String pipeId, String cid, String value) {
        updateCustomActionSlotSkillFieldById(pipeId, cid, skill -> skill.setHexKeyCode0(value));
        autoSave("updateCASKey0", pipeId);
    }
    public void updateCASKey1(String pipeId, String cid, String value) {
        updateCustomActionSlotSkillFieldById(pipeId, cid, skill -> skill.setHexKeyCode1(value));
        autoSave("updateCASKey1", pipeId);
    }

    public void updateCASCastTime(String pipeId, String cid, int value) {
        updateCustomActionSlotSkillFieldById(pipeId, cid, skill -> skill.setCastTime(value));
        autoSave("updateCASCastTime", pipeId);
    }
    public void updatePause(String pipeId, boolean value) {
        findPipeById(pipeId).setPaused(value);
        autoSave("updatePause", pipeId);
    }

    public void updateCASRunning(String pipeId, boolean value) {
        findPipeById(pipeId).setCustomActionSlotRunning(value);
        autoSave("updateCASRunning", pipeId);
    }
    private void autoSave(String functionName, String pipeId){
        try {
            log.debug("Save - {}", functionName);
            saveLoadService.saveConfiguration(pipeList);
        }catch (Exception e){
            log.error("Error occurred while invoking "+functionName+" with pipeId: "+pipeId, e);
        }
    }

    private void updateBindingRowFieldById(
            String pipeId,
            String id,
            Consumer<BindingRowDto> consumer
    ){
        findPipeById(pipeId)
                .getBindingRows()
                .stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .ifPresent(consumer);
    }

    private void updateCustomActionSlotSkillFieldById(
            String pipeId,
            String id,
            Consumer<CustomActionSlotSkillDto> consumer
    ){
        findPipeById(pipeId)
                .getCustomActionSlotSkills()
                .stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .ifPresent(consumer);
    }

}
