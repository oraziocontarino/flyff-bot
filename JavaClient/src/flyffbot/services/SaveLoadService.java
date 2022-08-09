package flyffbot.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import flyffbot.entity.PipeDto;
import flyffbot.exceptions.SaveLoadException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SaveLoadService {
    @Value("${auto-save.save-to-path}")
    private String saveToPath;
    @Value("${auto-save.save-file-name}")
    private String saveFileName;

    @Value("${auto-save.save-to-temp}")
    private boolean saveToTemp;

    @Autowired
    private ObjectMapper mapper;

    private String lastSavedCfg;

    public void saveConfiguration(List<PipeDto> cfg) throws IOException {
        if(saveToTemp){
            saveToTempFolder(cfg);
        }else{
            saveToUserFolder(cfg);
        }
    }

    public List<PipeDto> loadConfiguration() throws IOException {
        return saveToTemp ? loadFromTempFolder() : loadFromUserFolder();
    }

    public String toJsonConfig(List<PipeDto> cfg) throws JsonProcessingException {
        return mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(Optional.ofNullable(cfg).orElse(new ArrayList<>()));
    }

    public PipeDto readDefaultPipe(){
        try{
            val json = readJson("/userConfiguration.json");
            val array = mapper.readValue(json, PipeDto[].class);
            val pipe = array[0];
            pipe.setId(UUID.randomUUID().toString());
            pipe.getWindowNameRow().setId(UUID.randomUUID().toString());
            return pipe;
        }catch (Exception e){
            log.error("Error occurred while reading default configuration", e);
            return null;
        }
    }
    private void saveToTempFolder(List<PipeDto> cfg) throws IOException {
        val path = System.getProperty("java.io.tmpdir");
        writeCommonFile(cfg, path, "save-to-temp");
    }
    private void saveToUserFolder(List<PipeDto> cfg) throws IOException {
        writeCommonFile(cfg, saveToPath, "save-to-path");
    }

    private List<PipeDto> loadFromTempFolder() throws IOException {
        val path = System.getProperty("java.io.tmpdir");
        return readCommonFile(path, "load-from-temp");
    }

    private List<PipeDto> loadFromUserFolder() throws IOException {
        return readCommonFile(saveToPath, "load-from-path");
    }

    private void writeCommonFile(List<PipeDto> cfg, String basePath, String logSuffix) throws IOException {
        val json = toJsonConfig(cfg);
        val absFilePath = Paths.get(basePath, saveFileName+".json").toAbsolutePath().toString();
        log.debug("Save - File path: {}", absFilePath);
        val file = new File(absFilePath);
        if(file.exists() && !file.delete()){
            throw new SaveLoadException("Save - Unable to delete old "+logSuffix+" cfg file");
        }
        if(!file.createNewFile()){
            throw new SaveLoadException("Save - Unable to create new "+logSuffix+" cfg file");
        }
        try(val writer = new FileWriter(file.getAbsolutePath())){
            writer.write(json);
        }catch (Exception e){
            throw new SaveLoadException("Save - Error occurred while processing "+logSuffix+" cfg file", e);
        }
    }

    private List<PipeDto> readCommonFile(String basePath, String logSuffix) throws IOException {
        val absFilePath = Paths.get(basePath, saveFileName+".json").toAbsolutePath().toString();
        log.debug("Load - File path: {}", absFilePath);
        val file = new File(absFilePath);

        // Read stored cfg file
        if(file.exists()){
            val json = Files.readString(file.toPath());
            val array = mapper.readValue(json, PipeDto[].class);
            return new ArrayList<>(List.of(array));
        }

        // No file found in specified path - Create a new one
        if(!file.createNewFile()){
            throw new SaveLoadException("Save - Unable to create new "+logSuffix+" cfg file");
        }

        // Write default config to new file
        List<PipeDto> pipeList;
        try(val writer = new FileWriter(file.getAbsolutePath())){
            pipeList = Collections.singletonList(readDefaultPipe());
            val json = mapper.writeValueAsString(pipeList);
            writer.write(json);
        }catch (Exception e){
            throw new SaveLoadException("Load - Unable to read/write default cfg file");
        }

        return new ArrayList<>(pipeList);
    }

    private String readJson(String resourcePath) throws IOException {
        val classPathResource = new ClassPathResource(resourcePath);
        try (InputStream resource = classPathResource.getInputStream()) {
            val isr = new InputStreamReader(resource, StandardCharsets.UTF_8);
            val lines = new BufferedReader(isr).lines().collect(Collectors.toList());
            return String.join("\n", lines);
        }
    }
}
