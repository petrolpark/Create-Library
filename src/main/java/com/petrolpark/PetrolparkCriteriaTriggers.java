package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.core.badge.ReceiveBadgeCriterionTrigger;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.advancements.CriterionTrigger;

public class PetrolparkCriteriaTriggers {
    
    public static final RegistryEntry<CriterionTrigger<?>, ReceiveBadgeCriterionTrigger> RECEIVE_BADGE = REGISTRATE.criterionTrigger("receive_badge", ReceiveBadgeCriterionTrigger::new);

    public static final void register() {};
};
