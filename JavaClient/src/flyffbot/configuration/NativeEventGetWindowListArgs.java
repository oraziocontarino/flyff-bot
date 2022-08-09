package flyffbot.configuration;

import flyffbot.configuration.args.PathArg;
import flyffbot.configuration.args.getwindowlist.GetWindowListApiArg;
import flyffbot.configuration.args.getwindowlist.WindowSearchKeyArg;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class NativeEventGetWindowListArgs {
    @Autowired
    PathArg path;

    @Autowired
    GetWindowListApiArg api;

    @Autowired
    WindowSearchKeyArg windowSearchKey;
}
