package com.petrolpark.core.registrate.builder.shared;

import org.apache.commons.lang3.function.TriConsumer;

import com.petrolpark.core.registrate.AbstractPetrolparkRegistrate;
import com.petrolpark.core.registrate.builder.PotionBuilder;
import com.petrolpark.shared.SharedFeatureFlag;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing.Builder;;

public class SharedPotionBuilder<P> extends PotionBuilder<P> {

    public static final <P> SharedPotionBuilder<P> create(AbstractPetrolparkRegistrate<?> owner, P parent, SharedFeatureFlag featureFlag, String name, String potionName, BuilderCallback callback) {
        return new SharedPotionBuilder<P>(owner, parent, featureFlag, name, potionName, callback)
            .asOptional();
    };

    protected final SharedFeatureFlag featureFlag;

    protected SharedPotionBuilder(AbstractPetrolparkRegistrate<?> owner, P parent, SharedFeatureFlag featureFlag, String name, String potionName, BuilderCallback callback) {
        super(owner, parent, name, potionName, callback);
        this.featureFlag = featureFlag;
    };

    @Override
    public SharedPotionBuilder<P> asOptional() {
        super.asOptional();
        return this;
    };

    @Override
    public SharedPotionBuilder<P> recipe(TriConsumer<RegistryAccess, Builder, RegistryEntry<Potion, Potion>> consumer) {
        if (featureFlag.enabled()) super.recipe(consumer);
        return this;
    };

    @Override
    public PotionBuilder<PotionBuilder<P>> potion(String name, String potionName) {
        return petrolparkOwner.sharedEntry(featureFlag, name, callback -> create(petrolparkOwner, this, featureFlag, name, potionName, callback));
    };
    
};
