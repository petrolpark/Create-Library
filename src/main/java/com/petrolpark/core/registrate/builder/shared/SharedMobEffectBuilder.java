package com.petrolpark.core.registrate.builder.shared;

import com.petrolpark.core.registrate.AbstractPetrolparkRegistrate;
import com.petrolpark.core.registrate.builder.MobEffectBuilder;
import com.petrolpark.core.registrate.builder.PotionBuilder;
import com.petrolpark.shared.SharedFeatureFlag;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;

import net.minecraft.world.effect.MobEffect;

public class SharedMobEffectBuilder<T extends MobEffect, P> extends MobEffectBuilder<T, P> {

    public static final <T extends MobEffect, P> SharedMobEffectBuilder<T, P> create(AbstractPetrolparkRegistrate<?> owner, P parent, SharedFeatureFlag featureFlag, String name, BuilderCallback callback, MobEffectBuilder.Factory<T> factory) {
        return new SharedMobEffectBuilder<T,P>(owner, parent, featureFlag, name, callback, factory)
            .asOptional();
    };

    protected final SharedFeatureFlag featureFlag;

    protected SharedMobEffectBuilder(AbstractPetrolparkRegistrate<?> owner, P parent, SharedFeatureFlag featureFlag, String name, BuilderCallback callback, MobEffectBuilder.Factory<T> factory) {
        super(owner, parent, name, callback, factory);
        this.featureFlag = featureFlag;
    };

    @Override
    public SharedMobEffectBuilder<T, P> asOptional() {
        super.asOptional();
        return this;
    };

    @Override
    public PotionBuilder<SharedMobEffectBuilder<T, P>> potion(String potionName, NonNullUnaryOperator<MobEffectInstanceBuilder> builderTransformer) {
        return petrolparkOwner.sharedEntry(featureFlag, getName(), callback -> SharedPotionBuilder.create(petrolparkOwner, this, featureFlag, potionName, getName(), callback))
            .effect(builderTransformer.apply(new MobEffectBuilder.MobEffectInstanceBuilder(() -> get().getDelegate())));
    };
    
};
