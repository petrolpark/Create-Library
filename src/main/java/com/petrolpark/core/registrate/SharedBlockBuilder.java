package com.petrolpark.core.registrate;

import javax.annotation.Nonnull;

import com.petrolpark.PetrolparkRegistrate;
import com.petrolpark.compat.SharedFeatureBlockItem;
import com.petrolpark.compat.SharedFeatures;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class SharedBlockBuilder<T extends Block, P extends PetrolparkRegistrate> extends BlockBuilder<T, P> {

    public static <T extends Block, P extends PetrolparkRegistrate> SharedBlockBuilder<T, P> create(PetrolparkRegistrate owner, P parent, SharedFeatures feature, String name, BuilderCallback callback, NonNullFunction<BlockBehaviour.Properties, T> factory) {
        return new SharedBlockBuilder<>(owner, parent, feature, name, callback, factory, () -> BlockBehaviour.Properties.of());
    };

    private final PetrolparkRegistrate petrolparkOwner;
    public final SharedFeatures feature;

    public SharedBlockBuilder(PetrolparkRegistrate owner, P parent, SharedFeatures feature, String name, BuilderCallback callback, NonNullFunction<Properties, T> factory, NonNullSupplier<Properties> initialProperties) {
        super(owner, parent, name, callback, factory, initialProperties);
        this.petrolparkOwner = owner;
        this.feature = feature;
    };

    @Override
    public ItemBuilder<BlockItem, BlockBuilder<T, P>> item() {
        return item(SharedFeatureBlockItem.of(feature));
    };

    @Override
    public <I extends Item> ItemBuilder<I, BlockBuilder<T, P>> item(@Nonnull NonNullBiFunction<? super T, net.minecraft.world.item.Item.Properties, ? extends I> factory) {
        if (feature.enabled()) return petrolparkOwner.sharedItem(this, feature, getName(), p -> factory.apply(getEntry(), p));
        return ItemBuilder.create(DummyRegistrate.INSTANCE, this, getName(), petrolparkOwner.new SharedFeatureBuilderCallback(feature), p -> factory.apply(getEntry(), p));
    };

    @Override
    public BlockEntry<T> register() {
        return super.register();
    };
    
};
