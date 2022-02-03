package com.github.klainstom.terminus.sshd.authentication;

import org.apache.sshd.server.auth.keyboard.InteractiveChallenge;
import org.apache.sshd.server.auth.keyboard.KeyboardInteractiveAuthenticator;
import org.apache.sshd.server.auth.keyboard.PromptEntry;
import org.apache.sshd.server.session.ServerSession;

import java.util.List;

public class TerminusInteractiveAuth implements KeyboardInteractiveAuthenticator {
    @Override
    public InteractiveChallenge generateChallenge(ServerSession session, String username, String lang, String subMethods) throws Exception {
        InteractiveChallenge challenge = new InteractiveChallenge();
        challenge.addPrompt(new PromptEntry("Password: ", false));
        return challenge;
    }

    @Override
    public boolean authenticate(ServerSession session, String username, List<String> responses) throws Exception {
        return Authentication.isCorrect(username, responses.get(0), session);
    }
}
