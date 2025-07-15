package com.petrolpark.core.registrate;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import com.petrolpark.compat.SharedFeatureFlag;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;

public class SharedItemBuilder<T extends Item, P> extends ItemBuilder<T, P> {

    protected final SharedFeatureFlag featureFlag;

    private final Set<NonNullConsumer<? super T>> postConstructionRegistrationCallbacks = new HashSet<>(); // Registration callbacks accumulated during the constructor. Workaround for Registrate calling onRegister in the constructor with no way to avoid this

    public SharedItemBuilder(AbstractRegistrate<?> owner, P parent, @Nonnull SharedFeatureFlag featureFlag, String name, BuilderCallback callback, NonNullFunction<Properties, T> factory) {
        super(owner, parent, name, callback, factory);
        this.featureFlag = featureFlag;
        postConstructionRegistrationCallbacks.forEach(this::onRegister);
    };

    @Override
    public ItemBuilder<T, P> onRegister(@Nonnull NonNullConsumer<? super T> callback) {
        if (featureFlag == null) postConstructionRegistrationCallbacks.add(callback);
        else if (!featureFlag.enabled()) return this;
        return super.onRegister(callback);
    };
    
};
