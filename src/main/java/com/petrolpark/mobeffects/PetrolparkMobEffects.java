package com.petrolpark.mobeffects;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class PetrolparkMobEffects {
    
    public static final RegistryEntry<MobEffect, SimpleMobEffect> NUMBNESS = REGISTRATE.simple("numbness", Registries.MOB_EFFECT, NonNullSupplier.of(() -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 0x7A2337)));

    public static final void register() {};
};
