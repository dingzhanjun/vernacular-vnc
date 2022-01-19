package com.jding.remote.client.exceptions;

public class AuthenticationRequiredException extends RemoteException {

    public AuthenticationRequiredException() {
        super("Server requires authentication but no username or password supplier was provided");
    }
}
