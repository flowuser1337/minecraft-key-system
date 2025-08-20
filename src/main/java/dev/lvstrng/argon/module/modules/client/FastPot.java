package dev.lvstrng.argon.module.modules.client;

import dev.lvstrng.argon.event.events.TickListener;
import dev.lvstrng.argon.mixin.MinecraftClientAccessor;
import dev.lvstrng.argon.module.Category;
import dev.lvstrng.argon.module.Module;
import dev.lvstrng.argon.module.setting.MinMaxSetting;
import dev.lvstrng.argon.utils.EncryptedString;
import dev.lvstrng.argon.utils.TimerUtils;
import net.minecraft.item.SplashPotionItem;

import java.util.Random;

public class FastPot extends Module implements TickListener {
    public MinMaxSetting delay = new MinMaxSetting(EncryptedString.of("Delay"), 0, 10, 1, 0, 5);

    private final Random random = new Random();
    private final TimerUtils timer = new TimerUtils();
    
    // Nuevo: Variable de estado para saber si estábamos lanzando una poción.
    private boolean wasPotting = false;

    public FastPot() {
        super(EncryptedString.of("FastPot"), EncryptedString.of("Allows you to use splash potions faster."), -1, Category.FLOW);
        addSettings(delay);
    }

    @Override
    public void onEnable() {
        eventManager.add(TickListener.class, this);
        timer.reset();
        wasPotting = false; // Reiniciar estado al activar.
        super.onEnable();
    }

    @Override
    public void onDisable() {
        eventManager.remove(TickListener.class, this);
        // Se asegura de restaurar el cooldown por defecto de Minecraft (4 ticks) al desactivar.
        if (mc != null) {
            ((MinecraftClientAccessor) mc).setItemUseCooldown(4);
        }
        wasPotting = false; // Reiniciar estado al desactivar.
        super.onDisable();
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        boolean isHoldingSplashPot = mc.player.getMainHandStack().getItem() instanceof SplashPotionItem;
        boolean isUsingItem = mc.options.useKey.isPressed();

        // Si el jugador está activamente usando una poción splash...
        if (isHoldingSplashPot && isUsingItem) {
            // Y si el temporizador ha cumplido el delay...
            // (50L es 50ms, la duración de un tick de juego)
            if (timer.delay(delay.getRandomValueInt() * 50L)) {
                // ...entonces reducimos el cooldown.
                ((MinecraftClientAccessor) mc).setItemUseCooldown(random.nextInt(2)); // Cooldown de 0 o 1
                timer.reset();
            }
            // Marcamos que estamos en "modo poción".
            wasPotting = true;
        } 
        // Si en el tick anterior estábamos en "modo poción", pero ahora ya no...
        else if (wasPotting) {
            // ...restauramos inmediatamente el cooldown por defecto.
            ((MinecraftClientAccessor) mc).setItemUseCooldown(4);
            // Y salimos del "modo poción".
            wasPotting = false;
        }
    }
}