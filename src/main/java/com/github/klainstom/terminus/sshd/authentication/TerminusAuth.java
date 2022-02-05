package com.github.klainstom.terminus.sshd.authentication;

import org.apache.sshd.server.auth.AsyncAuthException;
import org.apache.sshd.server.auth.keyboard.InteractiveChallenge;
import org.apache.sshd.server.auth.keyboard.KeyboardInteractiveAuthenticator;
import org.apache.sshd.server.auth.keyboard.PromptEntry;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import java.security.PublicKey;
import java.util.List;

public class TerminusAuth implements PublickeyAuthenticator, PasswordAuthenticator, KeyboardInteractiveAuthenticator {
    @Override
    public InteractiveChallenge generateChallenge(ServerSession session, String username, String lang, String subMethods) throws Exception {
        InteractiveChallenge challenge = new InteractiveChallenge();
        challenge.addPrompt(new PromptEntry("Password: ", false));
        return challenge;
    }

    @Override
    public boolean authenticate(ServerSession session, String username, List<String> responses) throws Exception {
        return AccessControl.isAllowed(username, AccessControl.AccessMethod.INTERACTIVE, session)
                && Authentication.isCorrect(username, responses.get(0), session);
    }

    @Override
    public boolean authenticate(String username, String password, ServerSession session) throws PasswordChangeRequiredException, AsyncAuthException {
        return AccessControl.isAllowed(username, AccessControl.AccessMethod.PASSWORD, session)
                && Authentication.isCorrect(username, password, session);
    }

    @Override
    public boolean authenticate(String username, PublicKey key, ServerSession session) throws AsyncAuthException {
        return AccessControl.isAllowed(username, AccessControl.AccessMethod.PUBLIC_KEY, session)
                && Authentication.isCorrect(username, key, session);
    }
}
