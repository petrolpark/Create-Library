package com.petrolpark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.mobeffects.PetrolparkMobEffects;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        throw new AssertionError();
    };

    @Inject(
        method = "Lnet/minecraft/world/entity/LivingEntity;setLastHurtByMob(Lnet/minecraft/world/entity/LivingEntity;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void inSetLastHurtByMob(LivingEntity livingEntity, CallbackInfo ci) {
        if (livingEntity != null && livingEntity.hasEffect(PetrolparkMobEffects.NUMBNESS.get())) ci.cancel();
    };

    @Inject(
        method = "Lnet/minecraft/world/entity/LivingEntity;playHurtSound(Lnet/minecraft/world/damagesource/DamageSource;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    protected void inPlayHurtSound(DamageSource source, CallbackInfo ci) {
        if(source.getEntity() instanceof LivingEntity && ((LivingEntity)source.getEntity()).hasEffect(PetrolparkMobEffects.NUMBNESS.get())) ci.cancel();
    };
};
