package dev.lvstrng.argon.gui.components.settings;

import dev.lvstrng.argon.gui.components.ModuleButton;
import dev.lvstrng.argon.module.setting.ColorSetting;
import dev.lvstrng.argon.module.setting.Setting;
import dev.lvstrng.argon.utils.ColorUtils;
import dev.lvstrng.argon.utils.EncryptedString;
import dev.lvstrng.argon.utils.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public final class ColorSlider extends RenderableSetting {
    private final ColorSetting colorSetting;
    public Color currentColor1;
    public Color currentColor2;
    public double lerpedOffsetX;

    public ColorSlider(ModuleButton parent, Setting<?> setting, int offset) {
        super(parent, setting, offset);
        this.colorSetting = (ColorSetting) setting;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        TextRenderer.drawString(EncryptedString.of("Red: ").toString() + colorSetting.getRed(), context, parentX() + 5, (parentY() + parentOffset() + offset) + 9, Color.WHITE.getRGB());
        TextRenderer.drawString(EncryptedString.of("Green: ").toString() + colorSetting.getGreen(), context, parentX() + 5, (parentY() + parentOffset() + offset) + 9 + parentHeight(), Color.WHITE.getRGB());
        TextRenderer.drawString(EncryptedString.of("Blue: ").toString() + colorSetting.getBlue(), context, parentX() + 5, (parentY() + parentOffset() + offset) + 9 + parentHeight() * 2, Color.WHITE.getRGB());
        TextRenderer.drawString(EncryptedString.of("Alpha: ").toString() + colorSetting.getAlpha(), context, parentX() + 5, (parentY() + parentOffset() + offset) + 9 + parentHeight() * 3, Color.WHITE.getRGB());

        // Render sliders for each color component
        renderColorComponentSlider(context, mouseX, mouseY, delta, colorSetting.getRed(), 0, 255, parentX() + 5, (parentY() + parentOffset() + offset) + 25, colorSetting::setRed);
        renderColorComponentSlider(context, mouseX, mouseY, delta, colorSetting.getGreen(), 0, 255, parentX() + 5, (parentY() + parentOffset() + offset) + 25 + parentHeight(), colorSetting::setGreen);
        renderColorComponentSlider(context, mouseX, mouseY, delta, colorSetting.getBlue(), 0, 255, parentX() + 5, (parentY() + parentOffset() + offset) + 25 + parentHeight() * 2, colorSetting::setBlue);
        renderColorComponentSlider(context, mouseX, mouseY, delta, colorSetting.getAlpha(), 0, 255, parentX() + 5, (parentY() + parentOffset() + offset) + 25 + parentHeight() * 3, colorSetting::setAlpha);
    }

    private void renderColorComponentSlider(DrawContext context, int mouseX, int mouseY, float delta, int value, int min, int max, int x, int y, java.util.function.Consumer<Integer> setter) {
        double sliderWidth = parentWidth() - 10;
        double valuePercentage = (double) (value - min) / (max - min);
        double filledWidth = sliderWidth * valuePercentage;

        if (currentColor1 == null)
            currentColor1 = new Color(0, 0, 0, 0);
        else currentColor1 = new Color(currentColor1.getRed(), currentColor1.getGreen(), currentColor1.getBlue(), currentColor1.getAlpha());

        if (currentColor2 == null)
            currentColor2 = new Color(0, 0, 0, 0);
        else currentColor2 = new Color(currentColor2.getRed(), currentColor2.getGreen(), currentColor2.getBlue(), currentColor2.getAlpha());

        int toAlpha = 170;

        currentColor1 = ColorUtils.smoothAlphaTransition(0.05F, toAlpha, currentColor1);
        currentColor2 = ColorUtils.smoothAlphaTransition(0.05F, toAlpha, currentColor2);

        context.fill(x, y, x + (int) sliderWidth, y + 2, currentColor1.getRGB());
        context.fill(x, y, x + (int) filledWidth, y + 2, Color.WHITE.getRGB());

        if (isHovered(mouseX, mouseY) && mouseX >= x && mouseX <= x + sliderWidth && mouseY >= y && mouseY <= y + 2 && GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS) {
            double newValue = MathHelper.clamp((mouseX - x) / sliderWidth * (max - min) + min, min, max);
            setter.accept((int) newValue);
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        // Handled in renderColorComponentSlider for continuous dragging
    }

    @Override
    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        // Handled in renderColorComponentSlider for continuous dragging
    }
}