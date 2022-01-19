package com.jding.remote.client.exceptions;

public class UnexpectedRemoteException extends RemoteException {

    public UnexpectedRemoteException(Throwable cause) {
        super("An unexpected exception occurred: " + cause.getClass().getSimpleName(), cause);
    }

}
