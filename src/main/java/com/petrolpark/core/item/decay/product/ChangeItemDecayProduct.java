package com.petrolpark.core.item.decay.product;

import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkDecayProductTypes;
import com.petrolpark.core.flags.ItemFlagPole;
import com.petrolpark.util.CodecHelper;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public record ChangeItemDecayProduct(ItemStack stack) implements IDecayProduct {

    public static final MapCodec<ChangeItemDecayProduct> CODEC = CodecHelper.singleFieldMap(ItemStack.SINGLE_ITEM_CODEC, "item", ChangeItemDecayProduct::stack, ChangeItemDecayProduct::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, ChangeItemDecayProduct> STREAM_CODEC = StreamCodec.composite(
        ItemStack.STREAM_CODEC, ChangeItemDecayProduct::stack,
        ChangeItemDecayProduct::new
    );

    public static final ChangeItemDecayProduct of(ItemLike item) {
        return new ChangeItemDecayProduct(new ItemStack(item));
    };

    @Override
    public ItemStack get(ItemStack stack) {
        ItemStack product = this.stack.copyWithCount(stack.getCount());
        ItemFlagPole.perpetuateSingle(Stream.of(stack), Stream.of(product));
        return product;
    };

    @Override
    public DecayProductType getType() {
        return PetrolparkDecayProductTypes.CHANGE_ITEM.get();
    };

    @Override
    public final boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof ChangeItemDecayProduct cidp && ItemStack.isSameItemSameComponents(cidp.stack(), stack());
    };
    
};
