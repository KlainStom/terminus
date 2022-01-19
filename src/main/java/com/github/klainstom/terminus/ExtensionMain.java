package com.github.klainstom.terminus;

import net.minestom.server.MinecraftServer;
import net.minestom.server.extensions.Extension;

public class ExtensionMain extends Extension {
    @Override
    public void initialize() {
        MinecraftServer.LOGGER.info("$name initialize.");
        TerminusTerminal.start();
    }

    @Override
    public void terminate() {
        MinecraftServer.LOGGER.info("$name terminate.");
        TerminusTerminal.stop();
    }
}
