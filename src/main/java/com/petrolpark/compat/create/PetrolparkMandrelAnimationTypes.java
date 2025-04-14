package com.petrolpark.compat.create;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.mojang.serialization.MapCodec;
import com.petrolpark.compat.create.common.processing.mandrel.animation.CoilMandrelAnimation;
import com.petrolpark.compat.create.common.processing.mandrel.animation.IMandrelAnimation;
import com.petrolpark.compat.create.common.processing.mandrel.animation.MandrelAnimationType;
import com.petrolpark.compat.create.common.processing.mandrel.animation.PipeMandrelAnimation;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class PetrolparkMandrelAnimationTypes {

    public static final RegistryEntry<MandrelAnimationType, MandrelAnimationType>

    COIL = register("coil", CoilMandrelAnimation.CODEC, CoilMandrelAnimation.STREAM_CODEC),
    PIPE = register("pipe", PipeMandrelAnimation.CODEC, PipeMandrelAnimation.STREAM_CODEC);
    
    private static final RegistryEntry<MandrelAnimationType, MandrelAnimationType> register(String name, MapCodec<? extends IMandrelAnimation> codec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends IMandrelAnimation> streamCodec) {
        return REGISTRATE.simple(name, CreateRegistries.Keys.MANDREL_ANIMATION_TYPE, () -> new MandrelAnimationType(codec, streamCodec));
    };

    public static final void register() {};
};
