package com.github.klainstom.terminus.sshd.authentication;

import com.github.klainstom.terminus.Settings;
import inet.ipaddr.AddressStringException;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import org.apache.sshd.server.session.ServerSession;

import java.net.InetSocketAddress;

public class AccessControl {
    private static volatile IPAddress PUBLIC_KEY_SOURCE_IP;
    private static volatile IPAddress PASSWORD_SOURCE_IP;
    private static volatile IPAddress INTERACTIVE_SOURCE_IP;
    private static volatile long nextUpdate = 0;

    public static boolean isAllowed(String username, AccessMethod method, ServerSession session) {
        update();
        IPAddress remote = new IPAddressString(
                ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress()).getHostAddress();
        switch (method) {
            case PUBLIC_KEY -> { return PUBLIC_KEY_SOURCE_IP.contains(remote); }
            case PASSWORD -> { return PASSWORD_SOURCE_IP.contains(remote); }
            case INTERACTIVE -> { return INTERACTIVE_SOURCE_IP.contains(remote); }
        }
        return false;
    }

    private static void update() {
        if (nextUpdate > System.currentTimeMillis()) return;
        nextUpdate = System.currentTimeMillis() + 10000;
        try {
            PUBLIC_KEY_SOURCE_IP = new IPAddressString(Settings.getPublicKeySourceIp()).toAddress();
            PASSWORD_SOURCE_IP = new IPAddressString(Settings.getPasswordSourceIp()).toAddress();
            INTERACTIVE_SOURCE_IP = new IPAddressString(Settings.getInteractiveSourceIp()).toAddress();
        } catch (AddressStringException e) {
            e.printStackTrace();
        }
    }

    public enum AccessMethod {
        PUBLIC_KEY, PASSWORD, INTERACTIVE
    }
}
