package com.petrolpark.core.registrate;

import javax.annotation.Nonnull;

import com.petrolpark.PetrolparkRegistrate;
import com.petrolpark.compat.SharedFeatureBlockItem;
import com.petrolpark.compat.SharedFeatureFlag;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class SharedBlockBuilder<T extends Block, P extends PetrolparkRegistrate> extends PetrolparkBlockBuilder<T, P> {

    public static <T extends Block, P extends PetrolparkRegistrate> BlockBuilder<T, P> create(PetrolparkRegistrate owner, P parent, SharedFeatureFlag feature, String name, BuilderCallback callback, NonNullFunction<BlockBehaviour.Properties, T> factory) {
        return new SharedBlockBuilder<>(owner, parent, feature, name, callback, factory, () -> BlockBehaviour.Properties.of()).defaultLoot();
    };

    private final PetrolparkRegistrate petrolparkOwner;
    public final SharedFeatureFlag featureFlag;

    public SharedBlockBuilder(PetrolparkRegistrate owner, P parent, SharedFeatureFlag featureFlag, String name, BuilderCallback callback, NonNullFunction<Properties, T> factory, NonNullSupplier<Properties> initialProperties) {
        super(owner, parent, name, callback, factory, initialProperties);
        this.petrolparkOwner = owner;
        this.featureFlag = featureFlag;
    };

    @Override
    public ItemBuilder<BlockItem, BlockBuilder<T, P>> item() {
        return item(SharedFeatureBlockItem.of(featureFlag));
    };

    /**
     * Copied from {@link BlockBuilder#item()}
     */
    @Override
    public <I extends Item> ItemBuilder<I, BlockBuilder<T, P>> item(@Nonnull NonNullBiFunction<? super T, net.minecraft.world.item.Item.Properties, ? extends I> factory) {
        if (featureFlag.enabled()) return petrolparkOwner.<I, BlockBuilder<T, P>>sharedItem(this, featureFlag, getName(), p -> factory.apply(getEntry(), p))
            // .setData(ProviderType.LANG, NonNullBiConsumer.noop())
            // .model((ctx, prov) -> 
            //     getOwner().getDataProvider(ProviderType.BLOCKSTATE)
            //         .flatMap(p -> p.getExistingVariantBuilder(getEntry()))
            //         .map(b -> b.getModels().get(b.partialState()))
            //         .map(BlockStateProvider.ConfiguredModelList::toJSON)
            //         .filter(JsonElement::isJsonObject)
            //         .map(j -> j.getAsJsonObject().get("model"))
            //         .map(JsonElement::getAsString)
            //         .map(model -> prov.withExistingParent(ctx.getName(), model))
            //         .orElse(prov.blockItem(asSupplier()))
            // )
            ;
        else return ItemBuilder.create(DummyRegistrate.INSTANCE, this, getName(), petrolparkOwner.new SharedFeatureBuilderCallback(featureFlag), p -> factory.apply(getEntry(), p));
    };

    @Override
    public BlockBuilder<T, P> onRegister(@Nonnull NonNullConsumer<? super T> callback) {
        if (!featureFlag.enabled()) return this;
        return super.onRegister(callback);
    };
    
    
};
