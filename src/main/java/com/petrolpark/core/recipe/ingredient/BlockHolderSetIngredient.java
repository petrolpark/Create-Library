package com.petrolpark.core.recipe.ingredient;

import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.mojang.serialization.MapCodec;
import com.petrolpark.util.CodecHelper;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

public record BlockHolderSetIngredient(HolderSet<Block> blocks) implements ICustomIngredient {

    public static final MapCodec<BlockHolderSetIngredient> CODEC = CodecHelper.singleFieldMap(RegistryCodecs.homogeneousList(Registries.BLOCK), "blocks", BlockHolderSetIngredient::blocks, BlockHolderSetIngredient::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, BlockHolderSetIngredient> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.holderSet(Registries.BLOCK), BlockHolderSetIngredient::blocks, BlockHolderSetIngredient::new);

    @Override
    public boolean test(@Nonnull ItemStack stack) {
        return blocks.stream().map(Holder::value).map(Block::asItem).anyMatch(stack::is);
    };

    @Override
    public Stream<ItemStack> getItems() {
        return blocks.stream().map(Holder::value).map(Block::asItem).map(ItemStack::new);
    };

    @Override
    public boolean isSimple() {
        return true;
    };

    @Override
    public IngredientType<?> getType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getType'");
    };
    
};
