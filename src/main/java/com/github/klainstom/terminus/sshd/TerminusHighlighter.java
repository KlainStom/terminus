package com.github.klainstom.terminus.sshd;

import net.minestom.server.MinecraftServer;
import org.jline.reader.Highlighter;
import org.jline.reader.LineReader;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import java.util.regex.Pattern;

public class TerminusHighlighter implements Highlighter {

    @Override public void setErrorPattern(Pattern errorPattern) {}

    @Override public void setErrorIndex(int errorIndex) {}

    @Override
    public AttributedString highlight(LineReader reader, String buffer) {
        AttributedStringBuilder builder = new AttributedStringBuilder();
        String[] words = buffer.split(" ");

        for (int index = 0; index < words.length; index++) {
            AttributedStyle style = AttributedStyle.DEFAULT;
            if (index == 0) {
                if (TerminusShell.SHELL_COMMANDS.contains(words[0]))
                    style = AttributedStyle.DEFAULT.foreground(AttributedStyle.MAGENTA);
                else if (MinecraftServer.getCommandManager().commandExists(words[0]))
                    style = AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN);
            }
            builder.append(new AttributedString(words[index]+" ", style));
        }

        return builder.toAttributedString();
    }
}
