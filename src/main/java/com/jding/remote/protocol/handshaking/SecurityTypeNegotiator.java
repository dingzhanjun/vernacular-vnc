package com.jding.remote.protocol.handshaking;

import java.io.IOException;
import java.util.List;

import com.jding.remote.client.VncSession;
import com.jding.remote.client.exceptions.NoSupportedSecurityTypesException;
import com.jding.remote.client.exceptions.RemoteException;
import com.jding.remote.protocol.auth.MsLogon2AuthenticationHandler;
import com.jding.remote.protocol.auth.NoSecurityHandler;
import com.jding.remote.protocol.auth.SecurityHandler;
import com.jding.remote.protocol.auth.VncAuthenticationHandler;
import com.jding.remote.protocol.messages.SecurityType;
import com.jding.remote.protocol.messages.ServerSecurityType;
import com.jding.remote.protocol.messages.ServerSecurityTypes;

import static com.jding.remote.protocol.messages.SecurityType.*;
import static java.util.Collections.singletonList;

public class SecurityTypeNegotiator {

    public SecurityHandler negotiate(VncSession session) throws IOException, RemoteException {
        if (session.getProtocolVersion().equals(3, 3)) {
            ServerSecurityType serverSecurityType = ServerSecurityType.decode(session.getInputStream());
            return resolve(singletonList(serverSecurityType.getSecurityType()));
        } else {
            ServerSecurityTypes serverSecurityTypes = ServerSecurityTypes.decode(session.getInputStream());
            return resolve(serverSecurityTypes.getSecurityTypes());
        }
    }

    private static SecurityHandler resolve(List<SecurityType> securityTypes) throws  RemoteException {
        if (securityTypes.contains(NONE)) {
            return new NoSecurityHandler();
        } else if (securityTypes.contains(VNC)) {
            return new VncAuthenticationHandler();
        } else if (securityTypes.contains(MS_LOGON_2)) {
            return new MsLogon2AuthenticationHandler();
        } else {
            throw new NoSupportedSecurityTypesException();
        }
    }

}
