package com.petrolpark.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.compat.create.shared.registry.SharedCreateCriterionTriggers;
import com.simibubi.create.content.equipment.potatoCannon.PotatoProjectileEntity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

@Mixin(PotatoProjectileEntity.class)
public abstract class PotatoProjectileEntityMixin extends AbstractHurtingProjectile {

    protected PotatoProjectileEntityMixin(EntityType<? extends AbstractHurtingProjectile> entityType, double x, double y, double z, Level level) {
        super(entityType, x, y, z, level);
        throw new AssertionError();
    };

    @Shadow
    protected ItemStack stack;
    
    @Inject(
        method = "Lcom/simibubi/create/content/equipment/potatoCannon/PotatoProjectileEntity;onHitEntity(Lnet/minecraft/world/phys/EntityHitResult;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;getRemainingFireTicks()I"
        )
    )
    protected void petrolpark$triggerAdvancement(EntityHitResult ray, CallbackInfo ci) {
        if (getOwner() instanceof ServerPlayer serverPlayer) SharedCreateCriterionTriggers.POTATO_CANNON_HIT.get().trigger(serverPlayer, stack, ray.getEntity());
    };
};
