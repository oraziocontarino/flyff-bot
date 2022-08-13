package flyffbot.configuration.args;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.nio.file.Paths;

@Configuration
public class PathArg extends NativeArg {
    @Value("${auto-save.folder-name}")
    private String folderName;

    public PathArg(){
        super(null, null);
    }

    @PostConstruct
    private void init(){
        name = Paths.get(folderName, "flyff-bot-native-api.exe").toAbsolutePath().toString();
    }
}
