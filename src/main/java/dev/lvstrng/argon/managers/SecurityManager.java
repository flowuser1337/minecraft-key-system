package dev.lvstrng.argon.managers;

import net.minecraft.client.MinecraftClient;
// Asegúrate de que la ruta a tu pantalla de activación sea correcta
import dev.lvstrng.argon.gui.ActivationScreen;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class SecurityManager {

    private static final Path CONFIG_PATH = Paths.get(System.getProperty("user.home"), ".flow_config");

    public static void check() {
        // --- Comprobaciones de Seguridad Iniciales ---
        if (ManagementFactory.getRuntimeMXBean().getInputArguments().toString().contains("-agentlib:jdwp")) {
            System.err.println("Debugger detected! Flow will not initialize.");
            return; // Evitamos el crasheo, simplemente no iniciamos.
        }
        String protocol = SecurityManager.class.getResource("").getProtocol();
        if (!"jar".equals(protocol)) {
            System.err.println("Not running from JAR! Flow will not initialize.");
            return; // Evitamos el crasheo.
        }

        // --- Lógica de Licencia y HWID ---
        try {
            Properties config = loadConfig();
            String storedUsername = config.getProperty("username");

            if (storedUsername == null || storedUsername.isEmpty()) {
                // Si no hay configuración, le decimos a Minecraft que abra nuestra pantalla de activación.
                MinecraftClient.getInstance().execute(() -> {
                    MinecraftClient.getInstance().setScreen(new ActivationScreen());
                });
            } else {
                // La validación de HWID se hará en otro punto del arranque del mod
                // (Opcional, se puede añadir aquí si se desea)
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Manejar error de forma segura en la consola, sin crashear si es posible.
        }
    }

    /**
     * Carga la configuración desde el archivo local .flow_config.
     */
    public static Properties loadConfig() throws IOException {
        Properties props = new Properties();
        if (Files.exists(CONFIG_PATH)) {
            try (var in = Files.newInputStream(CONFIG_PATH)) {
                props.load(in);
            }
        }
        return props;
    }

    /**
     * Guarda el nombre de usuario en el archivo de configuración local.
     */
    public static void saveConfig(String username) throws IOException {
        Properties props = new Properties();
        props.setProperty("username", username);
        try (var out = Files.newOutputStream(CONFIG_PATH)) {
            props.store(out, "Flow Client Configuration - Do not share this file");
        }
    }
}