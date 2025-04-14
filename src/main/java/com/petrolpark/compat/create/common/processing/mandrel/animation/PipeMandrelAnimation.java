package com.petrolpark.compat.create.common.processing.mandrel.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.petrolpark.compat.create.PetrolparkMandrelAnimationTypes;
import com.petrolpark.compat.create.common.processing.mandrel.MandrelBlockEntity;
import com.petrolpark.util.CodecHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record PipeMandrelAnimation(ResourceLocation modelKey) implements IMandrelAnimation {
    
    public static final MapCodec<PipeMandrelAnimation> CODEC = CodecHelper.singleFieldMap(ResourceLocation.CODEC, "model", PipeMandrelAnimation::modelKey, PipeMandrelAnimation::new);
    public static final StreamCodec<ByteBuf, PipeMandrelAnimation> STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, PipeMandrelAnimation::modelKey, PipeMandrelAnimation::new);

    @Override
    public float getTotalAngleSubtended(MandrelBlockEntity be) {
        return 360f;
    };

    @Override
    public void animate(MandrelBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay, float shaftAngle, float recipeProgress) {
        // TODO Auto-generated method stub
        
    };

    @Override
    public MandrelAnimationType getType() {
        return PetrolparkMandrelAnimationTypes.PIPE.get();
    };
};
