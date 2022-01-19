package com.jding.remote.protocol.auth;

import java.io.IOException;

import com.jding.remote.client.VncSession;
import com.jding.remote.client.exceptions.RemoteException;
import com.jding.remote.protocol.messages.SecurityResult;

public interface SecurityHandler {
    SecurityResult authenticate(VncSession session) throws RemoteException, IOException;
}
