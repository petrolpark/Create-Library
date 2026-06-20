package com.petrolpark.core.client.rendering;

import javax.annotation.Nonnull;

import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.world.level.BlockAndTintGetter;

public class EmptySuperByteBuffer implements SuperByteBuffer {

    public static final EmptySuperByteBuffer EMPTY = new EmptySuperByteBuffer();

    private EmptySuperByteBuffer() {};

    @Override
    public EmptySuperByteBuffer pushPose() {
        return this;
    };

    @Override
    public EmptySuperByteBuffer popPose() {
        return this;
    };

    @Override
    public EmptySuperByteBuffer mulPose(Matrix4fc pose) {
        return this;
    };

    @Override
    public EmptySuperByteBuffer mulNormal(Matrix3fc normal) {
        return this;
    };

    @Override
    public EmptySuperByteBuffer translate(float x, float y, float z) {
        return this;
    };

    @Override
    public EmptySuperByteBuffer rotate(Quaternionfc quaternion) {
        return this;
    };

    @Override
    public EmptySuperByteBuffer scale(float factorX, float factorY, float factorZ) {
        return this;
    };

    @Override
    public void renderInto(@Nonnull PoseStack ms, @Nonnull VertexConsumer consumer) {
        //NOOP
    };

    @Override
    public boolean isEmpty() {
        return true;
    };

    @Override
    public PoseStack getTransforms() {
        return new PoseStack();
    };

    @Override
    @SuppressWarnings("unchecked")
    public EmptySuperByteBuffer reset() {
        return this;
    };

    @Override
    @SuppressWarnings("unchecked")
    public EmptySuperByteBuffer color(int color) {
        return this;
    };

    @Override
    @SuppressWarnings("unchecked")
    public EmptySuperByteBuffer color(int r, int g, int b, int a) {
        return this;
    };

    @Override
    @SuppressWarnings("unchecked")
    public EmptySuperByteBuffer disableDiffuse() {
        return this;
    };

    @Override
    @SuppressWarnings("unchecked")
    public EmptySuperByteBuffer shiftUV(@Nonnull SpriteShiftEntry entry) {
        return this;
    };

    @Override
    @SuppressWarnings("unchecked")
    public EmptySuperByteBuffer shiftUVScrolling(@Nonnull SpriteShiftEntry entry, float scrollU, float scrollV) {
        return this;
    };

    @Override
    @SuppressWarnings("unchecked")
    public EmptySuperByteBuffer shiftUVtoSheet(@Nonnull SpriteShiftEntry entry, float uTarget, float vTarget, int sheetSize) {
        return this;
    };

    @Override
    @SuppressWarnings("unchecked")
    public EmptySuperByteBuffer overlay(int overlay) {
        return this;
    };

    @Override
    @SuppressWarnings("unchecked")
    public EmptySuperByteBuffer light(int packedLight) {
        return this;
    };

    @Override
    @SuppressWarnings("unchecked")
    public EmptySuperByteBuffer useLevelLight(@Nonnull BlockAndTintGetter level) {
        return this;
    };

    @Override
    @SuppressWarnings("unchecked")
    public EmptySuperByteBuffer useLevelLight(@Nonnull BlockAndTintGetter level, @Nonnull Matrix4f lightTransform) {
        return this;
    };
    
};
