package flyffbot.configuration.args;

import lombok.val;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;

@Configuration
public class PathArg extends NativeArg {
    public PathArg(){
        super(null, null);
        val path = System.getProperty("java.io.tmpdir");
        value = Paths.get(path, "flyff-bot-native-api.exe").toAbsolutePath().toString();
    }
}
