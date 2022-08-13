package flyffbot.configuration;

import flyffbot.services.SaveLoadService;
import flyffbot.services.UserConfigService;
import flyffbot.services.nativeservices.NativeGetWindowListService;
import flyffbot.services.nativeservices.NativeSendKeyService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ScheduledExecutorService;

@Configuration
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DepInjComponentData {
    @Autowired
    private UserConfigService userConfigService;

    @Autowired
    private SaveLoadService saveLoadService;

    @Autowired
    private NativeGetWindowListService nativeGetWindowListService;

    @Autowired
    private NativeSendKeyService nativeSendKeyService;

    @Autowired
    private ScheduledExecutorService scheduler;
}
