package com.jding.remote.protocol.messages;

import static java.util.Arrays.stream;

import com.jding.remote.client.exceptions.UnsupportedEncodingException;

public enum Encoding {

    RAW(0),
    COPYRECT(1),
    RRE(2),
    HEXTILE(5),
    ZLIB(6),
    DESKTOP_SIZE(-223),
    CURSOR(-239)
    ;

    private int code;

    Encoding(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Encoding resolve(int code) throws UnsupportedEncodingException {
        return stream(values())
                .filter(e -> e.code == code)
                .findFirst()
                .orElseThrow(() -> new UnsupportedEncodingException(code));
    }
}
