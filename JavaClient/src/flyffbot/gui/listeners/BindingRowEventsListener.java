package flyffbot.gui.listeners;

public interface BindingRowEventsListener {
    void onKey0Change(String pipeId, String cid, String value);
    void onKey1Change(String pipeId, String cid, String value);
    void onDelayChange(String pipeId, String cid, int value);
    void onActiveChange(String pipeId, String cid, boolean value);

    void onDispatchKeyStroke(String pipeId, String cid);
}
