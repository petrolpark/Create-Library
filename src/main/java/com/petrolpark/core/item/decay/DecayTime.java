package com.petrolpark.core.item.decay;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record DecayTime(String translationKey, long lifetime) {

    protected static final String DEFAULT_TRANSLATION_KEY = "item.petrolpark.decaying_item.remaining";

    public static final DecayTime NONE = new DecayTime(DEFAULT_TRANSLATION_KEY, 0);

    public static final Codec<DecayTime> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("translationKey", DEFAULT_TRANSLATION_KEY).forGetter(DecayTime::translationKey),
        Codec.LONG.fieldOf("time").forGetter(DecayTime::lifetime)
    ).apply(instance, DecayTime::new));

    public static final StreamCodec<ByteBuf, DecayTime> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, DecayTime::translationKey,
        ByteBufCodecs.VAR_LONG, DecayTime::lifetime,
        DecayTime::new
    );
    
};
