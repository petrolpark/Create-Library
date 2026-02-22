package com.petrolpark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.petrolpark.PetrolparkMobEffects;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.extensions.ILivingEntityExtension;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ILivingEntityExtension {

    @Shadow
    private DamageSource lastDamageSource;

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
        throw new AssertionError();
    };

    /**
     * When Mobs are attack they swing their limbs. Cancel this if they are numb.
     */
    @WrapWithCondition(
        method = "hurt",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/WalkAnimationState;setSpeed(F)V"
        )
    )
    protected boolean petrolpark$numbnessCancelsLimbSwing(WalkAnimationState walkAnimation, float speed) {
        return !self().hasEffect(PetrolparkMobEffects.NUMBNESS.getDelegate());
    };

    /**
     * {@code lastHurtByMob} is used to target attackers. As Mobs should not respond to Damage if they have the Numbness Effect, we do not want to record this.
     * This has the side effect of not giving the correct loot/credit when slain.
     */
    @WrapMethod(
        method = "setLastHurtByMob"
    )
    public void petrolpark$numbnessForgetsAttacker(LivingEntity livingEntity, Operation<Void> original) {
        if (!self().hasEffect(PetrolparkMobEffects.NUMBNESS.getDelegate()) || livingEntity == null) original.call(livingEntity);
    };

    /**
     * Mobs should not make a hurt sound if they are numb.
     */
    @WrapMethod(
        method = "playHurtSound"
    )
    protected void petrolpark$numbnessCancelsHurtSound(DamageSource source, Operation<Void> original) {
        if (!self().hasEffect(PetrolparkMobEffects.NUMBNESS.getDelegate())) original.call(source);
    };

    /**
     * {@code lastDamageSource} is used for the {@link PanicGoal}, for example, so it should be discarded if the Mob is numb.
     */
    @WrapMethod(
        method = "getLastDamageSource"
    )
    protected DamageSource petrolpark$numbnessForgetsDamageSource(Operation<DamageSource> original) {
        if (self().hasEffect(PetrolparkMobEffects.NUMBNESS.getDelegate())) lastDamageSource = null;
        return original.call();
    };

    /**
     * When Mobs are attack they swing their limbs (on the client side). Cancel this if they are numb.
     * @param speed
     */
    @WrapWithCondition(
        method = "handleDamageEvent",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/WalkAnimationState;setSpeed(F)V"
        )
    )
    protected boolean petrolpark$numbnessCancelsLimbSwingClient(WalkAnimationState walkAnimation, float speed) {
        return !self().hasEffect(PetrolparkMobEffects.NUMBNESS.getDelegate());
    };

    /**
     * When Mobs are attacked, on the client side they make the hurt sound, but not with {@code playHurtSound}. Cancel this if they are numb.
     */
    @WrapWithCondition(
        method = "handleDamageEvent",
        at = @At(
            value = "INVOKE",
            target = "playSound"
        )
    )
    protected boolean petrolpark$numbnessCancelsHurtSound(LivingEntity livingEntity, SoundEvent soundEvent, float pitch, float volume) {
        return !self().hasEffect(PetrolparkMobEffects.NUMBNESS.getDelegate());
    };
};
