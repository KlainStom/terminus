package com.github.klainstom.terminus;

import net.minestom.server.MinecraftServer;
import net.minestom.server.extensions.Extension;

public class ExtensionMain extends Extension {
    @Override
    public void initialize() {
        MinecraftServer.LOGGER.info("$name$ initialize.");

    }

    @Override
    public void terminate() {
        MinecraftServer.LOGGER.info("$name$ terminate.");

    }
}
