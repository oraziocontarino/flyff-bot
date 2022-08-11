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
    @Value("${auto-save.folder-name}")
    private String folderName;
    @Value("${auto-save.file-name}")
    private String fileName;
    @Autowired
    private ObjectMapper mapper;

    public void saveConfiguration(List<PipeDto> cfg) throws IOException {
        val json = toJsonConfig(cfg);
        val absFilePath = Paths.get(folderName, fileName +".json").toAbsolutePath().toString();
        log.debug("Save - File path: {}", absFilePath);
        val file = new File(absFilePath);
        if(file.exists() && !file.delete()){
            throw new SaveLoadException("Save - Unable to delete old cfg file");
        }
        if(!file.createNewFile()){
            throw new SaveLoadException("Save - Unable to create new cfg file");
        }
        try(val writer = new FileWriter(file.getAbsolutePath())){
            writer.write(json);
        }catch (Exception e){
            throw new SaveLoadException("Save - Error occurred while processing cfg file", e);
        }
    }

    public List<PipeDto> loadConfiguration() throws IOException {
        val absDirectories = Paths.get(folderName).toAbsolutePath().toString();
        val absFullPath = Paths.get(folderName, fileName +".json").toAbsolutePath().toString();
        log.debug("Load - File path: {}", absFullPath);

        // Check / Create directories to cfg file
        val directories = new File(absDirectories);
        if(!directories.exists() && !directories.mkdirs()){
            throw new SaveLoadException("Save - Unable to find/create directories to: "+directories.getAbsolutePath());
        }

        // Read stored cfg file
        val file = new File(absFullPath);
        if(file.exists()){
            val json = Files.readString(file.toPath());
            val array = mapper.readValue(json, PipeDto[].class);
            return new ArrayList<>(List.of(array));
        }

        // No file found in specified path - Create a new one
        if(!file.createNewFile()){
            throw new SaveLoadException("Save - Unable to create new cfg file");
        }

        // Write default config to new file
        val pipeList = Collections.singletonList(readDefaultPipe());

        saveConfiguration(pipeList);

        return new ArrayList<>(pipeList);
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

    private String readJson(String resourcePath) throws IOException {
        val classPathResource = new ClassPathResource(resourcePath);
        try (InputStream resource = classPathResource.getInputStream()) {
            val isr = new InputStreamReader(resource, StandardCharsets.UTF_8);
            val lines = new BufferedReader(isr).lines().collect(Collectors.toList());
            return String.join("\n", lines);
        }
    }
}
