package flyffbot.configuration;

import flyffbot.configuration.args.PathArg;
import flyffbot.configuration.args.sendkeyup.SendKeyUpApiArg;
import flyffbot.configuration.args.sendkeyup.SendKeyUpKeystrokeIdArg;
import flyffbot.configuration.args.sendkeyup.SendKeyUpSelectedHwndIdArg;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class NativeEventSendKeyUpArgs {
    @Autowired
    PathArg path;

    @Autowired
    SendKeyUpApiArg api;

    @Autowired
    SendKeyUpSelectedHwndIdArg hwnd;

    @Autowired
    SendKeyUpKeystrokeIdArg keystroke;
}
