package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.common.mobeffect.SimpleMobEffect;
import com.petrolpark.common.mobeffect.SyncedMobEffect;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class PetrolparkMobEffects {
    
    public static final RegistryEntry<MobEffect, SyncedMobEffect> NUMBNESS = REGISTRATE.simple("numbness", Registries.MOB_EFFECT, NonNullSupplier.of(() -> new SyncedMobEffect(MobEffectCategory.HARMFUL, 0x7A2337)));

    public static final RegistryEntry<MobEffect, MobEffect> MINERS_LUCK = REGISTRATE.simple("miners_luck", Registries.MOB_EFFECT, NonNullSupplier.of(() -> 
        new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 0x4E0B60)
            .addAttributeModifier(PetrolparkAttributes.ORE_DISCOVERY_CHANCE.getDelegate(), Petrolpark.asResource("effect.miners_luck"), 0.1f, AttributeModifier.Operation.ADD_VALUE)
    ));

    public static final void register() {};
};
