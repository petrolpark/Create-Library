package petrolpark.mc.library.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.advancements.CriterionTrigger;
import petrolpark.mc.library.compat.create.shared.content.processing.centrifuge.CentrifugeCriterionTrigger;
import petrolpark.mc.library.compat.create.shared.content.processing.extrusion.ExtrudeCriterionTrigger;
import petrolpark.mc.library.core.badge.ReceiveBadgeCriterionTrigger;
import petrolpark.mc.library.core.client.ponder.WatchPonderCriterionTrigger;
import petrolpark.mc.library.core.world.restaurant.RestaurantChangedCriterionTrigger;

public class PetrolparkCriteriaTriggers {
    
    //TODO move to Shared
    public static final RegistryEntry<CriterionTrigger<?>, CentrifugeCriterionTrigger> CENTRIFUGE = REGISTRATE.criterionTrigger("centrifuge", CentrifugeCriterionTrigger::new);
    public static final RegistryEntry<CriterionTrigger<?>, ExtrudeCriterionTrigger> EXTRUDE = REGISTRATE.criterionTrigger("extrude", ExtrudeCriterionTrigger::new);
    public static final RegistryEntry<CriterionTrigger<?>, ReceiveBadgeCriterionTrigger> RECEIVE_BADGE = REGISTRATE.criterionTrigger("receive_badge", ReceiveBadgeCriterionTrigger::new);
    public static final RegistryEntry<CriterionTrigger<?>, RestaurantChangedCriterionTrigger> RESTAURANT_CHANGED = REGISTRATE.criterionTrigger("restaurant_changed", RestaurantChangedCriterionTrigger::new);
    public static final RegistryEntry<CriterionTrigger<?>, WatchPonderCriterionTrigger> WATCH_PONDER = REGISTRATE.criterionTrigger("ponder", WatchPonderCriterionTrigger::new);

    public static final void register() {};
};
