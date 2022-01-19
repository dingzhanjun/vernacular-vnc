package com.jding.remote.client.exceptions;

public abstract class RemoteException extends Exception {

    public RemoteException() {

    }

    public RemoteException(String message) {
        super(message);
    }

    public RemoteException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteException(Throwable cause) {
        super(cause);
    }
}
