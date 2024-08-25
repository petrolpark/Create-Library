package com.petrolpark.compat.curios.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.petrolpark.compat.curios.CuriosSetup.HeadwearCurioRenderInfo;
import com.simibubi.create.compat.curios.GogglesCurioRenderer;

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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

/**
 * Mostly copied from {@link GogglesCurioRenderer Create source code}. 
 */
@OnlyIn(Dist.CLIENT)
public class HeadwearCurioRenderer implements ICurioRenderer {

	private final HumanoidModel<LivingEntity> model;
    //private final HeadwearCurioRenderInfo info;

	public HeadwearCurioRenderer(ModelPart part, HeadwearCurioRenderInfo info) {
		this.model = new HumanoidModel<>(part);
        //this.info = info;
	};

	@Override
	public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack ms, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		// Prepare values for transformation
		model.setupAnim(slotContext.entity(), limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		model.prepareMobModel(slotContext.entity(), limbSwing, limbSwingAmount, partialTicks);
		ICurioRenderer.followHeadRotations(slotContext.entity(), model.head);

		// Translate and rotate with our head
		ms.pushPose();
		ms.translate(model.head.x / 16d, model.head.y / 16d, model.head.z / 16d);
		ms.mulPose(Axis.YP.rotation(model.head.yRot));
		ms.mulPose(Axis.XP.rotation(model.head.xRot));

		// Translate and scale to our head
		ms.translate(0d, -0.25d, 0d);
		ms.mulPose(Axis.ZP.rotationDegrees(180f));
		ms.scale(0.625f, 0.625f, 0.625f);

        // Render on top of armor if needed
		// if(!slotContext.entity().getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
		// 	ms.mulPose(Axis.ZP.rotationDegrees(180f));
		// 	ms.translate(0d, -0.25d, 0d);
		// };

		// Render
		Minecraft mc = Minecraft.getInstance();
		mc.getItemRenderer().renderStatic(stack, ItemDisplayContext.HEAD, light, OverlayTexture.NO_OVERLAY, ms, renderTypeBuffer, mc.level, 0);
		
        ms.popPose();
	};

	public static MeshDefinition mesh() {
		CubeListBuilder builder = new CubeListBuilder();
		MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0);
		mesh.getRoot().addOrReplaceChild("head", builder, PartPose.ZERO);
		return mesh;
	};
};
    
