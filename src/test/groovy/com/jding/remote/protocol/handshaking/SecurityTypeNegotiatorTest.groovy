package com.jding.remote.protocol.handshaking

import com.jding.remote.client.VncSession
import com.jding.remote.protocol.auth.MsLogon2AuthenticationHandler
import com.jding.remote.protocol.auth.NoSecurityHandler
import com.jding.remote.protocol.auth.VncAuthenticationHandler
import com.jding.remote.protocol.messages.ProtocolVersion
import spock.lang.Specification
import spock.lang.Subject

import static com.jding.remote.protocol.messages.SecurityType.*

class SecurityTypeNegotiatorTest extends Specification {

    @Subject
    def negotiator = new SecurityTypeNegotiator()

    def "for protocol version >= 3.7, should prefer 'No Authentication' if offered, or VNC authentication otherwise"() {
        given:
        def message = [serverTypes.size()]
        serverTypes.each { message += it.code }
        def bytes = new ByteArrayInputStream(message as byte[])
        def session = Mock(VncSession)
        _ * session.protocolVersion >> new ProtocolVersion(3, 7)

        when:
        def handler = negotiator.negotiate(session)

        then:
        1 * session.inputStream >> bytes
        handler.class == selected

        where:
        serverTypes             | selected
        [NONE]                  | NoSecurityHandler
        [NONE, VNC]             | NoSecurityHandler
        [NONE, VNC, MS_LOGON_2] | NoSecurityHandler
        [VNC]                   | VncAuthenticationHandler
        [VNC, MS_LOGON_2]       | VncAuthenticationHandler
        [MS_LOGON_2]            | MsLogon2AuthenticationHandler
    }

    def "for protocol version 3.3, must accept whatever security type is selected by the server"() {
        given:
        def message = [0, 0, 0, serverType.code]
        def bytes = new ByteArrayInputStream(message as byte[])
        def session = Mock(VncSession)
        _ * session.protocolVersion >> new ProtocolVersion(3, 3)

        when:
        def handler = negotiator.negotiate(session)

        then:
        1 * session.inputStream >> bytes
        handler.class == selected

        where:
        serverType | selected
        NONE       | NoSecurityHandler
        VNC        | VncAuthenticationHandler
        MS_LOGON_2 | MsLogon2AuthenticationHandler
    }

}
