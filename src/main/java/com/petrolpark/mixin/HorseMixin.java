package com.petrolpark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.compat.create.shared.content.kinetics.horseMill.HorseMillProperties;
import com.petrolpark.registry.PetrolparkAttributes;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.level.Level;

@Mixin(Horse.class)
public abstract class HorseMixin extends AbstractHorse {
    
    protected HorseMixin(EntityType<? extends AbstractHorse> entityType, Level level) {
        super(entityType, level);
        throw new AssertionError();
    };

    @Inject(
        method = "randomizeAttributes",
        at = @At("HEAD")
    )
    public void petrolpark$randomizeHorseMillCapacityAttribute(RandomSource random, CallbackInfo ci) {
        final AttributeInstance stressCapacity = getAttribute(PetrolparkAttributes.HORSE_MILL_STRESS_CAPACITY);
        if (stressCapacity != null) stressCapacity.setBaseValue(HorseMillProperties.generateHorseMillStressCapacityAttribute(random::nextDouble));
    };
};
