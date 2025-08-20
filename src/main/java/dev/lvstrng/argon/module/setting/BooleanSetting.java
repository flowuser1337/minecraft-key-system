package dev.lvstrng.argon.module.setting;

public final class BooleanSetting extends Setting<Boolean> {
	private final boolean originalValue;

	public BooleanSetting(CharSequence name, boolean value) {
		super(name, value);
		this.originalValue = value;
	}

	public void toggle() {
		setValue(!getValue());
	}

	public boolean getOriginalValue() {
		return originalValue;
	}

	@Override
	public BooleanSetting setDescription(CharSequence desc) {
		super.setDescription(desc);
		return this;
	}
}
