package dev.lvstrng.argon.gui.components.settings;

import dev.lvstrng.argon.gui.components.ModuleButton;
import dev.lvstrng.argon.module.setting.BooleanSetting;
import dev.lvstrng.argon.module.setting.Setting;
import dev.lvstrng.argon.utils.ColorUtils;
import dev.lvstrng.argon.utils.TextRenderer;
import dev.lvstrng.argon.utils.Utils;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public final class CheckBox extends RenderableSetting {
	private final BooleanSetting setting;
	private Color currentAlpha;

	public CheckBox(ModuleButton parent, Setting<?> setting, int offset) {
		super(parent, setting, offset);
		this.setting = (BooleanSetting) setting;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);

        // New colors from the design
        Color textColorActive = new Color(64, 224, 208); // Turquoise #40E0D0
        Color textColorInactive = new Color(170, 170, 170); // #AAAAAA
        Color boxFillActive = new Color(255, 255, 0); // #FFFF00
        Color boxFillInactive = new Color(0, 0, 0); // #000000
        Color boxBorderInactive = new Color(85, 85, 85); // #555555

        boolean isActive = setting.getValue();
        Color textColor = isActive ? textColorActive : textColorInactive;

        // Draw the checkbox square
        int boxX = parentX() + 5;
        int boxY = (parentY() + parentOffset() + offset) + 8;
        int boxSize = 10;

        if (isActive) {
            context.fill(boxX, boxY, boxX + boxSize, boxY + boxSize, boxFillActive.getRGB());
        } else {
            // Draw border first, then fill inside
            context.fill(boxX, boxY, boxX + boxSize, boxY + boxSize, boxBorderInactive.getRGB());
            context.fill(boxX + 1, boxY + 1, boxX + boxSize - 1, boxY + boxSize - 1, boxFillInactive.getRGB());
        }

        // Draw the setting name
		int nameOffset = parentX() + 20;
		CharSequence chars = setting.getName();
		TextRenderer.drawStringBold(chars, context, nameOffset, (parentY() + parentOffset() + offset) + 9, textColor.getRGB());

        // Keep hover effect consistent
		if (!parent.parent.dragging) {
			int toHoverAlpha = isHovered(mouseX, mouseY) ? 30 : 0;

			if (currentAlpha == null)
				currentAlpha = new Color(255, 255, 255, toHoverAlpha);
			else currentAlpha = new Color(255, 255, 255, currentAlpha.getAlpha());

			if (currentAlpha.getAlpha() != toHoverAlpha)
				currentAlpha = ColorUtils.smoothAlphaTransition(0.05F, toHoverAlpha, currentAlpha);

			context.fill(parentX(), parentY() + parentOffset() + offset, parentX() + parentWidth(), parentY() + parentOffset() + offset + parentHeight(), currentAlpha.getRGB());
		}
	}

	@Override
	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		if(mouseOver && parent.extended) {
			if(keyCode == GLFW.GLFW_KEY_BACKSPACE)
				setting.setValue(setting.getOriginalValue());
		}

		super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (isHovered(mouseX, mouseY) && button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
			setting.toggle();

		super.mouseClicked(mouseX, mouseY, button);
	}
}
