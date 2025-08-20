package dev.lvstrng.argon.module.setting;

import dev.lvstrng.argon.utils.EncryptedString;

import java.awt.*;

public class ColorSetting extends Setting<Color> {
    public ColorSetting(EncryptedString name, Color defaultValue) {
        super(name, defaultValue);
    }

    public ColorSetting(EncryptedString name, Color defaultValue, EncryptedString description) {
        super(name, defaultValue, description);
    }

    public int getRed() {
        return getValue().getRed();
    }

    public int getGreen() {
        return getValue().getGreen();
    }

    public int getBlue() {
        return getValue().getBlue();
    }

    public int getAlpha() {
        return getValue().getAlpha();
    }

    public void setRed(int red) {
        setValue(new Color(red, getGreen(), getBlue(), getAlpha()));
    }

    public void setGreen(int green) {
        setValue(new Color(getRed(), green, getBlue(), getAlpha()));
    }

    public void setBlue(int blue) {
        setValue(new Color(getRed(), getGreen(), blue, getAlpha()));
    }

    public void setAlpha(int alpha) {
        setValue(new Color(getRed(), getGreen(), getBlue(), alpha));
    }

    public int getRGB() {
        return getValue().getRGB();
    }
}
