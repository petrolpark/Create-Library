package petrolpark.mc.library.compat.create.shared.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import petrolpark.mc.library.compat.create.core.data.advancement.PotatoCannonHitCriterionTrigger;
import petrolpark.mc.library.compat.create.shared.content.processing.extrusion.ExtrudeCriterionTrigger;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.advancements.CriterionTrigger;

public class SharedCreateCriterionTriggers {
    
    public static final RegistryEntry<CriterionTrigger<?>, ExtrudeCriterionTrigger> EXTRUSION = REGISTRATE.criterionTrigger("extrude", ExtrudeCriterionTrigger::new);
    public static final RegistryEntry<CriterionTrigger<?>, PotatoCannonHitCriterionTrigger> POTATO_CANNON_HIT = REGISTRATE.criterionTrigger("potato_cannon_hit", PotatoCannonHitCriterionTrigger::new);

    public static final void register() {};
};
