package petrolpark.mc.library.compat.create.shared.content.processing.mandrel;

import com.mojang.blaze3d.vertex.PoseStack;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.compat.create.shared.registry.SharedPartialModels;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class MandrelRenderer extends KineticBlockEntityRenderer<MandrelBlockEntity> {

    public MandrelRenderer(Context context) {
        super(context);
    };

    @Override
    protected void renderSafe(MandrelBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        //TODO Flywheel visual
        BlockState state = getRenderedBlockState(be);
		renderRotatingBuffer(be, getRotatedModel(be, state), ms, buffer.getBuffer(getRenderType(be, state)), light);
        renderAnimation(be, partialTicks, ms, buffer, light, overlay);
    };

    @SuppressWarnings("null")
    public void renderAnimation(MandrelBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (!be.running) return;
        final Axis axis = ((IRotate) be.getBlockState().getBlock()).getRotationAxis(be.getBlockState());
        final float angleRemainingAfterStart = be.getSpeed() < 0f ? -getStartAngle(be, axis) : Mth.TWO_PI - getStartAngle(be, axis);
        final float partialTicksTil0 = angleRemainingAfterStart / ((be.getSpeed() * 3f / 10) * Mth.DEG_TO_RAD);
        final float partialTicksRunning = AnimationTickHolder.getRenderTime(be.getLevel()) - (be.animationStartPartialTick + partialTicksTil0);
        if (partialTicksRunning < 0f) return;
        Petrolpark.LOGGER.info("0");
    };

    public float getStartAngle(MandrelBlockEntity be, Axis axis) {
        float time = be.animationStartPartialTick;
		float offset = getRotationOffsetForPosition(be, be.getBlockPos(), axis);
		float angle = ((time * be.getSpeed() * 3f / 10 + offset) % 360) * Mth.DEG_TO_RAD;
		return angle;
    };
    
    @Override
    protected SuperByteBuffer getRotatedModel(MandrelBlockEntity be, BlockState state) {
        return CachedBuffers.partialFacing(SharedPartialModels.MANDREL_SHAFT, state, state.getValue(MandrelBlock.HORIZONTAL_FACING).getOpposite());
    };
};
