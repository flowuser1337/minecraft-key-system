package dev.lvstrng.argon.module.setting;

public final class KeybindSetting extends Setting<Integer> {
	private boolean listening;
	private final boolean moduleKey;
	private final int originalKey;

	public KeybindSetting(CharSequence name, int key, boolean moduleKey) {
		super(name, key);
		this.originalKey = key;
		this.moduleKey = moduleKey;
	}

	public boolean isModuleKey() {
		return moduleKey;
	}

	public boolean isListening() {
		return listening;
	}

	public int getOriginalKey() {
		return originalKey;
	}

	public void setListening(boolean listening) {
		this.listening = listening;
	}

	public int getKey() {
		return getValue();
	}

	public void setKey(int key) {
		setValue(key);
	}

    public void toggleListening() {
		this.listening = !listening;
	}
}
