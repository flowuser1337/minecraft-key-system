package dev.lvstrng.argon.font;

import dev.lvstrng.argon.utils.EncryptedString;

public final class Fonts {
	public static GlyphPageFontRenderer QUICKSAND = GlyphPageFontRenderer.create(EncryptedString.of("Arial").toString(), 40, true, false, false);
}
