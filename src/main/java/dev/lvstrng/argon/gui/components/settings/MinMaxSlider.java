package dev.lvstrng.argon.gui.components.settings;

import dev.lvstrng.argon.gui.components.ModuleButton;
import dev.lvstrng.argon.module.setting.MinMaxSetting;
import dev.lvstrng.argon.module.setting.Setting;
import dev.lvstrng.argon.utils.EncryptedString;
import dev.lvstrng.argon.utils.MathUtils;
import dev.lvstrng.argon.utils.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public final class MinMaxSlider extends RenderableSetting {
    private final MinMaxSetting setting;
    public double lerpedOffsetMinX;
    public double lerpedOffsetMaxX;

    public MinMaxSlider(ModuleButton parent, Setting<?> setting, int offset) {
        super(parent, setting, offset);
        this.setting = (MinMaxSetting) setting;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // New colors from the design
        Color textColor = new Color(64, 224, 208); // Turquoise #40E0D0
        Color barColor = new Color(255, 215, 0); // #FFD700
        Color handleColor = new Color(255, 255, 0); // #FFFF00 (Neon Yellow)
        Color trackColor = new Color(34, 34, 34); // #222222 (Border Gray)

        String minVal = String.format("%.2f", setting.getMinValue());
        String maxVal = String.format("%.2f", setting.getMaxValue());

        // Draw setting name left-aligned
        TextRenderer.drawStringBold(EncryptedString.of("Delay").toString(), context, parentX() + 5, (parentY() + parentOffset() + offset) + 9, textColor.getRGB());

        // Draw values right-aligned
        CharSequence valueStr = (setting.getMinValue() == setting.getMaxValue() ? minVal : minVal + EncryptedString.of(" - ").toString() + maxVal);
        int valueWidth = TextRenderer.getWidth(valueStr);
        TextRenderer.drawStringBold(valueStr, context, parentX() + parentWidth() - 5 - valueWidth, (parentY() + parentOffset() + offset) + 9, textColor.getRGB());

        double sliderWidth = parentWidth() - 10;
        double minPercentage = (setting.getMinValue() - setting.getMin()) / (setting.getMax() - setting.getMin());
        double maxPercentage = (setting.getMaxValue() - setting.getMin()) / (setting.getMax() - setting.getMin());

        double filledMinX = sliderWidth * minPercentage;
        double filledMaxX = sliderWidth * maxPercentage;

        // Draw the slider track
        context.fill(parentX() + 5, parentY() + parentOffset() + offset + 25, parentX() + parentWidth() - 5, parentY() + parentOffset() + offset + 27, trackColor.getRGB());
        // Draw the filled part of the slider
        context.fill(parentX() + 5 + (int) filledMinX, parentY() + parentOffset() + offset + 25, parentX() + 5 + (int) filledMaxX, parentY() + parentOffset() + offset + 27, barColor.getRGB());

        // Draw the two square handles
        double handleMinX = parentX() + 5 + filledMinX - 2; // Center the handle
        double handleMaxX = parentX() + 5 + filledMaxX - 2; // Center the handle
        double handleY = parentY() + parentOffset() + offset + 23; // Adjust Y to be centered on the bar

        context.fill((int)handleMinX, (int)handleY, (int)handleMinX + 4, (int)handleY + 4, handleColor.getRGB());
        context.fill((int)handleMaxX, (int)handleY, (int)handleMaxX + 4, (int)handleY + 4, handleColor.getRGB());

        lerpedOffsetMinX = MathUtils.goodLerp((float)0.3 * delta, lerpedOffsetMinX, filledMinX);
        lerpedOffsetMaxX = MathUtils.goodLerp((float)0.3 * delta, lerpedOffsetMaxX, filledMaxX);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY) && button == 0) {
            double value = MathHelper.clamp((mouseX - (parentX() + 5)) / (parentWidth() - 10) * (setting.getMax() - setting.getMin()) + setting.getMin(), setting.getMin(), setting.getMax());
            if (Math.abs(value - setting.getMinValue()) < Math.abs(value - setting.getMaxValue())) {
                setting.setMinValue(value);
            } else {
                setting.setMaxValue(value);
            }
        }
    }

    @Override
    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isHovered(mouseX, mouseY) && button == 0) {
            double value = MathHelper.clamp((mouseX - (parentX() + 5)) / (parentWidth() - 10) * (setting.getMax() - setting.getMin()) + setting.getMin(), setting.getMin(), setting.getMax());
            if (Math.abs(value - setting.getMinValue()) < Math.abs(value - setting.getMaxValue())) {
                setting.setMinValue(value);
            } else {
                setting.setMaxValue(value);
            }
        }
    }
}
