package dev.lvstrng.argon.utils;

import dev.lvstrng.argon.font.Fonts;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import static dev.lvstrng.argon.Argon.mc;


public final class TextRenderer {

	public static void drawString(CharSequence string, DrawContext context, int x, int y, int color) {
		Fonts.QUICKSAND.drawString(context.getMatrices(), string, x, y - 8, color);
	}

	public static int getWidth(CharSequence string) {
		return Fonts.QUICKSAND.getStringWidth(string);
	}

	public static void drawCenteredString(CharSequence string, DrawContext context, int x, int y, int color) {
		Fonts.QUICKSAND.drawString(context.getMatrices(), string, (x - (Fonts.QUICKSAND.getStringWidth(string) / 2)), y - 8, color);
	}

	public static void drawLargeString(CharSequence string, DrawContext context, int x, int y, int color) {
		MatrixStack matrices = context.getMatrices();
		matrices.push();

		matrices.scale(1.4f, 1.4f, 1.4f);
		Fonts.QUICKSAND.drawString(context.getMatrices(), string, x, y - 8, color);
		matrices.scale(1, 1, 1);

		matrices.pop();
	}

	public static void drawStringBold(CharSequence string, DrawContext context, int x, int y, int color) {
		Fonts.QUICKSAND.drawString(context.getMatrices(), string, x, y - 8, color);
		Fonts.QUICKSAND.drawString(context.getMatrices(), string, x + 1, y - 8, color);
		Fonts.QUICKSAND.drawString(context.getMatrices(), string, x, y - 7, color);
		Fonts.QUICKSAND.drawString(context.getMatrices(), string, x + 1, y - 7, color);
	}
}
