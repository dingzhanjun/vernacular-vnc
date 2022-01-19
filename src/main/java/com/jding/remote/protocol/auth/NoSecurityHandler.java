package com.jding.remote.protocol.auth;

import static com.jding.remote.protocol.messages.SecurityType.NONE;

import java.io.DataOutputStream;
import java.io.IOException;

import com.jding.remote.client.VncSession;
import com.jding.remote.protocol.messages.ProtocolVersion;
import com.jding.remote.protocol.messages.SecurityResult;

public class NoSecurityHandler implements SecurityHandler {

    @Override
    public SecurityResult authenticate(VncSession session) throws IOException {
        ProtocolVersion protocolVersion = session.getProtocolVersion();
        if (!protocolVersion.equals(3, 3)) {
            new DataOutputStream(session.getOutputStream()).writeByte(NONE.getCode());
        }
        if (protocolVersion.equals(3, 8)) {
            return SecurityResult.decode(session.getInputStream(), session.getProtocolVersion());
        } else {
            return new SecurityResult(true);
        }
    }
}
