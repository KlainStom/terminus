package com.github.klainstom.terminus.sshd.authentication;

import org.apache.sshd.server.auth.AsyncAuthException;
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import java.security.PublicKey;

public class TerminusPublickeyAuth implements PublickeyAuthenticator {
    @Override
    public boolean authenticate(String username, PublicKey key, ServerSession session) throws AsyncAuthException {
        return Authentication.isCorrect(username, key, session);
    }
}
