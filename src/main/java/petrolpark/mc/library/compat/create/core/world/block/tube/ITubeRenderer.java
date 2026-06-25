package petrolpark.mc.library.compat.create.core.world.block.tube;

import java.util.Arrays;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
//import com.simibubi.create.foundation.render.CachedBufferer;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import petrolpark.mc.library.util.MathsHelper;

public interface ITubeRenderer<T extends SmartBlockEntity> {

    default void renderTube(T be, PoseStack ms, MultiBufferSource bufferSource, int light) {
        final float partialTicks = AnimationTickHolder.getPartialTicks();
        final TubeBehaviour tube = be.getBehaviour(TubeBehaviour.TYPE);
        if (tube == null || !tube.isController()) return;
        final PartialModel[] segmentModels = getTubeSegmentModels(be);
        final TubeSpline spline = tube.getSpline();
        if (spline == null) return;
        final VertexConsumer vc = bufferSource.getBuffer(RenderType.solid());
        final float[] segmentScales = new float[spline.getPoints().size()];
        Arrays.fill(segmentScales, 1f);
        modifySegmentScales(be, segmentScales, partialTicks);
        for (int i = 0; i < spline.getPoints().size() - 1; i++) {
            final float scale = segmentScales[i];
            final Vec3 tangent = spline.getTangents().get(i);
            CachedBuffers.partial(segmentModels[i % segmentModels.length], be.getBlockState())
                .translate(spline.getPoints().get(i).subtract(Vec3.atLowerCornerOf(be.getBlockPos())))
                .rotateY((float) MathsHelper.azimuth(tangent))
                .rotateX((float) MathsHelper.inclination(tangent))
                .scale(scale, 1f, scale)
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
     */
    public default void modifySegmentScales(T be, float[] segmentScales, float partialTicks) {

    };
    
};
