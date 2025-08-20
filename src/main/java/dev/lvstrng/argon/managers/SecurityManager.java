package dev.lvstrng.argon.managers;

import java.lang.management.ManagementFactory;
import java.io.File;

public class SecurityManager {

    public static void check() {
        // Anti-Debugging
        if (ManagementFactory.getRuntimeMXBean().getInputArguments().toString().contains("-agentlib:jdwp")) {
            System.out.println("Debugger detected! Exiting...");
            System.exit(0);
        }

        // Anti-Tampering (check if running from JAR)
        String protocol = SecurityManager.class.getResource("").getProtocol();
        if (!"jar".equals(protocol)) {
            System.out.println("Not running from JAR! Exiting...");
            System.exit(0);
        }
    }
}
