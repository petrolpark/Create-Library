package com.petrolpark.compat.create;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.compat.create.common.processing.extrusion.ExtrudeCriterionTrigger;
import com.petrolpark.compat.create.core.advancement.PotatoCannonHitCriterionTrigger;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.advancements.CriterionTrigger;

public class PetrolparkCreateCriterionTriggers {
    
    public static final RegistryEntry<CriterionTrigger<?>, ExtrudeCriterionTrigger> EXTRUSION = REGISTRATE.criterionTrigger("extrude", ExtrudeCriterionTrigger::new);
    public static final RegistryEntry<CriterionTrigger<?>, PotatoCannonHitCriterionTrigger> POTATO_CANNON_HIT = REGISTRATE.criterionTrigger("potato_cannon_hit", PotatoCannonHitCriterionTrigger::new);

    public static final void register() {};
};
