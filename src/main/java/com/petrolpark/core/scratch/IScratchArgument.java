package com.petrolpark.core.scratch;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkRegistries;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface IScratchArgument<CONTEXT extends IScratchContext, TYPE> {

    /**
     * Use {@link #CODEC} instead.
     */
    static Codec<IScratchArgument<?, ?>> TYPED_CODEC = PetrolparkRegistries.SCRATCH_ARGUMENT_TYPES.byNameCodec().dispatch(IScratchArgument::getType, IScratchArgument.Type::codec);

    public static Codec<IScratchArgument<?, ?>> CODEC = Codec.lazyInitialized(() -> TYPED_CODEC);
    
    public TYPE get(CONTEXT context);

    public IScratchArgument.Type<?> getType();

    public record Type<ARGUMENT extends IScratchArgument<?, ?>>(MapCodec<ARGUMENT> codec, StreamCodec<? super RegistryFriendlyByteBuf, ARGUMENT> streamCodec) {};
};
