package dev.lvstrng.argon.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityJumpAccessor {
    @Accessor("jumpingCooldown")
    void setJumpingCooldown(int jumpingCooldown);
}
