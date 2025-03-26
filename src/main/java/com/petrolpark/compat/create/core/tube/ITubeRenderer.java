package com.petrolpark.compat.create.core.tube;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.petrolpark.util.MathsHelper;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import net.createmod.catnip.animation.AnimationTickHolder;
//import com.simibubi.create.foundation.render.CachedBufferer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;

public interface ITubeRenderer<T extends SmartBlockEntity> {

    default void renderTube(T be, PoseStack ms, MultiBufferSource bufferSource, int light) {
        final float partialTicks = AnimationTickHolder.getPartialTicks();
        final TubeBehaviour tube = be.getBehaviour(TubeBehaviour.TYPE);
        if (tube == null || !tube.isController()) return;
        final PartialModel[] segmentModels = getTubeSegmentModels(be);
        final TubeSpline spline = tube.getSpline();
        final VertexConsumer vc = bufferSource.getBuffer(RenderType.solid());
        final float[] segmentScales = getSegmentScales(spline.getPoints().size(), partialTicks);
        if (segmentScales != null && segmentScales.length != spline.getPoints().size()) throw new IllegalStateException("Segment size array must have length equal to number of segments");
        for (int i = 0; i < spline.getPoints().size() - 1; i++) {
            final SuperByteBuffer buffer = CachedBuffers.partial(segmentModels[i % segmentModels.length], be.getBlockState())
                .translateBack(Vec3.atLowerCornerOf(be.getBlockPos()))
                .translate(spline.getPoints().get(i));
            if (segmentScales != null) {
                final float scale = segmentScales[i];
                buffer.scale(scale, 1f, scale);
            };
            buffer.rotateY((float) MathsHelper.azimuth(spline.getTangents().get(i)))
                .rotateX((float) MathsHelper.inclination(spline.getTangents().get(i)))
                .light(light)
                .renderInto(ms, vc);
        };
    };

    /**
     * Partial Models of the segments of this tube. The model for each segment will cycle through this array.
     * @param be
     * @return Non-empty array of Partial Models
     */
    public PartialModel[] getTubeSegmentModels(T be);

    /**
     * Get the sizes of the segments for this frame.
     * @param segments The total number of segments.
     * @return An array {@code segments} long, or {@code null} if all segments should have the base size.
     */
    public default float[] getSegmentScales(int segments, float partialTicks) {
        return null;
    };
    
};
