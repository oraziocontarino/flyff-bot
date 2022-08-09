package flyffbot.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import flyffbot.dto.nativeapi.NativeResponse;
import flyffbot.dto.nativeapi.WindowItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NativeResponseMapper {
    @Autowired
    private ObjectMapper mapper;

    public NativeResponse<List<WindowItem>> mapListWindowItem(String source) throws JsonProcessingException {
        return mapper.readValue(source, new TypeReference<>(){});
    }
    public NativeResponse<Void> mapVoid(String source) throws JsonProcessingException {
        return mapper.readValue(source, new TypeReference<>(){});
    }
}