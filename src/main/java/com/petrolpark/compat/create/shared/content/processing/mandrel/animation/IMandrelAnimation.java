package com.petrolpark.compat.create.shared.content.processing.mandrel.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.petrolpark.compat.create.registry.PetrolparkCreateRegistries;
import com.petrolpark.compat.create.shared.content.processing.mandrel.MandrelBlockEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface IMandrelAnimation {

    /**
     * Use {@link IMandrelAnimation#CODEC} instead.
     */
    static final Codec<IMandrelAnimation> TYPED_CODEC = PetrolparkCreateRegistries.MANDREL_ANIMATION_TYPES
        .byNameCodec()
        .dispatch(IMandrelAnimation::getType, MandrelAnimationType::codec);

    public static final Codec<IMandrelAnimation> CODEC = Codec.lazyInitialized(() -> TYPED_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, IMandrelAnimation> STREAM_CODEC = ByteBufCodecs.registry(PetrolparkCreateRegistries.Keys.MANDREL_ANIMATION_TYPE)
        .dispatch(IMandrelAnimation::getType, MandrelAnimationType::streamCodec);

    /**
     * The angle (in degrees) by which the Shaft must turn over the course of this whole animation.
     */
    public float getTotalAngleSubtended(MandrelBlockEntity be);

    @OnlyIn(Dist.CLIENT)
    public void animate(MandrelBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay, float shaftAngle, float recipeProgress);
    
    public MandrelAnimationType getType();
};
