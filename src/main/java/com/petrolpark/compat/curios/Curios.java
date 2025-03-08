package com.petrolpark.compat.curios;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import com.petrolpark.Petrolpark;
import com.petrolpark.badge.BadgeItem;
import com.petrolpark.compat.CompatMods;
import com.petrolpark.compat.curios.renderer.CuriosRenderers;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import com.simibubi.create.foundation.utility.DistExecutor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

public class Curios {

    public static Optional<Map<String, ICurioStacksHandler>> getCuriosMap(LivingEntity entity) {
		return entity.getCapability(CuriosCapability.INVENTORY).map(ICuriosItemHandler::getCurios);
	};
    
    public static void ctor(IEventBus modEventBus, IEventBus forgeEventBus) {

        CompatMods.CREATE.executeIfInstalled(() -> () -> GogglesItem.addIsWearingPredicate(wearingCurioPredicate(stack -> CuriosSetup.ENGINEERS_GOGGLES.stream().anyMatch(b -> b.get().get().equals(stack.getItem())), "head")));

        // Rendering
        modEventBus.addListener(Curios::onClientSetup);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modEventBus.addListener(CuriosRenderers::onLayerRegister));
    
        // Validators
        CuriosApi.registerCurioPredicate(Petrolpark.asResource("badge"), sr -> sr.stack().getItem() instanceof BadgeItem);
    };

    public static <E extends LivingEntity> Predicate<E> wearingCurioPredicate(Predicate<ItemStack> curioPredicate, String slotId) {
        return le -> {
            return getCuriosMap(le)
            .map(m -> m.get(slotId))
            .map(sh -> {
                for (int slot = 0; slot < sh.getSlots(); slot++) {
                    if (curioPredicate.test(sh.getStacks().getStackInSlot(slot))) return true;
                };
                return false;
            }).orElse(false);
        };
    };

    private static void onClientSetup(final FMLClientSetupEvent event) {
		CuriosRenderers.register();
	};
};
