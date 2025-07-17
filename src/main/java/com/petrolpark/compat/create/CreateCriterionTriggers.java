package com.petrolpark.compat.create;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.compat.create.common.processing.extrusion.ExtrudeCriterionTrigger;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.advancements.CriterionTrigger;

public class CreateCriterionTriggers {
    
    public static final RegistryEntry<CriterionTrigger<?>, ExtrudeCriterionTrigger> EXTRUSION = REGISTRATE.criterionTrigger("extrude", ExtrudeCriterionTrigger::new);

    public static final void register() {};
};
