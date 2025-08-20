package dev.lvstrng.argon.module.setting;

import java.util.Arrays;
import java.util.List;

public final class ModeSetting<T extends Enum<T>> extends Setting<T> {
	private final List<T> possibleValues;
	private final T originalValue;

	public ModeSetting(CharSequence name, T defaultValue, Class<T> type) {
		super(name, defaultValue);
		T[] values = type.getEnumConstants();
		this.possibleValues = Arrays.asList(values);
		this.originalValue = defaultValue;
	}

	public List<T> getPossibleValues() {
		return possibleValues;
	}

	public T getMode() {
		return getValue();
	}

	public void setMode(T mode) {
		setValue(mode);
	}

	public void setModeIndex(int mode) {
		setValue(possibleValues.get(mode));
	}

	public int getModeIndex() {
		return possibleValues.indexOf(getValue());
	}

	public T getOriginalValue() {
		return originalValue;
	}

	public void cycle() {
        int index = getModeIndex();
		if (index < possibleValues.size() - 1)
			index++;
		else index = 0;
        setModeIndex(index);
	}

	public boolean isMode(T mode) {
		return getValue() == mode;
	}

	@Override
	public ModeSetting<T> setDescription(CharSequence desc) {
		super.setDescription(desc);
		return this;
	}
}
