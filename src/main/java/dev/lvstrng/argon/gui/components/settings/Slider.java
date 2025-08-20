package dev.lvstrng.argon.gui.components.settings;

import dev.lvstrng.argon.gui.components.ModuleButton;
import dev.lvstrng.argon.module.setting.NumberSetting;
import dev.lvstrng.argon.utils.ColorUtils;
import dev.lvstrng.argon.utils.EncryptedString;
import dev.lvstrng.argon.utils.MathUtils;
import dev.lvstrng.argon.utils.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public final class Slider extends RenderableSetting {
    private final NumberSetting setting;
    public double lerpedOffsetX;

    public Slider(ModuleButton parent, NumberSetting setting, int offset) {
        super(parent, setting, offset);
        this.setting = setting;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // New colors from the design
        Color textColor = new Color(64, 224, 208); // Turquoise #40E0D0
        Color barColor = new Color(255, 215, 0); // #FFD700
        Color handleColor = new Color(255, 255, 0); // #FFFF00 (Neon Yellow)
        Color trackColor = new Color(34, 34, 34); // #222222 (Border Gray)

        TextRenderer.drawStringBold(setting.getName() + EncryptedString.of(": ").toString() + setting.getValue().toString(), context, parentX() + 5, (parentY() + parentOffset() + offset) + 9, textColor.getRGB());

        double sliderWidth = parentWidth() - 10;
        double valuePercentage = (setting.getValue() - setting.getMin()) / (setting.getMax() - setting.getMin());
        double filledWidth = sliderWidth * valuePercentage;

        // Draw the slider track
        context.fill(parentX() + 5, parentY() + parentOffset() + offset + 25, parentX() + parentWidth() - 5, parentY() + parentOffset() + offset + 27, trackColor.getRGB());
        // Draw the filled part of the slider
        context.fill(parentX() + 5, parentY() + parentOffset() + offset + 25, parentX() + 5 + (int) filledWidth, parentY() + parentOffset() + offset + 27, barColor.getRGB());

        // Draw the square handle
        double handleX = parentX() + 5 + filledWidth - 2; // Center the handle
        double handleY = parentY() + parentOffset() + offset + 23; // Adjust Y to be centered on the bar
        context.fill((int)handleX, (int)handleY, (int)handleX + 4, (int)handleY + 4, handleColor.getRGB());

        lerpedOffsetX = MathUtils.goodLerp((float)(0.3 * delta), lerpedOffsetX, filledWidth);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY) && button == 0) {
            setting.setValue(MathHelper.clamp((mouseX - (parentX() + 5)) / (parentWidth() - 10) * (setting.getMax() - setting.getMin()) + setting.getMin(), setting.getMin(), setting.getMax()));
        }
    }

    @Override
    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isHovered(mouseX, mouseY) && button == 0) {
            setting.setValue(MathHelper.clamp((mouseX - (parentX() + 5)) / (parentWidth() - 10) * (setting.getMax() - setting.getMin()) + setting.getMin(), setting.getMin(), setting.getMax()));
        }
    }
}
