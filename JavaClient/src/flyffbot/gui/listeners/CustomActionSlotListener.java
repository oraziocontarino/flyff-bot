package flyffbot.gui.listeners;

public interface CustomActionSlotListener {
    void onKey0Change(String pipeId, String cid, String value);
    void onKey1Change(String pipeId, String cid, String value);
    void onCastTimeChange(String pipeId, String cid, int value);
}