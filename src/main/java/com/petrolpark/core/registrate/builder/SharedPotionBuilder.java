package com.petrolpark.core.registrate.builder;

import org.apache.commons.lang3.function.TriConsumer;

import com.petrolpark.PetrolparkRegistrate;
import com.petrolpark.compat.SharedFeatureFlag;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing.Builder;;

public class SharedPotionBuilder<P> extends PotionBuilder<P> {

    public static final <P> SharedPotionBuilder<P> create(PetrolparkRegistrate owner, P parent, SharedFeatureFlag featureFlag, String name, String potionName, BuilderCallback callback) {
        return new SharedPotionBuilder<P>(owner, parent, featureFlag, name, potionName, callback)
            .asOptional();
    };

    protected final SharedFeatureFlag featureFlag;

    protected SharedPotionBuilder(PetrolparkRegistrate owner, P parent, SharedFeatureFlag featureFlag, String name, String potionName, BuilderCallback callback) {
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
    public SharedPotionBuilder<SharedPotionBuilder<P>> potion(String name, String potionName) {
        return (SharedPotionBuilder<SharedPotionBuilder<P>>)petrolparkOwner.sharedEntry(featureFlag, name, callback -> create(petrolparkOwner, this, featureFlag, name, potionName, callback));
    };
    
};
