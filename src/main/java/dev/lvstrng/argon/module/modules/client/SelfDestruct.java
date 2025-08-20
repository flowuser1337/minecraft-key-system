package dev.lvstrng.argon.module.modules.client;

import com.sun.jna.Memory;
import dev.lvstrng.argon.Argon;
import dev.lvstrng.argon.gui.ClickGui;
import dev.lvstrng.argon.module.Category;
import dev.lvstrng.argon.module.Module;
import dev.lvstrng.argon.module.setting.*;
import dev.lvstrng.argon.utils.EncryptedString;
import dev.lvstrng.argon.utils.Utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import org.lwjgl.glfw.GLFW;

import static dev.lvstrng.argon.Argon.mc;

@SuppressWarnings("all")
public final class SelfDestruct extends Module {
	

	private final BooleanSetting replaceMod = new BooleanSetting(EncryptedString.of("Replace Mod"), true);

	private final BooleanSetting saveLastModified = new BooleanSetting(EncryptedString.of("Save Last Modified"), true);

	private final StringSetting downloadURL = new StringSetting(EncryptedString.of("Replace URL"), EncryptedString.of("https://cdn.modrinth.com/data/5ZwdcRci/versions/FEOsWs1E/ImmediatelyFast-Fabric-1.2.11%2B1.20.4.jar").toString());

	public SelfDestruct() {
		super(EncryptedString.of("Self Destruct"),
				EncryptedString.of("Removes the client from your game |Credits to lwes for deletion|"),
				-1,
				Category.FLOW);
		replaceMod.setDescription(EncryptedString.of("Repalces the mod with the original JAR file of the ImmediatelyFast mod"));
		saveLastModified.setDescription(EncryptedString.of("Saves the last modified date after self destruct"));
		addSettings(replaceMod, saveLastModified, downloadURL);
	}

	@Override
	public void onEnable() {
		

		Argon.INSTANCE.getModuleManager().getModule(ClickGUI.class).setEnabled(false);
		// Set ClickGUI keybind to UNKNOWN to prevent re-opening
		Argon.INSTANCE.getModuleManager().getModule(ClickGUI.class).setKey(GLFW.GLFW_KEY_UNKNOWN);

		setEnabled(false);

		Argon.INSTANCE.getProfileManager().saveProfile();

		try {
			// Delete the profile folder
			Path profileFolderPath = Argon.INSTANCE.getProfileManager().getProfileFolderPath();
			if (Files.exists(profileFolderPath)) {
				Files.walk(profileFolderPath)
					.sorted(Comparator.reverseOrder())
					.map(Path::toFile)
					.forEach(File::delete);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (mc.currentScreen instanceof ClickGui) {
			Argon.INSTANCE.guiInitialized = false;
			mc.currentScreen.close();
			// Set clickGui instance to null to prevent re-opening
			Argon.INSTANCE.clickGui = null;
		}

		if (replaceMod.getValue()) {
			try {
				String modUrl = downloadURL.getValue();
				File currentJar = Utils.getCurrentJarPath();

				if (currentJar.exists())
                    Utils.replaceModFile(modUrl, Utils.getCurrentJarPath());
			} catch (Exception ignored) {}
		}

		// Remove SelfDestruct from the module list
		Argon.INSTANCE.getModuleManager().getModules().remove(this);
	}
}
