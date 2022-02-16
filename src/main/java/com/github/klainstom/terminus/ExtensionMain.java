package com.github.klainstom.terminus;

import com.github.klainstom.terminus.sshd.TerminusShell;
import com.github.klainstom.terminus.sshd.authentication.TerminusAuth;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extensions.Extension;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ExtensionMain extends Extension {
    public static Path DATA_DIRECTORY;

    private final SshServer SSHD = SshServer.setUpDefaultServer();

    @Override
    public void preInitialize() {
         DATA_DIRECTORY = Objects.requireNonNull(MinecraftServer.getExtensionManager()
                 .getExtension("$Name"), "Extension installed but not found!").getDataDirectory();

        if (!DATA_DIRECTORY.toFile().exists()) {
            try {
                Files.createDirectory(DATA_DIRECTORY);
            } catch (IOException e) {
                MinecraftServer.LOGGER.error("Could not create terminus data directory", e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize() {
        Settings.read();
        SSHD.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(
                this.getDataDirectory().resolve("host_key_pair")));
        SSHD.setPublickeyAuthenticator(new TerminusAuth());
        SSHD.setPasswordAuthenticator(new TerminusAuth());
        SSHD.setKeyboardInteractiveAuthenticator(new TerminusAuth());

        SSHD.setShellFactory(TerminusShell::new);
        // SSHD.setCommandFactory();

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
            e.printStackTrace();
        }
    }

    @Override
    public void terminate() {
        SSHD.getActiveSessions().forEach(session -> {
            try {
                session.disconnect(11, "Server shutdown.");
            } catch (IOException e) {
                MinecraftServer.LOGGER.error("Couldn't cleanly disconnect client.", e);
                e.printStackTrace();
            }
        });
    }
}
