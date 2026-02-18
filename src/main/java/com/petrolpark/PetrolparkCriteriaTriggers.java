package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.compat.create.common.processing.centrifuge.CentrifugeCriterionTrigger;
import com.petrolpark.compat.create.common.processing.extrusion.ExtrudeCriterionTrigger;
import com.petrolpark.core.badge.ReceiveBadgeCriterionTrigger;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.advancements.CriterionTrigger;

public class PetrolparkCriteriaTriggers {
    
    public static final RegistryEntry<CriterionTrigger<?>, CentrifugeCriterionTrigger> CENTRIFUGE = REGISTRATE.criterionTrigger("centrifuge", CentrifugeCriterionTrigger::new);
    public static final RegistryEntry<CriterionTrigger<?>, ExtrudeCriterionTrigger> EXTRUDE = REGISTRATE.criterionTrigger("extrude", ExtrudeCriterionTrigger::new);
    public static final RegistryEntry<CriterionTrigger<?>, ReceiveBadgeCriterionTrigger> RECEIVE_BADGE = REGISTRATE.criterionTrigger("receive_badge", ReceiveBadgeCriterionTrigger::new);

    public static final void register() {};
};
