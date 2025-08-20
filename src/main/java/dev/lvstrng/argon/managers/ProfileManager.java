package dev.lvstrng.argon.managers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.lvstrng.argon.Argon;
import dev.lvstrng.argon.module.Module;
import dev.lvstrng.argon.module.setting.*;
import dev.lvstrng.argon.utils.EncryptedString;

import java.awt.*; // Import Color class
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ProfileManager {
	private final Gson g = new Gson();
	private Path profileFolderPath;
	private Path profilePath;
	private String temp = System.getProperty("user.home");
	private String folderName = EncryptedString.of(".argonclient").toString();
	Path folder = Paths.get(temp, folderName);
	private JsonObject profile;

	public ProfileManager() {
		profileFolderPath = folder;
		profilePath = profileFolderPath.resolve(EncryptedString.of("a.json").toString());
	}

	public void loadProfile() {
		try {
			if (Files.isRegularFile(profilePath)) {
				profile = g.fromJson(Files.readString(profilePath), JsonObject.class);

				for (Module module : Argon.INSTANCE.getModuleManager().getModules()) {
					JsonElement moduleJson = profile.get(String.valueOf(Argon.INSTANCE.getModuleManager().getModules().indexOf(module)));
					if (moduleJson == null || !moduleJson.isJsonObject())
						continue;
					JsonObject moduleConfig = moduleJson.getAsJsonObject();

					JsonElement enabledJson = moduleConfig.get(EncryptedString.of("enabled").toString());
					if (enabledJson == null || !enabledJson.isJsonPrimitive())
						continue;

					if (enabledJson.getAsBoolean())
						module.setEnabled(true);

					for (Setting<?> setting : module.getSettings()) {
						JsonElement settingJson = moduleConfig.get(String.valueOf(module.getSettings().indexOf(setting)));
						if (settingJson == null)
							continue;

						if (setting instanceof BooleanSetting booleanSetting) {
							booleanSetting.setValue(settingJson.getAsBoolean());
						} else if (setting instanceof ModeSetting<?> modeSetting) {
							modeSetting.setModeIndex(settingJson.getAsInt());
						} else if (setting instanceof NumberSetting numberSetting) {
							numberSetting.setValue(settingJson.getAsDouble());
						} else if (setting instanceof KeybindSetting keybindSetting) {
							keybindSetting.setKey(settingJson.getAsInt());
							if(keybindSetting.isModuleKey())
								module.setKey(settingJson.getAsInt());
						} else if (setting instanceof StringSetting stringSetting) {
							stringSetting.setValue(settingJson.getAsString());
						} else if (setting instanceof MinMaxSetting minMaxSetting) {
							if (settingJson.isJsonObject()) {
								JsonObject minMaxObject = settingJson.getAsJsonObject();
								double minValue = minMaxObject.get(EncryptedString.of("1").toString()).getAsDouble();
								double maxValue = minMaxObject.get(EncryptedString.of("2").toString()).getAsDouble();

								minMaxSetting.setMinValue(minValue);
								minMaxSetting.setMaxValue(maxValue);
							}
						} else if (setting instanceof ColorSetting colorSetting) {
							colorSetting.setValue(new Color(settingJson.getAsInt()));
						}
					}
				}
			} else {
				Files.createDirectories(profileFolderPath);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveProfile() {
		try {
			Files.createDirectories(profileFolderPath);
			profile = new JsonObject();

			for (Module module : Argon.INSTANCE.getModuleManager().getModules()) {
				JsonObject moduleConfig = new JsonObject();

				moduleConfig.addProperty(EncryptedString.of("enabled").toString(), module.isEnabled());
				for (Setting<?> setting : module.getSettings()) {
					if (setting instanceof BooleanSetting booleanSetting) {
						moduleConfig.addProperty(String.valueOf(module.getSettings().indexOf(setting)), booleanSetting.getValue());
					} else if (setting instanceof ModeSetting<?> modeSetting) {
						moduleConfig.addProperty(String.valueOf(module.getSettings().indexOf(setting)), modeSetting.getModeIndex());
					} else if (setting instanceof NumberSetting numberSetting) {
						moduleConfig.addProperty(String.valueOf(module.getSettings().indexOf(setting)), numberSetting.getValue());
					} else if (setting instanceof KeybindSetting keybindSetting) {
						moduleConfig.addProperty(String.valueOf(module.getSettings().indexOf(setting)), keybindSetting.getKey());
					} else if (setting instanceof StringSetting stringSetting) {
						moduleConfig.addProperty(String.valueOf(module.getSettings().indexOf(setting)), stringSetting.getValue());
					} else if (setting instanceof MinMaxSetting minMaxSetting) {
						JsonObject minMaxObject = new JsonObject();
						minMaxObject.addProperty(EncryptedString.of("1").toString(), minMaxSetting.getMinValue());
						minMaxObject.addProperty(EncryptedString.of("2").toString(), minMaxSetting.getMaxValue());

						moduleConfig.add(String.valueOf(module.getSettings().indexOf(setting)), minMaxObject);
					} else if (setting instanceof ColorSetting colorSetting) {
						moduleConfig.addProperty(String.valueOf(module.getSettings().indexOf(setting)), colorSetting.getRGB());
					}
				}

				profile.add(String.valueOf(Argon.INSTANCE.getModuleManager().getModules().indexOf(module)), moduleConfig);
			}
			Files.writeString(profilePath, g.toJson(profile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Path getProfileFolderPath() {
		return profileFolderPath;
	}
}