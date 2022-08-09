package flyffbot.configuration.args.sendkeyup;

import flyffbot.configuration.args.NativeArg;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SendKeyUpApiArg extends NativeArg {
    public SendKeyUpApiArg(
            @Value("${native-service.send-key-up-args.api.name}") String name,
            @Value("${native-service.send-key-up-args.api.value}") String value
    ){
        super(name, value);
    }
}
