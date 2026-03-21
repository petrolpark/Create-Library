package com.petrolpark.core.registrate;

import java.util.function.Supplier;

import com.petrolpark.core.registrate.builder.MobEffectBuilder;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.registries.DeferredHolder;

public class MobEffectEntry<T extends MobEffect> extends RegistryEntry<MobEffect, T> {

    public MobEffectEntry(AbstractRegistrate<?> owner, DeferredHolder<MobEffect, T> key) {
        super(owner, key);
    };

    public MobEffectBuilder.MobEffectInstanceBuilder asInstance() {
        return new MobEffectBuilder.MobEffectInstanceBuilder(this::getDelegate);
    };

    public MobEffectInstance asInstance(int duration, int amplifier) {
        return new MobEffectInstance(getDelegate(), duration, amplifier);
    };

    public Supplier<MobEffectInstance> asInstanceSupplier(int duration, int amplifier) {
        return () -> new MobEffectInstance(getDelegate(), duration, amplifier);
    };
    
};
