package com.jding.remote.protocol.handshaking;

import java.io.IOException;

import com.jding.remote.client.VncSession;
import com.jding.remote.client.exceptions.AuthenticationFailedException;
import com.jding.remote.client.exceptions.RemoteException;
import com.jding.remote.protocol.auth.SecurityHandler;
import com.jding.remote.protocol.messages.SecurityResult;

public class Handshaker {

    private final ProtocolVersionNegotiator protocolVersionNegotiator;
    private final SecurityTypeNegotiator securityTypeNegotiator;

    public Handshaker() {
        protocolVersionNegotiator = new ProtocolVersionNegotiator();
        securityTypeNegotiator = new SecurityTypeNegotiator();
    }

    public void handshake(VncSession session) throws RemoteException, IOException {
        protocolVersionNegotiator.negotiate(session);

        SecurityHandler securityHandler = securityTypeNegotiator.negotiate(session);
        SecurityResult securityResult = securityHandler.authenticate(session);

        if (!securityResult.isSuccess()) {
            if (securityResult.getErrorMessage() != null) {
                throw new AuthenticationFailedException(securityResult.getErrorMessage());
            } else {
                throw new AuthenticationFailedException();
            }
        }
    }
}
