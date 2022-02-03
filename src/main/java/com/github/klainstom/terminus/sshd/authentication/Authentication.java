package com.github.klainstom.terminus.sshd.authentication;

import org.apache.sshd.server.session.ServerSession;

import java.security.PublicKey;

public class Authentication {
    public static boolean isCorrect(String username, String password, ServerSession session) {
        return true;
    }

    public static boolean isCorrect(String username, PublicKey key, ServerSession session) {
        return false;
    }
}
