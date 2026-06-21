package petrolpark.mc.library.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import petrolpark.mc.library.compat.create.shared.content.processing.centrifuge.CentrifugeCriterionTrigger;
import petrolpark.mc.library.compat.create.shared.content.processing.extrusion.ExtrudeCriterionTrigger;
import petrolpark.mc.library.core.badge.ReceiveBadgeCriterionTrigger;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.advancements.CriterionTrigger;

public class PetrolparkCriteriaTriggers {
    
    public static final RegistryEntry<CriterionTrigger<?>, CentrifugeCriterionTrigger> CENTRIFUGE = REGISTRATE.criterionTrigger("centrifuge", CentrifugeCriterionTrigger::new);
    public static final RegistryEntry<CriterionTrigger<?>, ExtrudeCriterionTrigger> EXTRUDE = REGISTRATE.criterionTrigger("extrude", ExtrudeCriterionTrigger::new);
    public static final RegistryEntry<CriterionTrigger<?>, ReceiveBadgeCriterionTrigger> RECEIVE_BADGE = REGISTRATE.criterionTrigger("receive_badge", ReceiveBadgeCriterionTrigger::new);

    public static final void register() {};
};
