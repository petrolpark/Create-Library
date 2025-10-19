package com.petrolpark.core.registrate;

import com.petrolpark.PetrolparkRegistrate;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.nullness.NonNullFunction;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;

/**
 * {@link ItemBuilder} without any default datagen
 */
public class PetrolparkItemBuilder<T extends Item, P> extends ItemBuilder<T, P> {

    public static <T extends Item, P> PetrolparkItemBuilder<T, P> create(PetrolparkRegistrate owner, P parent, String name, BuilderCallback callback, NonNullFunction<Item.Properties, T> factory) {
        return new PetrolparkItemBuilder<>(owner, parent, name, callback, factory);
    };

    public PetrolparkItemBuilder(PetrolparkRegistrate owner, P parent, String name, BuilderCallback callback, NonNullFunction<Properties, T> factory) {
        super(owner, parent, name, callback, factory);
    };
    
};
