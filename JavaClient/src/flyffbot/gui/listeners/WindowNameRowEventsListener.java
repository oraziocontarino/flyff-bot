package flyffbot.gui.listeners;

public interface WindowNameRowEventsListener {
    void onNameChange(String pipeId, String value);
    void onAdd();
    void onRemove();

    void onNameRefresh(String pipeId);
}
