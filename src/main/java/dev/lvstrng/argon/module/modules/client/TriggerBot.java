package dev.lvstrng.argon.module.modules.client;

import dev.lvstrng.argon.Argon;
import dev.lvstrng.argon.event.events.AttackListener;
import dev.lvstrng.argon.event.events.HudListener;
import dev.lvstrng.argon.event.events.TickListener;
import dev.lvstrng.argon.mixin.MinecraftClientAccessor;
import dev.lvstrng.argon.module.Category;
import dev.lvstrng.argon.module.Module;
import dev.lvstrng.argon.module.modules.client.Friends;
import dev.lvstrng.argon.module.setting.BooleanSetting;
import dev.lvstrng.argon.module.setting.MinMaxSetting;
import dev.lvstrng.argon.module.setting.NumberSetting;
import dev.lvstrng.argon.utils.*;
import dev.lvstrng.argon.utils.rotation.Rotation;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;
import java.util.Random;

import static dev.lvstrng.argon.Argon.mc;

public final class TriggerBot extends Module implements TickListener, HudListener {
	private final BooleanSetting inScreen = new BooleanSetting(EncryptedString.of("Work In Screen"), false);
	private final BooleanSetting allItems = new BooleanSetting(EncryptedString.of("All Items"), false);
	private final MinMaxSetting swordDelay = new MinMaxSetting(EncryptedString.of("Sword Delay"), 0, 100, 1, 40, 60);
	private final BooleanSetting checkShield = new BooleanSetting(EncryptedString.of("Check Shield"), false);
	private final BooleanSetting prioritizeCrits = new BooleanSetting(EncryptedString.of("Prioritize Crits"), false);
	
	private final BooleanSetting focusMode = new BooleanSetting(EncryptedString.of("Focus Mode"), false);
	private final BooleanSetting allEntities = new BooleanSetting(EncryptedString.of("All Entities"), false);
	private final BooleanSetting useShield = new BooleanSetting(EncryptedString.of("Use Shield"), false);
	private final NumberSetting shieldTime = new NumberSetting(EncryptedString.of("Shield Time"), 100, 1000, 350, 1);
	private final TimerUtils timer = new TimerUtils();

	private Entity getEntityNearCrosshair() {
		double smallestAngle = Double.MAX_VALUE;
		Entity bestEntity = null;

		for (Entity entity : mc.world.getEntities()) {
			if (entity instanceof LivingEntity && entity != mc.player && mc.player.distanceTo(entity) <= 6) {
				if (!entity.isAlive()) continue;

				Rotation requiredRotation = RotationUtils.getDirection(mc.player, entity.getEyePos());
				double angle = RotationUtils.getAngleToRotation(requiredRotation);

				if (angle < smallestAngle) {
					smallestAngle = angle;
					bestEntity = entity;
				}
			}
		}
		return bestEntity;
	}
	private final Random random = new Random();

	private double currentSwordDelay;
	private Entity focusedEntity = null;
    private String errorMessage = null;
    private double displayDelay = 0;

	public TriggerBot() {
		super(EncryptedString.of("Trigger Bot"),
				EncryptedString.of("Automatically hits players for you"),
				-1,
				Category.FLOW);
		inScreen.setDescription(EncryptedString.of("Will trigger even if youre inside a screen"));
		allItems.setDescription(EncryptedString.of("Works with all Items /THIS USES SWORD DELAY AS THE DELAY/"));
		swordDelay.setDescription(EncryptedString.of("Delay for swords"));
		checkShield.setDescription(EncryptedString.of("Checks if the player is blocking your hits with a shield (Recommended with Shield Disabler)"));
		prioritizeCrits.setDescription(EncryptedString.of("Allows normal hits, but will time and prioritize criticals when airborne."));
		
		focusMode.setDescription(EncryptedString.of("Only attacks the last entity hit."));
		allEntities.setDescription(EncryptedString.of("Will attack all entities"));
		useShield.setDescription(EncryptedString.of("Uses shield if it's in your offhand"));
		addSettings(inScreen, allItems, swordDelay, checkShield, focusMode, prioritizeCrits, allEntities, useShield, shieldTime);
	}

	@Override
	public void onEnable() {
		// --- CORRECCIÓN APLICADA AQUÍ ---
		int minInt = (int) swordDelay.getMinValue();
		int maxInt = (int) swordDelay.getMaxValue();
		double randomDecimal = minInt + (random.nextDouble() * (maxInt - minInt));
		currentSwordDelay = randomDecimal * 10;

		eventManager.add(TickListener.class, this);
		//eventManager.add(AttackListener.class, this);
        eventManager.add(HudListener.class, this);
		super.onEnable();
	}

	@Override
	public void onDisable() {
		focusedEntity = null;
		eventManager.remove(TickListener.class, this);
		//eventManager.remove(AttackListener.class, this);
        eventManager.remove(HudListener.class, this);
		super.onDisable();
	}

	private boolean canCrit() {
		if (mc.player == null) return false;
		return mc.player.fallDistance > 0.0F && !mc.player.isOnGround() && !mc.player.isClimbing() && !mc.player.isSubmergedInWater() && mc.player.getVehicle() == null;
	}

	@SuppressWarnings("all")
	@Override
	public void onTick() {
		try {
			this.errorMessage = null; // Reset error message

			// Initial checks
			if (!inScreen.getValue() && mc.currentScreen != null) return;
			if (Argon.INSTANCE.getModuleManager().getModule(Friends.class).antiAttack.getValue() && Argon.INSTANCE.getFriendManager().isAimingOverFriend()) return;
			if (focusedEntity != null && (!focusedEntity.isAlive() || mc.player.distanceTo(focusedEntity) > 8)) {
				focusedEntity = null;
			}
			Item item = mc.player.getMainHandStack().getItem();
			if (((mc.player.getOffHandStack().getItem().getComponents().contains(DataComponentTypes.FOOD) || mc.player.getOffHandStack().getItem() instanceof ShieldItem) && GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS)) return;
			if (prioritizeCrits.getValue() && !mc.player.isOnGround() && !canCrit()) return;
			if (mc.player.isOnGround() && !mc.player.isSprinting()) return;

			// Target finding logic
			Entity targetEntity = null;
			boolean isDirectHit = false;

			if (mc.crosshairTarget instanceof EntityHitResult hit && hit.getType() == HitResult.Type.ENTITY) {
				targetEntity = hit.getEntity();
				isDirectHit = true;
			} else {
				targetEntity = getEntityNearCrosshair();
				isDirectHit = false;
			}

			if (targetEntity == null) return;

            // --- NUEVA LÓGICA DE LÍNEA DE VISIÓN ---
            // Asegurarse de que hay una línea de visión clara al objetivo
            if (!mc.player.canSee(targetEntity)) { // canSee es un método de LivingEntity
                return; // No golpear si no hay línea de visión
            }
            // --- FIN NUEVA LÓGICA ---

            float distance = mc.player.distanceTo(targetEntity);

			// Item and entity checks
			boolean isWeapon = item instanceof SwordItem || item instanceof AxeItem;
			if (!allItems.getValue() && !isWeapon) return;
			if (focusMode.getValue() && focusedEntity != null && targetEntity != focusedEntity) return;
			if (!(targetEntity instanceof PlayerEntity || allEntities.getValue())) return;
			if (targetEntity instanceof PlayerEntity player) {
				if (checkShield.getValue() && player.isBlocking() && !WorldUtils.isShieldFacingAway(player)) return;
			}

			// Main action logic
			if (isDirectHit) {
				// Logic for a direct, real hit
				if (timer.delay((long) currentSwordDelay)) {
					if (useShield.getValue() && mc.player.getOffHandStack().getItem() == Items.SHIELD && mc.player.isBlocking()) {
						MouseSimulation.mouseRelease(GLFW.GLFW_MOUSE_BUTTON_RIGHT);
					}

					// Asegurarse de que la entidad sigue viva antes de golpear
					if (!targetEntity.isAlive()) return;

					WorldUtils.hitEntity(targetEntity, true);
					if (focusMode.getValue() && this.focusedEntity == null) this.focusedEntity = targetEntity;

					// --- CORRECCIÓN APLICADA AQUÍ ---
					int minInt = (int) swordDelay.getMinValue();
					int maxInt = (int) swordDelay.getMaxValue();
					double randomDecimal = minInt + (random.nextDouble() * (maxInt - minInt));
					currentSwordDelay = randomDecimal * 10;

					this.displayDelay = currentSwordDelay;
					timer.reset();
				} else {
					if (useShield.getValue() && mc.player.getOffHandStack().getItem() == Items.SHIELD) {
						int useFor = shieldTime.getValueInt();
						MouseSimulation.mouseClick(GLFW.GLFW_MOUSE_BUTTON_RIGHT, useFor);
					}
				}
			}
		} catch (Exception e) {
			this.errorMessage = e.getMessage();
		}
	}

	/*
	@Override
	public void onAttack(AttackEvent event) {
		if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) != GLFW.GLFW_PRESS)
			event.cancel();
	}
	*/

    @Override
    public void onRenderHud(HudEvent event) {
        // Content removed as requested
    }
}