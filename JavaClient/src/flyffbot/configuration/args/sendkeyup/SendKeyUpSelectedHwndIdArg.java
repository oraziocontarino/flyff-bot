package flyffbot.configuration.args.sendkeyup;

import flyffbot.configuration.args.NativeArg;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SendKeyUpSelectedHwndIdArg extends NativeArg {
    public SendKeyUpSelectedHwndIdArg(
            @Value("${native-service.send-key-up-args.selected-hwnd-id.name}") String name
    ){
        super(name, null);
    }
}
