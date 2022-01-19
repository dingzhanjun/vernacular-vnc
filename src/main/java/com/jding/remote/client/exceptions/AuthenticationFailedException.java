package com.jding.remote.client.exceptions;

public class AuthenticationFailedException extends RemoteException {

    private final String serverMessage;

    public AuthenticationFailedException() {
        super("Authentication failed");
        serverMessage = null;
    }

    public AuthenticationFailedException(String serverMessage) {
        super("Authentication failed. The server returned the following extra information: " + serverMessage);
        this.serverMessage = serverMessage;
    }

    public String getServerMessage() {
        return serverMessage;
    }

}
