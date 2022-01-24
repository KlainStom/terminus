package com.github.klainstom.terminus.sshd;

import net.minestom.server.command.CommandSender;
import net.minestom.server.permission.Permission;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jline.terminal.Terminal;

import java.util.Set;

public class SshdSender implements CommandSender {
    private final Terminal terminal;

    public SshdSender(Terminal terminal) {
        this.terminal = terminal;
    }

    @Override
    public @NotNull Set<Permission> getAllPermissions() {
        return Set.of();
    }

    @Override
    public <T> @Nullable T getTag(@NotNull Tag<T> tag) {
        return null;
    }

    @Override
    public <T> void setTag(@NotNull Tag<T> tag, @Nullable T value) {

    }

    @Override
    public void sendMessage(@NotNull String message) {
        terminal.writer().printf("\r%s\n=> ", message);
    }

    @Override
    public void sendMessage(@NotNull String @NotNull [] messages) {
        for (String msg : messages) {
            terminal.writer().printf("\r%s\n=> ", msg);
        }
    }
}
