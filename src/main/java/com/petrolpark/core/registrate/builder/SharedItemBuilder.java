package com.petrolpark.core.registrate.builder;

import javax.annotation.Nonnull;

import com.petrolpark.AbstractPetrolparkRegistrate;
import com.petrolpark.compat.SharedFeatureFlag;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.ItemCapability;

public class SharedItemBuilder<T extends Item, P> extends PetrolparkItemBuilder<T, P> {

    protected final SharedFeatureFlag featureFlag;

    public SharedItemBuilder(AbstractPetrolparkRegistrate<?> owner, P parent, @Nonnull SharedFeatureFlag featureFlag, String name, BuilderCallback callback, NonNullFunction<Properties, T> factory) {
        super(owner, parent, name, callback, factory);
        this.featureFlag = featureFlag;
    }

    @Override
    public <CAP, CTX> SharedItemBuilder<T, P> capability(ItemCapability<CAP, CTX> capability, NonNullFunction<T, ICapabilityProvider<ItemStack, CTX, CAP>> provider) {
        if (featureFlag.enabled()) super.capability(capability, provider);
        return this;
    };
    
    @Override
    public ItemBuilder<T, P> onRegister(@Nonnull NonNullConsumer<? super T> callback) {
        if (featureFlag == null || !featureFlag.enabled()) return this;
        return super.onRegister(callback);
    };
    
};
