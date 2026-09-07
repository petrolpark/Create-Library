package petrolpark.mc.library.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import java.util.Optional;

import com.mojang.serialization.MapCodec;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.world.entity.animal.Panda;
import petrolpark.mc.library.core.data.predicate.entity.ChargedCreeperEntitySubPredicate;
import petrolpark.mc.library.core.data.predicate.entity.ColorEntitySubPredicate;
import petrolpark.mc.library.core.data.predicate.entity.HorseMarkingsEntitySubPredicate;
import petrolpark.mc.library.core.data.predicate.entity.IsNeutralPredicate;
import petrolpark.mc.library.core.data.predicate.entity.NameEntitySubPredicate;
import petrolpark.mc.library.core.data.predicate.entity.OrEntitySubPredicate;
import petrolpark.mc.library.core.data.predicate.entity.PermissionsEntitySubPredicate;
import petrolpark.mc.library.core.data.predicate.entity.TamedEntitySubPredicate;
import petrolpark.mc.library.core.data.predicate.entity.VillagerProfessionEntitySubPredicate;
import petrolpark.mc.library.core.data.predicate.item.AdvancedIngredientItemSubPredicate;
import petrolpark.mc.library.core.data.predicate.item.HasFlagItemSubPredicate;
import petrolpark.mc.library.core.world.entity.player.team.predicate.ITeamPredicate;
import petrolpark.mc.library.core.world.entity.player.team.predicate.NumberComparisonTeamPredicate;

public class PetrolparkDataSubPredicates {

    public static final RegistryEntry<ItemSubPredicate.Type<?>, ? extends ItemSubPredicate.Type<?>> 

    ITEM_ADVANCED_INGREDIENT = REGISTRATE.itemSubPredicateType("advanced", AdvancedIngredientItemSubPredicate.CODEC),
    ITEM_HAS_FLAG = REGISTRATE.itemSubPredicateType("has_flag", HasFlagItemSubPredicate.CODEC);
    
    public static final RegistryEntry<MapCodec<? extends EntitySubPredicate>, ? extends MapCodec<? extends EntitySubPredicate>>

    ENTITY_COLOR = REGISTRATE.entitySubPredicateType("color", ColorEntitySubPredicate.CODEC),
    ENTITY_CHARGED_CREEPER = REGISTRATE.entitySubPredicateType("charged_creeper", ChargedCreeperEntitySubPredicate.CODEC),
    ENTITY_HORSE_MARKINGS = REGISTRATE.entitySubPredicateType("horse_markings", HorseMarkingsEntitySubPredicate.CODEC),
    ENTITY_NAME = REGISTRATE.entitySubPredicateType("name", NameEntitySubPredicate.CODEC),
    ENTITY_NEUTRAL = REGISTRATE.entitySubPredicateType("is_neutral", IsNeutralPredicate.CODEC),
    ENTITY_OR = REGISTRATE.entitySubPredicateType("or", OrEntitySubPredicate.CODEC),
    ENTITY_PANDA = REGISTRATE.entityVariantPredicateType("panda", Panda.Gene.CODEC, e -> e instanceof Panda panda ? Optional.of(panda.getMainGene()) : Optional.empty()),
    ENTITY_PERMISSIONS = REGISTRATE.entitySubPredicateType("permissions", PermissionsEntitySubPredicate.CODEC),
    ENTITY_TAMED = REGISTRATE.entitySubPredicateType("tamed", MapCodec.unit(TamedEntitySubPredicate::new)),
    ENTITY_VILLAGER_PROFESSION = REGISTRATE.entitySubPredicateType("villager_profession", VillagerProfessionEntitySubPredicate.CODEC);

    public static final RegistryEntry<ITeamPredicate.Type, ITeamPredicate.Type>

    TEAM_NUMBER_COMPARISON = REGISTRATE.teamPredicateType("number_comparison", NumberComparisonTeamPredicate.CODEC);

    public static final void register() {};
};
