package com.github.klainstom.terminus.sshd;

import com.github.klainstom.terminus.ExtensionMain;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.command.builder.CommandResult;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

public class TerminusShell implements Command, Runnable {
    private static final CommandManager COMMAND_MANAGER = MinecraftServer.getCommandManager();

    private final ChannelSession channelSession;
    private Terminal terminal;
    private LineReader reader;

    private ExitCallback exitCallback;

    private PrintStream err;
    private PrintStream out;
    private InputStream in;

    private boolean running = true;
    private Thread thread;


    public TerminusShell(ChannelSession channelSession) {
        this.channelSession = channelSession;
    }

    @Override
    public void setExitCallback(ExitCallback callback) {
        this.exitCallback = callback;
    }

    @Override
    public void setErrorStream(OutputStream err) {
        this.err = new PrintStream(err);
    }

    @Override
    public void setOutputStream(OutputStream out) {
        this.out = new PrintStream(out);
    }

    @Override
    public void setInputStream(InputStream in) {
        this.in = in;
    }

    @Override
    public void start(ChannelSession channel, Environment env) throws IOException {
        this.terminal = TerminalBuilder.builder()
                .system(false)
                .streams(in, out)
                .jansi(false).jna(false) // TODO: 23.01.22 fix error with enabled jansi
                .build();
        this.reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build();

        MinecraftServer.LOGGER.info("Open Shell for {}", channel.getSession().getUsername());
        (thread = new Thread(this)).start();
    }

    @Override
    public void destroy(ChannelSession channel) throws Exception {
        MinecraftServer.LOGGER.info("Close Shell for {}", channel.getSession().getUsername());
        running = false;
        thread.join();
    }

    @Override
    public void run() {
        print("==== Terminos SSHD ====");

        String line;
        while (running) {
            try { line = reader.readLine("=> "); }
            catch (UserInterruptException ignored) { continue; }
            catch (EndOfFileException ignored) { break; }
            if (line == null) break;
            if (line.isBlank()) continue;

            executeCommand(line);
        }

        exitCallback.onExit(0);
        channelSession.getSession().close(true);
    }

    public void executeCommand(String command) {
        switch (command) {
            case "exit" -> running = false;
            case "uptime", "up" -> {
                long period = System.currentTimeMillis() - ExtensionMain.START_TIME;
                long D = TimeUnit.MILLISECONDS.toDays(period);
                long HH = TimeUnit.MILLISECONDS.toHours(period) % 24;
                long MM = TimeUnit.MILLISECONDS.toMinutes(period) % 60;
                long SS = TimeUnit.MILLISECONDS.toSeconds(period) % 60;
                print("Up %d days %02d:%02d:%02d\n".formatted(D, HH, MM, SS));
            }
            default -> {
                CommandResult result = COMMAND_MANAGER.execute(
                        COMMAND_MANAGER.getConsoleSender(), command);
                switch (result.getType()) {
                    case UNKNOWN -> print("Unknown command: %s\n".formatted(result.getInput()));
                    case INVALID_SYNTAX -> print("Invalid command syntax: %s\n".formatted(result.getInput()));
                    case CANCELLED -> print("Command was cancelled: %s\n".formatted(result.getInput()));
                    case SUCCESS -> print("Successfully executed.");
                }
            }
        }
    }

    public void print(String line) { reader.printAbove(line); }
}
