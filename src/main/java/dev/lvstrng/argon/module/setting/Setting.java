package dev.lvstrng.argon.module.setting;

public abstract class Setting<T> {
    private CharSequence name;
    public CharSequence description;
    private T value;

    public Setting(CharSequence name, T value) {
        this.name = name;
        this.value = value;
    }

    public Setting(CharSequence name, T value, CharSequence description) {
        this.name = name;
        this.value = value;
        this.description = description;
    }

    public void setName(CharSequence name) {
        this.name = name;
    }

    public CharSequence getName() {
        return name;
    }

    public CharSequence getDescription() {
        return description;
    }

    public Setting<T> setDescription(CharSequence desc) {
        this.description = desc;
        return this;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
