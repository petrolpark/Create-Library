package com.petrolpark.core.scratch.environment.variable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ScratchVariableIdentifier(IScratchScope scope, String name) {
    
    public static final Codec<ScratchVariableIdentifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        IScratchScope.CODEC.fieldOf("scope").forGetter(ScratchVariableIdentifier::scope),
        Codec.STRING.fieldOf("name").forGetter(ScratchVariableIdentifier::name)
    ).apply(instance, ScratchVariableIdentifier::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ScratchVariableIdentifier> STREAM_CODEC = StreamCodec.composite(
        IScratchScope.STREAM_CODEC, ScratchVariableIdentifier::scope,
        ByteBufCodecs.STRING_UTF8, ScratchVariableIdentifier::name,
        ScratchVariableIdentifier::new
    );
};
