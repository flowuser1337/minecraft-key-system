package dev.lvstrng.argon.module.modules.client;

import dev.lvstrng.argon.event.events.TickListener;
import dev.lvstrng.argon.module.Category;
import dev.lvstrng.argon.module.Module;
import dev.lvstrng.argon.utils.EncryptedString;

import static dev.lvstrng.argon.Argon.mc;

public class Sprint extends Module implements TickListener {

    public Sprint() {
        super(EncryptedString.of("Sprint"), EncryptedString.of("Automatically sprints for you."), -1, Category.FLOW);
    }

    @Override
    public void onEnable() {
        eventManager.add(TickListener.class, this);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        eventManager.remove(TickListener.class, this);
        super.onDisable();
    }

    @Override
    public void onTick() {
        if (mc.player != null && mc.player.forwardSpeed > 0 && !mc.player.isSprinting()) {
            mc.player.setSprinting(true);
        }
    }
}