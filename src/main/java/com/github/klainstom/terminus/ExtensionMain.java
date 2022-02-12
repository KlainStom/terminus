package com.github.klainstom.terminus;

import com.github.klainstom.terminus.sshd.TerminusShell;
import com.github.klainstom.terminus.sshd.authentication.TerminusAuth;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extensions.Extension;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;

import java.io.IOException;
import java.nio.file.Files;

public class ExtensionMain extends Extension {
    private static final SshServer SSHD;

    static {
        Settings.read();
        if (!Settings.getTerminusDirectory().toFile().exists()) {
            try {
                Files.createDirectory(Settings.getTerminusDirectory());
            } catch (IOException e) {
                MinecraftServer.LOGGER.error("Could not create terminus data directory", e);
            }
        }
        SSHD = SshServer.setUpDefaultServer();
        SSHD.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(
                Settings.getTerminusDirectory().resolve("host_key_pair")));

        SSHD.setPublickeyAuthenticator(new TerminusAuth());
        SSHD.setPasswordAuthenticator(new TerminusAuth());
        SSHD.setKeyboardInteractiveAuthenticator(new TerminusAuth());

        SSHD.setShellFactory(TerminusShell::new);
        // SSHD.setCommandFactory();
    }

    @Override
    public void initialize() {
        MinecraftServer.LOGGER.info("====== TERMINUS ======");
        Info.printVersionLines();
        Info.printSettingsLines();
        MinecraftServer.LOGGER.info("======================");
        try {
            SSHD.setHost(Settings.getServerIp());
            SSHD.setPort(Settings.getServerPort());
            SSHD.start();
        } catch (IOException e) {
            MinecraftServer.LOGGER.error("SSHD couldn't start.", e);
        }
    }

    @Override
    public void terminate() {
        try {
            if (SSHD.isStarted()) SSHD.stop();
        } catch (IOException e) {
            MinecraftServer.LOGGER.error("SSHD couldn't stop correctly.", e);
        }
    }
}
