package com.github.klainstom.terminus.sshd;

import com.github.klainstom.terminus.ExtensionMain;
import net.minestom.server.MinecraftServer;
import org.apache.sshd.common.mac.MacInformation;
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
    private final ChannelSession channelSession;

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
    public void start(ChannelSession channel, Environment env) {
        MinecraftServer.LOGGER.info("Open Shell...");
        (thread = new Thread(this)).start();
    }

    @Override
    public void destroy(ChannelSession channel) throws Exception {
        MinecraftServer.LOGGER.info("Destroy shell...");
        running = false;
        thread.join();
    }

    @Override
    public void run() {
        MinecraftServer.LOGGER.info("Start shell thread...");
        // FIXME: 22.01.22 undefined symbol openpty
        try {
            MinecraftServer.LOGGER.info("TerminalBuilder");
            TerminalBuilder builder = TerminalBuilder.builder()
                    .system(false)
                    .streams(in, out)
                    .jansi(false).jna(false); // TODO: 23.01.22 fix error with enabled jansi
            MinecraftServer.LOGGER.info("Terminal");
            Terminal terminal = builder.build();
            MinecraftServer.LOGGER.info("LineReader");
            LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .build();

            terminal.writer().println("Terminos SSHD");

            terminal.resume();
            String line;
            while (running) {
                try { line = reader.readLine("=> "); }
                catch (UserInterruptException ignored) { continue; }
                catch (EndOfFileException ignored) { break; }
                if (line == null) break;

                switch (line) {
                    case "" -> { }
                    case "exit" -> running = false;
                    case "uptime", "up" -> {
                        long period = System.currentTimeMillis() - ExtensionMain.START_TIME;
                        long D = TimeUnit.MILLISECONDS.toDays(period);
                        long HH = TimeUnit.MILLISECONDS.toHours(period) % 24;
                        long MM = TimeUnit.MILLISECONDS.toMinutes(period) % 60;
                        long SS = TimeUnit.MILLISECONDS.toSeconds(period) % 60;
                        terminal.writer().printf("Up %d days %02d:%02d:%02d\n", D, HH, MM, SS);
                    }
                    default -> terminal.writer().println("Unknown command: "+line); // execute as minestom command
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            exitCallback.onExit(0);
            channelSession.getSession().close(true);
        }
    }
}
