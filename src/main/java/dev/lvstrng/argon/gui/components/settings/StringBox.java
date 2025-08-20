package dev.lvstrng.argon.gui.components.settings;

import dev.lvstrng.argon.Argon;
import dev.lvstrng.argon.gui.components.ModuleButton;
import dev.lvstrng.argon.module.setting.Setting;
import dev.lvstrng.argon.module.setting.StringSetting;
import dev.lvstrng.argon.utils.ColorUtils;
import dev.lvstrng.argon.utils.EncryptedString;
import dev.lvstrng.argon.utils.RenderUtils;
import dev.lvstrng.argon.utils.TextRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public final class StringBox extends RenderableSetting {
    private final StringSetting setting;
    private Color currentAlpha;

    public StringBox(ModuleButton parent, Setting<?> setting, int offset) {
        super(parent, setting, offset);
        this.setting = (StringSetting) setting;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // New colors from the design
        Color textColor = new Color(255, 255, 255); // #FFFFFF

        String value = setting.getValue();
        String truncatedValue = value.length() <= 15 ? value : (value.substring(0, 15) + "...");
        String displayText = setting.getName() + EncryptedString.of(": ").toString() + truncatedValue;

        TextRenderer.drawStringBold(displayText, context, parentX() + 5, (parentY() + parentOffset() + offset) + 9, textColor.getRGB());

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
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY) && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            // Create a new, simplified screen for editing the string
            mc.setScreen(new Screen(Text.empty()) {
                private String content = setting.getValue();
                private int tick = 0;

                @Override
                public void render(DrawContext context, int mouseX, int mouseY, float delta) {
                    // Inherit the main GUI's background blur
                    if (Argon.INSTANCE.clickGui != null) {
                        Argon.INSTANCE.clickGui.render(context, 0, 0, delta);
                    }

                    tick++;

                    // New colors for the editing screen
                    Color bgColor = new Color(17, 17, 17, 230); // #111111
                    Color borderColor = new Color(34, 34, 34, 230); // #222222
                    Color textColor = new Color(255, 255, 255); // #FFFFFF

                    int screenMidX = mc.getWindow().getWidth() / 2;
                    int screenMidY = mc.getWindow().getHeight() / 2;
                    int width = 400;
                    int height = 50;

                    int startX = screenMidX - (width / 2);
                    int startY = screenMidY - (height / 2);

                    // Draw background and border
                    RenderUtils.renderRoundedQuad(context.getMatrices(), borderColor, startX - 1, startY - 1, startX + width + 1, startY + height + 1, 6, 10);
                    RenderUtils.renderRoundedQuad(context.getMatrices(), bgColor, startX, startY, startX + width, startY + height, 5, 10);

                    // Draw text and a blinking cursor
                    String textToRender = content;
                    if (tick % 60 < 30) { // Blink every half second
                        textToRender += "_";
                    }

                    TextRenderer.drawCenteredString(textToRender, context, screenMidX, startY + (height / 2) - 4, textColor.getRGB());
                }

                @Override
                public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                    if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                        setting.setValue(content.strip());
                        mc.setScreen(Argon.INSTANCE.clickGui);
                        return true;
                    }

                    if (isPaste(keyCode)) {
                        String clipboard = mc.keyboard.getClipboard();
                        if (clipboard != null) content += clipboard;
                        return true;
                    }

                    if (isCopy(keyCode)) {
                        GLFW.glfwSetClipboardString(mc.getWindow().getHandle(), content);
                        return true;
                    }

                    if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                        if (!content.isEmpty()) {
                            content = content.substring(0, content.length() - 1);
                        }
                        return true;
                    }
                    return super.keyPressed(keyCode, scanCode, modifiers);
                }

                @Override
                public boolean charTyped(char chr, int modifiers) {
                    content += chr;
                    return true;
                }

                @Override
                public boolean shouldCloseOnEsc() {
                    return false;
                }
            });
        }
        super.mouseClicked(mouseX, mouseY, button);
    }
}
