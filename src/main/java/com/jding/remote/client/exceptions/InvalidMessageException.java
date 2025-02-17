package com.jding.remote.client.exceptions;

import static java.lang.String.format;

public class InvalidMessageException extends RemoteException {

    private final String messageType;

    public InvalidMessageException(String messageType) {
        super(format("The server sent an invalid '%s' message", messageType));

        this.messageType = messageType;
    }

    public String getMessageType() {
        return messageType;
    }
}
