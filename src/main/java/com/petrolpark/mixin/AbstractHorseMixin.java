package com.petrolpark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.petrolpark.compat.create.shared.content.kinetics.horseMill.HarnessEntity;
import com.petrolpark.compat.create.shared.content.kinetics.horseMill.HorseMillContraptionEntity;
import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.registry.PetrolparkAttributes;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin extends Animal {

    protected AbstractHorseMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        throw new AssertionError();
    };

    @Shadow
    abstract void setOffspringAttribute(AgeableMob parent, AbstractHorse child, Holder<Attribute> attribute, double min, double max);
    
    @Inject(
        method = "setOffspringAttributes",
        at = @At("HEAD")
    )
    public void petrolpark$inheritHorseMillStressCapacity(AgeableMob parent, AbstractHorse child, CallbackInfo ci) {
        if (
            getAttribute(PetrolparkAttributes.HORSE_MILL_STRESS_CAPACITY) != null && // Need to check as child classes may not have the Attribute, or the shared feature flag may be disabled
            parent.getAttribute(PetrolparkAttributes.HORSE_MILL_STRESS_CAPACITY) != null &&
            child.getAttribute(PetrolparkAttributes.HORSE_MILL_STRESS_CAPACITY) != null
        ) {
            setOffspringAttribute(parent, child, PetrolparkAttributes.HORSE_MILL_STRESS_CAPACITY, 256f, PetrolparkConfigs.common().createHorseMillStressCapacityAttributeMax.getF());
        };
    };

    @ModifyReturnValue(
        method = "canPerformRearing",
        at = @At("RETURN")
    )
    public boolean petrolpark$dontRearWhileHarnessed(boolean original) {
        return (isPassenger() && (getVehicle() instanceof HarnessEntity || getVehicle() instanceof HorseMillContraptionEntity)) ? false : original;
    };
};
