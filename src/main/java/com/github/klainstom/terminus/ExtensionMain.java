package com.github.klainstom.terminus;

import com.github.klainstom.terminus.sshd.TerminusShell;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extensions.Extension;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;

import java.io.IOException;
import java.nio.file.Path;

public class ExtensionMain extends Extension {
    private static final SshServer SSHD;

    static {
        SSHD = SshServer.setUpDefaultServer();
        SSHD.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(Path.of("ssh", "id")));

        SSHD.setPublickeyAuthenticator(((username, key, session) -> true));
        // SSHD.setPasswordAuthenticator(((username, password, session) -> true));

        SSHD.setShellFactory(TerminusShell::new);
        // SSHD.setCommandFactory();
    }

    @Override
    public void initialize() {
        MinecraftServer.LOGGER.info("$name initialize.");
        try {
            SSHD.setHost("localhost");
            SSHD.setPort(2223);
            SSHD.start();
        } catch (IOException e) {
            MinecraftServer.LOGGER.error("SSHD couldn't start.", e);
        }
    }

    @Override
    public void terminate() {
        MinecraftServer.LOGGER.info("$name terminate.");
        try {
            if (SSHD.isStarted()) SSHD.stop();
        } catch (IOException e) {
            MinecraftServer.LOGGER.error("SSHD couldn't stop correctly.", e);
        }
    }
}
