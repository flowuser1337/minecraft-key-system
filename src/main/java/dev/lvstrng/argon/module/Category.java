package dev.lvstrng.argon.module;

import dev.lvstrng.argon.utils.EncryptedString;

public enum Category {
	        FLOW(EncryptedString.of("Flow"));
    
	public final CharSequence name;

	Category(CharSequence name) {
		this.name = name;
	}
}
