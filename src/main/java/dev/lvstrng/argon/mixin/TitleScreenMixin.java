package dev.lvstrng.argon.mixin;

import dev.lvstrng.argon.Argon;
import dev.lvstrng.argon.gui.AuthScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void onInit(CallbackInfo ci) {
        if (Argon.INSTANCE.getAuthManager().checkHWID()) {
            // HWID is already authorized, so we don't need to show the auth screen.
            return;
        }
        MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new AuthScreen(Argon.INSTANCE.getAuthManager())));
        ci.cancel();
    }
}
