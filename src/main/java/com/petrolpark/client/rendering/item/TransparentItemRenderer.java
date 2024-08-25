package com.petrolpark.client.rendering.item;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.ForgeHooksClient;

public class TransparentItemRenderer {

    public static void transformAndRenderModel(BakedModel model, ItemDisplayContext transformType, int color, int light, int overlay, PoseStack poseStack, MultiBufferSource buffer) {
        boolean leftHand = transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
        poseStack.pushPose();
        poseStack.translate(-0.5f, -0.5f, -0.5f);
        renderModelLists(ForgeHooksClient.handleCameraTransforms(poseStack, model, transformType, leftHand), color, light, overlay, poseStack, buffer.getBuffer(Sheets.translucentCullBlockSheet()));
        poseStack.popPose();
    };

    @SuppressWarnings("deprecation")
    public static void renderModelLists(BakedModel model, int color, int combinedLight, int combinedOverlay, PoseStack poseStack, VertexConsumer buffer) {
        RandomSource randomsource = RandomSource.create();

        for(Direction direction : Direction.values()) {
            randomsource.setSeed(42l);
            renderQuadList(poseStack, buffer, model.getQuads(null, direction, randomsource), color, combinedLight, combinedOverlay);
        };

        randomsource.setSeed(42l);
        renderQuadList(poseStack, buffer, model.getQuads(null, null, randomsource), color, combinedLight, combinedOverlay);
    };
  
    public static void renderQuadList(PoseStack poseStack, VertexConsumer buffer, List<BakedQuad> quads, int color, int combinedLight, int combinedOverlay) {
        PoseStack.Pose pose = poseStack.last();

        for(BakedQuad quad : quads) {
            if (!quad.isTinted()) return;
            buffer.putBulkData(pose, quad, (float)(color >> 16 & 255) / 255f, (float)(color >> 8 & 255) / 255f, (float)(color & 255) / 255f, (float)(color >> 24 & 255) / 255f, combinedLight, combinedOverlay, true);
        };

    };
};
