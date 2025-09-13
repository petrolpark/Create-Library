package com.petrolpark.core.registrate;

import javax.annotation.Nonnull;

import com.petrolpark.PetrolparkRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * {@link BlockBuilder} without any default datagen
 */
public class PetrolparkBlockBuilder<T extends Block, P> extends BlockBuilder<T, P> {

    public static <T extends Block, P> PetrolparkBlockBuilder<T, P> create(PetrolparkRegistrate owner, P parent, String name, BuilderCallback callback, NonNullFunction<BlockBehaviour.Properties, T> factory) {
        return new PetrolparkBlockBuilder<>(owner, parent, name, callback, factory, () -> BlockBehaviour.Properties.of());
    }

    public PetrolparkBlockBuilder(PetrolparkRegistrate owner, P parent, String name, BuilderCallback callback, NonNullFunction<BlockBehaviour.Properties, T> factory, NonNullSupplier<BlockBehaviour.Properties> initialProperties) {
        super(owner, parent, name, callback, factory, initialProperties);
    };

    @Override
    public <I extends Item> ItemBuilder<I, BlockBuilder<T, P>> item(@Nonnull NonNullBiFunction<? super T, Properties, ? extends I> factory) {
        return getOwner().<I, BlockBuilder<T, P>> item(this, getName(), p -> factory.apply(getEntry(), p));
    };
    
};
