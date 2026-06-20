package com.petrolpark.core.world.item.decay;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.Petrolpark;

import io.netty.buffer.ByteBuf;
import net.minecraft.Util;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record DecayTime(String translationKey, long lifetime) {

    protected static final String DEFAULT_TRANSLATION_KEY = Util.makeDescriptionId("item", Petrolpark.asResource("decaying_item.remaining"));

    public static final DecayTime NONE = new DecayTime(DEFAULT_TRANSLATION_KEY, 0);

    public static final Codec<DecayTime> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("translation_key", DEFAULT_TRANSLATION_KEY).forGetter(DecayTime::translationKey),
        Codec.LONG.fieldOf("time").forGetter(DecayTime::lifetime)
    ).apply(instance, DecayTime::new));

    public static final Codec<DecayTime> INLINE_CODEC = Codec.LONG.xmap(DecayTime::new, DecayTime::lifetime);

    public static final StreamCodec<ByteBuf, DecayTime> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, DecayTime::translationKey,
        ByteBufCodecs.VAR_LONG, DecayTime::lifetime,
        DecayTime::new
    );

    public DecayTime(long decayTime) {
        this(DEFAULT_TRANSLATION_KEY, decayTime);
    };
    
};
