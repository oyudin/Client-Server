package org.example;

public enum Enums {

    pathToFile("Enter a path to the file and then send the file: \n"),
    clientMessage("Send a message to server. Or type 'exit' for leave: \n"),
    clientDisconnected("The client disconnected \n"),
    connectionNotification("[SERVER] A new client has been connected: client-"),
    menuMessage("Enter 1 to send a file, enter 2 to sent a message or 'exit' to leave: \n");

    private final String description;

    Enums(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
