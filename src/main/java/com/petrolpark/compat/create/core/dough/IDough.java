package com.petrolpark.compat.create.core.dough;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;
import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.create.CreateRegistries;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

@ApiStatus.Experimental
@RequiresCreate
public interface IDough<DOUGH extends IDough<DOUGH>> {

    /**
     * Use {@link IDough#CODEC} instead.
     */
    static final Codec<IDough<?>> TYPED_CODEC = CreateRegistries.DOUGH_TYPES.byNameCodec()
        .dispatch(IDough::getType, IDoughType::codec);

    public static final Codec<IDough<?>> CODEC = Codec.lazyInitialized(() -> TYPED_CODEC);

    static final StreamCodec<RegistryFriendlyByteBuf, IDough<?>> STREAM_CODEC = ByteBufCodecs.registry(CreateRegistries.Keys.DOUGH_TYPE)
        .dispatch(IDough::getType, IDoughType::streamCodec);
    
    public boolean canBeCut();

    //TODO texture

    public IDoughType<DOUGH> getType();
};
