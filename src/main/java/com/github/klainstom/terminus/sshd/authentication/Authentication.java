package com.github.klainstom.terminus.sshd.authentication;

import com.github.klainstom.terminus.Settings;
import net.minestom.server.MinecraftServer;
import org.apache.sshd.common.config.keys.PublicKeyEntry;
import org.apache.sshd.server.session.ServerSession;

import java.io.*;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.List;
import java.util.Set;

public class Authentication {
    private static final Path AUTHORIZED_KEYS = Settings.getTerminusDirectory().resolve("authorized_keys");
    private static final Path PASSWORD = Settings.getTerminusDirectory().resolve("password");

    private static volatile String PASS = "";
    private static volatile Set<PublicKey> KEYS = Set.of();
    private static volatile long lastUpdate = 0;

    public static boolean isCorrect(String username, String password, ServerSession session) {
        update();
        return password.equals(PASS);
    }

    public static boolean isCorrect(String username, PublicKey key, ServerSession session) {
        update();
        return KEYS.contains(key);
    }

    private static void update() {
        if (lastUpdate > System.currentTimeMillis()-10000) return;
        lastUpdate = System.currentTimeMillis();
        try (BufferedReader reader = new BufferedReader(new FileReader(AUTHORIZED_KEYS.toFile()))) {
            List<PublicKeyEntry> publicKeyEntries = reader.lines().map(PublicKeyEntry::parsePublicKeyEntry).toList();
            KEYS = Set.copyOf(PublicKeyEntry.resolvePublicKeyEntries(null, publicKeyEntries, null));
        } catch (FileNotFoundException e) {
            MinecraftServer.LOGGER.error("File \"authorized_keys\" not found.", e);
        } catch (IOException e) {
            MinecraftServer.LOGGER.error("An error occurred while reading \"authorized_keys\".", e);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(PASSWORD.toFile()))) {
            PASS = reader.readLine();
        } catch (FileNotFoundException e) {
            MinecraftServer.LOGGER.error("File \"password\" not found.", e);
        } catch (IOException e) {
            MinecraftServer.LOGGER.error("An error occurred while reading \"password\".", e);
        }
    }
}
