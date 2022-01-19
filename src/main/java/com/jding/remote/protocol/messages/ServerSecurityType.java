package com.jding.remote.protocol.messages;

import static com.jding.remote.protocol.messages.SecurityType.resolve;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.jding.remote.client.exceptions.HandshakingFailedException;
import com.jding.remote.client.exceptions.NoSupportedSecurityTypesException;

public class ServerSecurityType {

    private final SecurityType securityType;

    private ServerSecurityType(SecurityType securityType) {
        this.securityType = securityType;
    }

    public SecurityType getSecurityType() {
        return securityType;
    }

    public static ServerSecurityType decode(InputStream in) throws HandshakingFailedException, NoSupportedSecurityTypesException, IOException {
        DataInputStream dataInput = new DataInputStream(in);
        int type = dataInput.readInt();

        if (type == 0) {
            ErrorMessage errorMessage = ErrorMessage.decode(in);
            throw new HandshakingFailedException(errorMessage.getMessage());
        }

        return resolve(type).map(ServerSecurityType::new).orElseThrow(NoSupportedSecurityTypesException::new);
    }
}
