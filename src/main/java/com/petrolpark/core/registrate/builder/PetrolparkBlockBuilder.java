package com.petrolpark.core.registrate.builder;

import java.util.Optional;

import javax.annotation.Nonnull;

import com.google.gson.JsonElement;
import com.petrolpark.core.registrate.AbstractPetrolparkRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;

/**
 * {@link BlockBuilder} without any default datagen
 */
public class PetrolparkBlockBuilder<T extends Block, P> extends BlockBuilder<T, P> {

    public static <T extends Block, P> PetrolparkBlockBuilder<T, P> create(AbstractPetrolparkRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullFunction<BlockBehaviour.Properties, T> factory) {
        return new PetrolparkBlockBuilder<>(owner, parent, name, callback, factory, () -> BlockBehaviour.Properties.of());
    }

    public PetrolparkBlockBuilder(AbstractPetrolparkRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullFunction<BlockBehaviour.Properties, T> factory, NonNullSupplier<BlockBehaviour.Properties> initialProperties) {
        super(owner, parent, name, callback, factory, initialProperties);
    };

    @Override
    public <I extends Item> ItemBuilder<I, BlockBuilder<T, P>> item(@Nonnull NonNullBiFunction<? super T, Properties, ? extends I> factory) {
        return getOwner().<I, BlockBuilder<T, P>> item(this, getName(), p -> factory.apply(getEntry(), p));
    };

    public static final <T extends Block, P> ItemBuilder<BlockItem, BlockBuilder<T, P>> defaultBlockItem(BlockBuilder<T, P> builder) {
        return builder
            .item(BlockItem::new)
            .model((ctx, prov) -> {
                final Optional<String> model = builder.getOwner().getDataProvider(ProviderType.BLOCKSTATE)
                    .flatMap(p -> p.getExistingVariantBuilder(builder.getEntry()))
                    .map(b -> b.getModels().get(b.partialState()))
                    .map(BlockStateProvider.ConfiguredModelList::toJSON)
                    .filter(JsonElement::isJsonObject)
                    .map(j -> j.getAsJsonObject().get("model"))
                    .map(JsonElement::getAsString);
                if (model.isPresent()) {
                    prov.withExistingParent(ctx.getName(), model.get());
                } else {
                    prov.blockItem(builder.asSupplier());
                }
            });
    };
    
};
