package com.petrolpark.event;

import com.petrolpark.core.contamination.Contaminant;
import com.petrolpark.core.contamination.IContamination;
import com.petrolpark.core.contamination.ItemContamination;
import com.petrolpark.core.item.decay.ItemDecay;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {

        // Decay Times
        ItemDecay.getTooltip(event.getItemStack()).ifPresent(event.getToolTip()::add);

        // Item Contamination
        if (event.getEntity() == null) return; // Don't populate the Intrinsics map before the world has been loaded, as the Tags have not been loaded
        IContamination<?, ?> contamination = ItemContamination.get(event.getItemStack());
        contamination.streamShownContaminants().map(Contaminant::getNameColored).forEach(event.getToolTip()::add);
        contamination.streamShownAbsentContaminants().map(Contaminant::getAbsentNameColored).forEach(event.getToolTip()::add);
    };

    public static boolean isGameActive() {
        Minecraft mc = Minecraft.getInstance();
		return !(mc.level == null || mc.player == null);
	};
};
