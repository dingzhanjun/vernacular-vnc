package com.jding.remote.client.exceptions;

import static java.lang.String.format;

public class UnsupportedEncodingException extends RemoteException {

    private final int encodingType;

    public int getEncodingType() {
        return encodingType;
    }

    public UnsupportedEncodingException(int encodingType) {
        super(format("Unsupported encoding type: %d", encodingType));

        this.encodingType = encodingType;
    }
}
