package flyffbot.gui.listeners;

import java.util.Optional;

public interface KeyDownHookListener {
    void onAddPipe();
    Optional<String> onRemovePipe();
    void onTogglePause(String pipeId);

    void onCustomActionSlot(String pipeId);
}
