package com.github.klainstom.terminus.sshd.authentication;

import com.github.klainstom.terminus.Settings;
import inet.ipaddr.AddressStringException;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import org.apache.sshd.server.session.ServerSession;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class AccessControl {
    private static volatile List<IPAddress> PUBLIC_KEY_SOURCE_IP;
    private static volatile List<IPAddress> PASSWORD_SOURCE_IP;
    private static volatile List<IPAddress> INTERACTIVE_SOURCE_IP;
    private static volatile long nextUpdate = 0;

    public static boolean isAllowed(String username, AccessMethod method, ServerSession session) {
        update();
        IPAddress remote = new IPAddressString(
                ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress()).getHostAddress();
        switch (method) {
            case PUBLIC_KEY -> { return PUBLIC_KEY_SOURCE_IP.stream().anyMatch(netIp -> netIp.contains(remote)); }
            case PASSWORD -> { return PASSWORD_SOURCE_IP.stream().anyMatch(netIp -> netIp.contains(remote)); }
            case INTERACTIVE -> { return INTERACTIVE_SOURCE_IP.stream().anyMatch(netIp -> netIp.contains(remote)); }
        }
        return false;
    }

    private static void update() {
        if (nextUpdate > System.currentTimeMillis()) return;
        nextUpdate = System.currentTimeMillis() + 10000;
        try {
            List<IPAddress> publicKey = new ArrayList<>();
            List<IPAddress> password = new ArrayList<>();
            List<IPAddress> interactive = new ArrayList<>();

            for (String sourceIp : Settings.getPublicKeySourceIps()) {
                publicKey.add(new IPAddressString(sourceIp).toAddress());
            }
            for (String sourceIp : Settings.getPasswordSourceIps()) {
                password.add(new IPAddressString(sourceIp).toAddress());
            }
            for (String sourceIp : Settings.getInteractiveSourceIps()) {
                interactive.add(new IPAddressString(sourceIp).toAddress());
            }
            PUBLIC_KEY_SOURCE_IP = List.copyOf(publicKey);
            PASSWORD_SOURCE_IP = List.copyOf(password);
            INTERACTIVE_SOURCE_IP = List.copyOf(interactive);
        } catch (AddressStringException e) {
            e.printStackTrace();
        }
    }

    public enum AccessMethod {
        PUBLIC_KEY, PASSWORD, INTERACTIVE
    }
}
