package com.github.klainstom.terminus.sshd;

import net.minestom.server.command.ConsoleSender;
import org.jetbrains.annotations.NotNull;

public class TerminusSender extends ConsoleSender {
    private final TerminusShell shell;

    protected TerminusSender(TerminusShell shell) {
        this.shell = shell;
    }

    @Override
    public void sendMessage(@NotNull String message) {
        shell.print(message);
    }
}
