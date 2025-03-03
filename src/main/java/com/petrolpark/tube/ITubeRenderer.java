package com.petrolpark.tube;

//import dev.engine_room.flywheel.core.PartialModel;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.petrolpark.util.MathsHelper;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
//import com.simibubi.create.foundation.render.CachedBufferer;
import net.createmod.catnip.render.CachedBuffers;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;

public interface ITubeRenderer<T extends SmartBlockEntity> {

    default void renderTube(T be, PoseStack ms, MultiBufferSource bufferSource, int light) {
        TubeBehaviour tube = be.getBehaviour(TubeBehaviour.TYPE);
        if (tube == null || !tube.isController()) return;
        TubeSpline spline = tube.getSpline();
        VertexConsumer vc = bufferSource.getBuffer(RenderType.solid());
        for (int i = 0; i < spline.getPoints().size() - 1; i++) {
            CachedBuffers.partial(getTubeSegmentModel(be), be.getBlockState())
                .translateBack(Vec3.atLowerCornerOf(be.getBlockPos()))
                .translate(spline.getPoints().get(i))
                .rotateY((float) MathsHelper.azimuth(spline.getTangents().get(i)))
                .rotateX((float) MathsHelper.inclination(spline.getTangents().get(i)))
                .light(light)
                .renderInto(ms, vc);
        };
    };

    public PartialModel getTubeSegmentModel(T be);
    
};
