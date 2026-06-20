package com.petrolpark.core.item.decay.product;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkDecayProductTypes;
import com.petrolpark.core.flags.Flag;
import com.petrolpark.core.flags.FlagsLootItemFunction;
import com.petrolpark.core.flags.ItemFlagPole;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record FlagDecayProduct(Holder<Flag> flagHolder, FlagsLootItemFunction.Action action) implements IDecayProduct {
    
    public static final MapCodec<FlagDecayProduct> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Flag.CODEC.fieldOf("flag").forGetter(FlagDecayProduct::flagHolder),
        FlagsLootItemFunction.Action.CODEC.optionalFieldOf("action", FlagsLootItemFunction.Action.ADD).forGetter(FlagDecayProduct::action)
    ).apply(instance, FlagDecayProduct::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FlagDecayProduct> STREAM_CODEC = StreamCodec.composite(
        Flag.STREAM_CODEC, FlagDecayProduct::flagHolder, 
        FlagsLootItemFunction.Action.STREAM_CODEC, FlagDecayProduct::action,
        FlagDecayProduct::new
    );

    @Override
    public ItemStack get(ItemStack stack) {
        action().apply(ItemFlagPole.get(stack), flagHolder);
        return stack;
    };

    @Override
    public DecayProductType getType() {
        return PetrolparkDecayProductTypes.FLAG.get();
    };

    @Override
    public final boolean equals(Object other) {
        if (this == other) return true;
        return (other instanceof FlagDecayProduct cdp && cdp.action() == action() && cdp.flagHolder().equals(flagHolder()));
    };
};
