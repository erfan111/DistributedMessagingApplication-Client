package dev2dev.textclient;

interface MessageProcessor {
    void processMessage(String sender, String message);

    void processError(String errorMessage);

    void processInfo(String infoMessage);

    void processClientRegistration(boolean status);
}
