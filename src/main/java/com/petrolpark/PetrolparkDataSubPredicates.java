package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import java.util.Optional;

import com.mojang.serialization.MapCodec;
import com.petrolpark.core.data.predicate.entity.ChargedCreeperEntitySubPredicate;
import com.petrolpark.core.data.predicate.entity.ColorEntitySubPredicate;
import com.petrolpark.core.data.predicate.entity.HorseMarkingsEntitySubPredicate;
import com.petrolpark.core.data.predicate.entity.IsNeutralPredicate;
import com.petrolpark.core.data.predicate.entity.OrEntitySubPredicate;
import com.petrolpark.core.data.predicate.entity.PermissionsEntitySubPredicate;
import com.petrolpark.core.data.predicate.entity.VillagerProfessionEntitySubPredicate;
import com.petrolpark.core.data.predicate.item.AdvancedIngredientItemSubPredicate;
import com.petrolpark.core.data.predicate.item.HasFlagItemSubPredicate;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.world.entity.animal.Panda;

public class PetrolparkDataSubPredicates {

    public static final RegistryEntry<ItemSubPredicate.Type<?>, ? extends ItemSubPredicate.Type<?>> 

    ITEM_ADVANCED_INGREDIENT = REGISTRATE.itemSubPredicateType("advanced", AdvancedIngredientItemSubPredicate.CODEC),
    ITEM_HAS_FLAG = REGISTRATE.itemSubPredicateType("has_flag", HasFlagItemSubPredicate.CODEC);
    
    public static final RegistryEntry<MapCodec<? extends EntitySubPredicate>, ? extends MapCodec<? extends EntitySubPredicate>>

    ENTITY_COLOR = REGISTRATE.entitySubPredicateType("color", ColorEntitySubPredicate.CODEC),
    ENTITY_CHARGED_CREEPER = REGISTRATE.entitySubPredicateType("charged_creeper", ChargedCreeperEntitySubPredicate.CODEC),
    ENTITY_HORSE_MARKINGS = REGISTRATE.entitySubPredicateType("horse_markings", HorseMarkingsEntitySubPredicate.CODEC),
    ENTITY_NEUTRAL = REGISTRATE.entitySubPredicateType("is_neutral", IsNeutralPredicate.CODEC),
    ENTITY_OR = REGISTRATE.entitySubPredicateType("or", OrEntitySubPredicate.CODEC),
    ENTITY_PANDA = REGISTRATE.entityVariantPredicateType("panda", Panda.Gene.CODEC, e -> e instanceof Panda panda ? Optional.of(panda.getMainGene()) : Optional.empty()),
    ENTITY_PERMISSIONS = REGISTRATE.entitySubPredicateType("permissions", PermissionsEntitySubPredicate.CODEC),
    ENTITY_VILLAGER_PROFESSION = REGISTRATE.entitySubPredicateType("villager_profession", VillagerProfessionEntitySubPredicate.CODEC);

    public static final void register() {};
};
