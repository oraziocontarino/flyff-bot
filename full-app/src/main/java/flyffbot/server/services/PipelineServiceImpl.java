package flyffbot.server.services;

import flyffbot.server.entity.PipelineEntity;
import flyffbot.server.exceptions.PipeConfigNotFound;
import flyffbot.server.repositories.PipelineRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PipelineServiceImpl {
    @Autowired
    private PipelineRepository repository;


    public List<PipelineEntity> findAllPipes() {
        val list = new ArrayList<PipelineEntity>();
        repository.findAll().forEach(list::add);
        return list;
    }

    public PipelineEntity findPipeById(Long pipelineId){
        return repository.findById(pipelineId)
                .orElseThrow(()-> new PipeConfigNotFound("Unable to add binding row, pipe "+pipelineId+" not found"));
    }

    public void saveSelectedWindowHwnd(Long pipelineId, String selectedWindowHwnd) {
        repository.updateSelectedWindowHwndById(pipelineId, selectedWindowHwnd);
    }

    public int findIndex(Long pipelineId) {
        return repository.findPipelineIndexById(pipelineId);
    }

    public void pauseAll(){
        repository.pauseAll();
    }
    public void updateTogglePause(Long pipelineId) {
        repository.togglePausedById(pipelineId);
    }

    public Long removeLastPipe() {
        val toDelete = repository.findTopByOrderByIdDesc();
        //TODO: prima di rimuovere la pipe bisogna rimuovere i tasks schedulati sull'executor!
        repository.deleteById(toDelete.getId());
        return toDelete.getId();
    }

    public PipelineEntity addNewPipe() {
        return repository.save(PipelineEntity.builder().build());
    }

    public void updateCustomActionSlotRunning(long id, boolean isRunning) {
        repository.updateCustomActionSlotRunningById(id, isRunning);
    }
}
