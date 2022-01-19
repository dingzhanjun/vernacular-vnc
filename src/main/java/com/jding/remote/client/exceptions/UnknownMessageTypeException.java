package com.jding.remote.client.exceptions;

import static java.lang.String.format;

public class UnknownMessageTypeException extends RemoteException {

    private final int messageType;

    public int getMessageType() {
        return messageType;
    }

    public UnknownMessageTypeException(int messageType) {
        super(format("Received unexpected message type: %s", messageType));

        this.messageType = messageType;
    }

}
