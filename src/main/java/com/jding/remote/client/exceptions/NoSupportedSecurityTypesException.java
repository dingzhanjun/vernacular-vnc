package com.jding.remote.client.exceptions;

public class NoSupportedSecurityTypesException extends RemoteException {

    public NoSupportedSecurityTypesException() {
        super("The server does not support any VNC security types supported by this client");
    }

}
