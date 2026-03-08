package com.petrolpark.core.registrate.builder;

import java.util.Collections;

import javax.annotation.Nonnull;

import com.petrolpark.PetrolparkRegistrate;
import com.petrolpark.compat.SharedFeatureBlockItem;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.core.data.condition.SharedFeatureEnabledCondition;
import com.petrolpark.core.registrate.DummyRegistrate;
import com.petrolpark.core.registrate.RegistrateConditionalLootTableProvider;
import com.petrolpark.core.registrate.RegistrateConditionalLootTableProvider.ConditionalLootType;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class SharedBlockBuilder<T extends Block, P extends PetrolparkRegistrate> extends PetrolparkBlockBuilder<T, P> {

    public static <T extends Block, P extends PetrolparkRegistrate> BlockBuilder<T, P> create(P owner, P parent, SharedFeatureFlag feature, String name, BuilderCallback callback, NonNullFunction<BlockBehaviour.Properties, T> factory) {
        return new SharedBlockBuilder<>(owner, parent, feature, name, callback, factory, () -> BlockBehaviour.Properties.of())
            .asOptional()
            .defaultLoot();
    };

    private final PetrolparkRegistrate petrolparkOwner;
    public final SharedFeatureFlag featureFlag;

    public SharedBlockBuilder(PetrolparkRegistrate owner, P parent, SharedFeatureFlag featureFlag, String name, BuilderCallback callback, NonNullFunction<Properties, T> factory, NonNullSupplier<Properties> initialProperties) {
        super(owner, parent, name, callback, factory, initialProperties);
        this.petrolparkOwner = owner;
        this.featureFlag = featureFlag;
    };

    @Override
    public SharedItemBuilder<BlockItem, BlockBuilder<T, P>> item() {
        return item(SharedFeatureBlockItem.of(featureFlag));
    };

    /**
     * Copied from {@link BlockBuilder#item()}
     */
    @Override
    public <I extends Item> SharedItemBuilder<I, BlockBuilder<T, P>> item(@Nonnull NonNullBiFunction<? super T, net.minecraft.world.item.Item.Properties, ? extends I> factory) {
        if (featureFlag.enabled()) return petrolparkOwner.<I, BlockBuilder<T, P>>sharedItem(this, featureFlag, getName(), p -> factory.apply(getEntry(), p));
        else return new SharedItemBuilder<>(DummyRegistrate.INSTANCE, this, featureFlag, getName(), petrolparkOwner.new SharedFeatureBuilderCallback(featureFlag), p -> factory.apply(getEntry(), p));
    };

    @Override
    public SharedBlockBuilder<T, P> onRegister(@Nonnull NonNullConsumer<? super T> callback) {
        if (featureFlag.enabled()) super.onRegister(callback);
        return this;
    };

    @Override
    public SharedBlockBuilder<T, P> loot(@Nonnull NonNullBiConsumer<RegistrateBlockLootTables, T> cons) {
        setData(RegistrateConditionalLootTableProvider.TYPE, (ctx, prov) -> prov.addLootAction(ConditionalLootType.BLOCK, tb -> {
            if (!ctx.getEntry().getLootTable().equals(BuiltInLootTables.EMPTY)) {
                cons.accept(tb.withConditions(Collections.singletonList(new SharedFeatureEnabledCondition(featureFlag))), ctx.getEntry());
            };
        }));
        return this;
    };
    
    
};
