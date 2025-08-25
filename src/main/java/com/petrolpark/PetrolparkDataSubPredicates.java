package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.mojang.serialization.MapCodec;
import com.petrolpark.core.data.predicate.entity.ColorEntitySubPredicate;
import com.petrolpark.core.data.predicate.entity.PermissionsEntitySubPredicate;
import com.petrolpark.core.data.predicate.item.HasContaminantItemSubPredicate;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicate;

public class PetrolparkDataSubPredicates {

    public static final RegistryEntry<ItemSubPredicate.Type<?>, ? extends ItemSubPredicate.Type<?>> 

    ITEM_HAS_CONTAMINANT = REGISTRATE.itemSubPredicateType("has_contaminant", HasContaminantItemSubPredicate.CODEC);
    
    public static final RegistryEntry<MapCodec<? extends EntitySubPredicate>, ? extends MapCodec<? extends EntitySubPredicate>>

    ENTITY_COLOR = REGISTRATE.entitySubPredicateType("color", ColorEntitySubPredicate.CODEC),
    ENTITY_PERMISSIONS = REGISTRATE.entitySubPredicateType("permissions", PermissionsEntitySubPredicate.CODEC);

    public static final void register() {};
};
