package com.petrolpark.common.item.shulkerbelt;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkItems;
import com.simibubi.create.content.equipment.armor.BacktankArmorLayer;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ShulkerBeltLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    public static final ModelResourceLocation SHULKER_BELT = ModelResourceLocation.standalone(Petrolpark.asResource("item/shulker_belt_on_body"));

    public static final List<Predicate<LivingEntity>> WEARING_PREDICATES = new ArrayList<>();

    static {
        WEARING_PREDICATES.add(player -> PetrolparkItems.SHULKER_BELT.isIn(player.getItemBySlot(EquipmentSlot.LEGS)));
    };

    protected final ItemRenderer itemRenderer;
    protected final ItemStack beltStackInstance;

    public ShulkerBeltLayer(RenderLayerParent<T, M> renderer, ItemRenderer itemRenderer) {
        super(renderer);
        this.itemRenderer = itemRenderer;
        beltStackInstance = PetrolparkItems.SHULKER_BELT.asStack();
    };

    @Override
    public void render(@Nonnull PoseStack ms, @Nonnull MultiBufferSource bufferSource, int packedLight, @Nonnull T livingEntity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (WEARING_PREDICATES.stream().noneMatch(p -> p.test(livingEntity))) return;

        M entityModel = getParentModel();
        if (!(entityModel instanceof HumanoidModel<?> model)) return;

        ms.pushPose(); {
            model.body.translateAndRotate(ms);
            TransformStack.of(ms)
                .rotateZDegrees(180f)
                .scale(9 / 7f)
                .translate(4.5f / 16f, 0.5f / 16f, 6 / 16f);
            itemRenderer.render(beltStackInstance, ItemDisplayContext.NONE, false, ms, bufferSource, packedLight, OverlayTexture.NO_OVERLAY, itemRenderer.getItemModelShaper().getModelManager().getModel(SHULKER_BELT));
        }; ms.popPose();

    };

    /**
     * Copied from {@link BacktankArmorLayer#registerOn Create source code}.
     * @param entityRenderer
     * @param itemRenderer
     */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static void registerOn(EntityRenderer<?> entityRenderer, ItemRenderer itemRenderer) {
		if (!(entityRenderer instanceof LivingEntityRenderer<?, ?> livingRenderer)) return;
		if (!(livingRenderer.getModel() instanceof HumanoidModel)) return;
		ShulkerBeltLayer<?, ?> layer = new ShulkerBeltLayer<>(livingRenderer, itemRenderer);
		livingRenderer.addLayer((ShulkerBeltLayer) layer);
	};

    @SubscribeEvent
    public static final void onRegisterLayerDefintions(EntityRenderersEvent.AddLayers event) {
        ItemRenderer itemRenderer = event.getContext().getItemRenderer();
        EntityRenderDispatcher renderManager = event.getContext().getEntityRenderDispatcher();
        for (EntityRenderer<? extends Player> renderer : renderManager.getSkinMap().values()) registerOn(renderer, itemRenderer);
		for (EntityRenderer<?> renderer : renderManager.renderers.values()) registerOn(renderer, itemRenderer);
    };

    @SubscribeEvent
    public static final void onRegisterAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(SHULKER_BELT);
    };

    
};
