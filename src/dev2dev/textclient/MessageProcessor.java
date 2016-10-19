package dev2dev.textclient;

public interface MessageProcessor {
    void processMessage(String sender, String message);

    void processError(String errorMessage);

    void processInfo(String infoMessage);
}
