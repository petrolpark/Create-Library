package com.petrolpark.compat.curios.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class BadgeCurioRenderer implements ICurioRenderer {

    private final HumanoidModel<LivingEntity> model;

    public BadgeCurioRenderer(ModelPart part) {
		this.model = new HumanoidModel<>(part);
	}

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack ms, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        LivingEntity entity = slotContext.entity();
        model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
        ICurioRenderer.followBodyRotations(entity, model);

        // Translate and rotate with our body
		ms.pushPose();
		ms.translate(model.body.x / 16.0, model.body.y / 16.0, model.body.z / 16.0);
		ms.mulPose(Axis.YP.rotation(model.body.yRot));
		ms.mulPose(Axis.XP.rotation(model.body.xRot));

		// Translate and scale to our body
		ms.translate(-2 / 16d, 2.5 / 16d, -2.25 / 16d);
		if (!entity.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) ms.translate(0d, 0d, -1 / 16d);
		ms.mulPose(Axis.ZP.rotationDegrees(180.0f));
		ms.scale(0.3f, 0.3f, 0.3f);

		// Render
		Minecraft mc = Minecraft.getInstance();
		mc.getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, ms, renderTypeBuffer, mc.level, 0);
		ms.popPose();
    };

    public static MeshDefinition mesh() {
		CubeListBuilder builder = new CubeListBuilder();
		MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0);
		mesh.getRoot().addOrReplaceChild("body", builder, PartPose.ZERO);
		return mesh;
	}
    
};
