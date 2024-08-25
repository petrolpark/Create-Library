package com.petrolpark.compat.curios.renderer;

import com.petrolpark.compat.curios.CuriosSetup;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.ItemEntry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@OnlyIn(Dist.CLIENT)
public class CuriosRenderers {

	public static void register() {
		Minecraft mc = Minecraft.getInstance();

		CuriosSetup.RENDERED_ON_HEAD.forEach((item, info) -> {
			CuriosRendererRegistry.register(item.asSupplier().get(), () -> new HeadwearCurioRenderer(mc.getEntityModels().bakeLayer(getLayer(item)), info));
		});

		CuriosSetup.BADGES.forEach(item -> {
			CuriosRendererRegistry.register(item.get(), () -> new BadgeCurioRenderer(mc.getEntityModels().bakeLayer(getBadgeLayer(item))));
		});
	};

	public static void onLayerRegister(final EntityRenderersEvent.RegisterLayerDefinitions event) {
		CuriosSetup.RENDERED_ON_HEAD.forEach((item, info) -> {
			event.registerLayerDefinition(getLayer(item), () -> LayerDefinition.create(HeadwearCurioRenderer.mesh(), 1, 1));
		});

		CuriosSetup.BADGES.forEach(item -> {
			event.registerLayerDefinition(getBadgeLayer(item), () -> LayerDefinition.create(BadgeCurioRenderer.mesh(), 1, 1));
		});
	};

	private static ModelLayerLocation getLayer(ItemBuilder<?, ?> item) {
		return new ModelLayerLocation(item.getRegistryKey().location(), "headwear");
	};

	private static ModelLayerLocation getBadgeLayer(ItemEntry<?> item) {
		return new ModelLayerLocation(item.getId(), "badge");
	};
};
