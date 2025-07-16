package com.petrolpark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkMobEffects;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.extensions.ILivingEntityExtension;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ILivingEntityExtension {

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
        throw new AssertionError();
    };

    @Inject(
        method = "setLastHurtByMob",
        at = @At("HEAD"),
        cancellable = true
    )
    public void inSetLastHurtByMob(LivingEntity livingEntity, CallbackInfo ci) {
        if (self().hasEffect(PetrolparkMobEffects.NUMBNESS.getDelegate()) && livingEntity != null) ci.cancel();
    };

    @Inject(
        method = "playHurtSound",
        at = @At("HEAD"),
        cancellable = true
    )
    protected void inPlayHurtSound(DamageSource source, CallbackInfo ci) {
        if (self().hasEffect(PetrolparkMobEffects.NUMBNESS.getDelegate())) {
            ci.cancel();
            Petrolpark.LOGGER.info("hiiii");
        };
    };
};
