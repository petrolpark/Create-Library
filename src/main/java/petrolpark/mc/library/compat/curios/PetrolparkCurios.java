package petrolpark.mc.library.compat.curios;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.compat.Mods;
import petrolpark.mc.library.compat.curios.renderer.CuriosRenderers;
import petrolpark.mc.library.core.badge.BadgeItem;
import com.simibubi.create.content.equipment.goggles.GogglesItem;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

public class PetrolparkCurios {

    public static Optional<Map<String, ICurioStacksHandler>> getCuriosMap(LivingEntity entity) {
		return Optional.ofNullable(entity.getCapability(CuriosCapability.INVENTORY)).map(ICuriosItemHandler::getCurios);
	};
    
    public static void ctor(IEventBus modEventBus, IEventBus forgeEventBus) {

        // Add predicates
        Mods.CREATE.executeIfInstalled(() -> () -> GogglesItem.addIsWearingPredicate(wearingCurioPredicate(stack -> PetrolparkCuriosSetup.ENGINEERS_GOGGLES.stream().anyMatch(b -> b.get().get().equals(stack.getItem())), "head")));

        // Rendering
        modEventBus.addListener(PetrolparkCurios::onClientSetup);
    
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
