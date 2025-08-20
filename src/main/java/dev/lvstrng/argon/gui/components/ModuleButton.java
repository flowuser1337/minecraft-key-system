package dev.lvstrng.argon.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.lvstrng.argon.Argon;
import dev.lvstrng.argon.gui.Window;
import dev.lvstrng.argon.gui.components.settings.*;
import dev.lvstrng.argon.module.Module;
import dev.lvstrng.argon.module.modules.client.ClickGUI;
import dev.lvstrng.argon.module.setting.*;
import dev.lvstrng.argon.module.setting.ColorSetting;
import dev.lvstrng.argon.gui.components.settings.ColorSlider;
import dev.lvstrng.argon.utils.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static dev.lvstrng.argon.Argon.mc;

public final class ModuleButton {
	public List<RenderableSetting> settings = new ArrayList<>();
	public Window parent;
	public Module module;
	public int offset;
	public boolean extended;
	public int settingOffset;
	public Color currentColor;
	public Color defaultColor = Color.WHITE;
	public Color currentAlpha;
	public AnimationUtils animation = new AnimationUtils(0);

	public ModuleButton(Window parent, Module module, int offset) {
		this.parent = parent;
		this.module = module;
		this.offset = offset;
		this.extended = false;

		settingOffset = parent.getHeight();
		for (Setting<?> setting : module.getSettings()) {
			if (setting instanceof BooleanSetting booleanSetting)
				settings.add(new CheckBox(this, booleanSetting, settingOffset));
			else if (setting instanceof NumberSetting numberSetting)
				settings.add(new Slider(this, numberSetting, settingOffset));
			else if (setting instanceof ModeSetting<?> modeSetting)
				settings.add(new ModeBox(this, modeSetting, settingOffset));
			else if (setting instanceof KeybindSetting keybindSetting)
				settings.add(new KeybindBox(this, keybindSetting, settingOffset));
			else if (setting instanceof StringSetting stringSetting)
				settings.add(new StringBox(this, stringSetting, settingOffset));
			else if (setting instanceof MinMaxSetting minMaxSetting)
				settings.add(new MinMaxSlider(this, minMaxSetting, settingOffset));
			else if (setting instanceof ColorSetting colorSetting) // Added ColorSetting handling
				settings.add(new ColorSlider(this, colorSetting, settingOffset));

			if (setting instanceof ColorSetting) { // ColorSetting takes up more space
				settingOffset += parent.getHeight() * 4;
			} else {
				settingOffset += parent.getHeight();
			}
		}
	}

	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		if (parent.getY() + offset > MinecraftClient.getInstance().getWindow().getHeight())
			return;

		for (RenderableSetting renderableSetting : settings)
			renderableSetting.onUpdate();

        // New colors
        Color textColorActive = new Color(64, 224, 208); // Turquoise #40E0D0
        Color textColorInactive = new Color(255, 255, 255); // #FFFFFF

		Color toColor = module.isEnabled() ? textColorActive : textColorInactive;

		if (defaultColor != toColor)
			defaultColor = ColorUtils.smoothColorTransition(0.1F, toColor, defaultColor);

        // Draw module button background (same as window background)
        Color moduleBgColor = new Color(17, 17, 17, 200); // #111111 with some alpha
        Color moduleBorderColor = new Color(34, 34, 34, 200); // #222222 with some alpha

        RenderUtils.renderRoundedQuad(context.getMatrices(), moduleBorderColor, parent.getX() -1, parent.getY() + offset -1, parent.getX() + parent.getWidth() +1, parent.getY() + parent.getHeight() + offset +1, 5, 5, 5, 5, 50);
        RenderUtils.renderRoundedQuad(context.getMatrices(), moduleBgColor, parent.getX(), parent.getY() + offset, parent.getX() + parent.getWidth(), parent.getY() + parent.getHeight() + offset, 5, 5, 5, 5, 50);

		CharSequence nameChars = module.getName();

        // Left-align the text with a 5px padding
		TextRenderer.drawStringBold(nameChars, context, parent.getX() + 5, parent.getY() + offset + 8, defaultColor.getRGB());

		renderHover(context, mouseX, mouseY, delta);
		renderSettings(context, mouseX, mouseY, delta);

		for(RenderableSetting renderableSetting : settings)
			if(extended) renderableSetting.renderDescription(context, mouseX, mouseY, delta);

		if (isHovered(mouseX, mouseY) && !parent.dragging) {
            // Restyled description box
            Color bgColor = new Color(17, 17, 17, 220); // #111111 with alpha
            Color borderColor = new Color(34, 34, 34, 220); // #222222 with alpha

			CharSequence chars = module.getDescription();
			int tw = TextRenderer.getWidth(chars);

			int parentCenter = mc.getWindow().getFramebufferWidth() / 2;
			int textCenter = parentCenter - tw / 2;

            RenderUtils.renderRoundedQuad(context.getMatrices(), borderColor, textCenter - 6, ((double) mc.getWindow().getFramebufferHeight() / 2) + 293, textCenter + tw + 6, ((double) mc.getWindow().getFramebufferHeight() / 2) + 319, 4, 10);
			RenderUtils.renderRoundedQuad(context.getMatrices(), bgColor, textCenter - 5, ((double) mc.getWindow().getFramebufferHeight() / 2) + 294, textCenter + tw + 5, ((double) mc.getWindow().getFramebufferHeight() / 2) + 318, 3, 10);

			TextRenderer.drawString(chars, context, textCenter, (mc.getWindow().getFramebufferHeight() / 2) + 300, Color.WHITE.getRGB());
		}
	}

	private void renderHover(DrawContext context, int mouseX, int mouseY, float delta) {
		if (!parent.dragging) {
			int toHoverAlpha = isHovered(mouseX, mouseY) ? 30 : 0; // More subtle hover

			if (currentAlpha == null)
				currentAlpha = new Color(255, 255, 255, toHoverAlpha);
			else currentAlpha = new Color(255, 255, 255, currentAlpha.getAlpha());

			if (currentAlpha.getAlpha() != toHoverAlpha)
				currentAlpha = ColorUtils.smoothAlphaTransition(0.05F, toHoverAlpha, currentAlpha);

			context.fill(parent.getX(), parent.getY() + offset, parent.getX() + parent.getWidth(), parent.getY() + offset + parent.getHeight(), currentAlpha.getRGB());
		}
	}

	private void renderSettings(DrawContext context, int mouseX, int mouseY, float delta) {
        // Background for expanded settings area
        Color settingsBgColor = new Color(25, 25, 25, 200); // A slightly lighter dark color for contrast
        if (extended && animation.getValue() > 0) {
            RenderUtils.renderRoundedQuad(context.getMatrices(), settingsBgColor, parent.getX(), parent.getY() + offset + parent.getHeight(), parent.getX() + parent.getWidth(), parent.getY() + offset + parent.getHeight() + animation.getValue(), 0, 0, 5, 5, 10);
        }

		int scissorX = parent.getX();
		int scissorY = (int) (mc.getWindow().getHeight() - (parent.getY() + offset + parent.getHeight() + animation.getValue()));
		int scissorWidth = parent.getWidth();
		int scissorHeight = (int) animation.getValue();

		RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);

		for (RenderableSetting renderableSetting : settings)
			if(animation.getValue() > parent.getHeight())
				renderableSetting.render(context, mouseX, mouseY, delta);

        // This specific rendering is now moved to the component itself
		// for (RenderableSetting renderableSetting : settings) { ... }

		RenderSystem.disableScissor();
	}

	public void onExtend() {
		for(ModuleButton moduleButton : parent.moduleButtons) {
			moduleButton.extended = false;
		}
	}

	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		for (RenderableSetting setting : settings)
			setting.keyPressed(keyCode, scanCode, modifiers);
	}

	public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (extended)
			for (RenderableSetting renderableSetting : settings)
				renderableSetting.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (isHovered(mouseX, mouseY)) {
			if (button == 0)
				module.toggle();

			if (button == 1) {
				if (module.getSettings().isEmpty()) return;
				if (!extended)
					onExtend();

				extended = !extended;
			}
		}
		if (extended) {
			for (RenderableSetting renderableSetting : settings) {
				renderableSetting.mouseClicked(mouseX, mouseY, button);
			}
		}
	}

	public void onGuiClose() {
		this.currentAlpha = null;
		this.currentColor = null;

		for (RenderableSetting renderableSetting : settings)
			renderableSetting.onGuiClose();
	}

	public void mouseReleased(double mouseX, double mouseY, int button) {
		for (RenderableSetting renderableSetting : settings)
			renderableSetting.mouseReleased(mouseX, mouseY, button);
	}

	public boolean isHovered(double mouseX, double mouseY) {
		return mouseX > parent.getX()
				&& mouseX < parent.getX() + parent.getWidth()
				&& mouseY > parent.getY() + offset
				&& mouseY < parent.getY() + offset + parent.getHeight();
	}
}