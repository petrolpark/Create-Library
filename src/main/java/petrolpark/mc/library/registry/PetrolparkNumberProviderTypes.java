package petrolpark.mc.library.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import com.mojang.serialization.MapCodec;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import petrolpark.mc.library.core.data.numberProvider.ConditionalNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.ContextEntityNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.ContextTeamNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.ContextToolNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.CustomerWaitTimeNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.DataComponentNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.MaxNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.MeanNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.MinNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.PolynomialNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.ProductNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.SigmoidNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.SumNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.entity.AnimalMoodEntityNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.entity.AttributeEntityNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.entity.EntityEffectDurationNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.entity.EntityPredicateNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.entity.EquipmentNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.entity.ExperienceLevelNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.entity.FlatEntityNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.entity.HealthEntityNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.entity.LootEntityNumberProviderType;
import petrolpark.mc.library.core.data.numberProvider.itemStack.CountItemStackNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.itemStack.EnchantmentLevelItemStackNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.itemStack.FlatItemStackNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.itemStack.ItemPredicateNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.itemStack.LootItemStackNumberProviderType;
import petrolpark.mc.library.core.data.numberProvider.itemStack.QualityItemStackNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.team.FlatTeamNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.team.LootTeamNumberProviderType;
import petrolpark.mc.library.core.data.numberProvider.team.MemberCountTeamNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.team.MemberReductionTeamNumberProvider;

public class PetrolparkNumberProviderTypes {
    
    public static final RegistryEntry<LootNumberProviderType, LootNumberProviderType>
    
    MAX = REGISTRATE.functionLootNumberProviderType("max", MaxNumberProvider::new),
    MIN = REGISTRATE.functionLootNumberProviderType("min", MinNumberProvider::new),
    SUM = REGISTRATE.functionLootNumberProviderType("sum", SumNumberProvider::new),
    PRODUCT = REGISTRATE.functionLootNumberProviderType("product", ProductNumberProvider::new),
    MEAN = REGISTRATE.functionLootNumberProviderType("mean", MeanNumberProvider::new),
    POLYNOMIAL = REGISTRATE.lootNumberProviderType("polynomial", PolynomialNumberProvider.CODEC),
    SIGMOID = REGISTRATE.lootNumberProviderType("sigmoid", SigmoidNumberProvider.CODEC),
    CONDITIONAL = REGISTRATE.lootNumberProviderType("conditional", ConditionalNumberProvider.CODEC),

    CUSTOMER_WAIT_TIME = REGISTRATE.lootNumberProviderType("customer_wait_time", MapCodec.unit(CustomerWaitTimeNumberProvider::new)),

    CONTEXT_ENTITY = REGISTRATE.lootNumberProviderType("entity_property", ContextEntityNumberProvider.CODEC),
    CONTEXT_TEAM = REGISTRATE.lootNumberProviderType("team_property", ContextTeamNumberProvider.CODEC),
    CONTEXT_TOOL = REGISTRATE.lootNumberProviderType("tool_property", ContextToolNumberProvider.CODEC);

    public static final RegistryEntry<LootEntityNumberProviderType, LootEntityNumberProviderType>
    
    ANIMAL_MOOD = REGISTRATE.lootEntityNumberProviderType("animal_mood", MapCodec.unit(AnimalMoodEntityNumberProvider::new)),
    ATTRIBUTE = REGISTRATE.lootEntityNumberProviderType("attribute", AttributeEntityNumberProvider.CODEC),
    ENTITY_PREDICATE = REGISTRATE.lootEntityNumberProviderType("predicate", EntityPredicateNumberProvider.CODEC),
    EFFECT_DURATION = REGISTRATE.lootEntityNumberProviderType("effect_duration", EntityEffectDurationNumberProvider.CODEC),
    EQUIPMENT = REGISTRATE.lootEntityNumberProviderType("equipment_property", EquipmentNumberProvider.CODEC),
    EXPERIENCE_LEVEL = REGISTRATE.lootEntityNumberProviderType("experience_level", MapCodec.unit(ExperienceLevelNumberProvider::new)),
    FLAT_ENTITY = REGISTRATE.lootEntityNumberProviderType("flat", FlatEntityNumberProvider.CODEC),
    HEALTH = REGISTRATE.lootEntityNumberProviderType("health", HealthEntityNumberProvider.CODEC);

    public static final RegistryEntry<LootItemStackNumberProviderType, LootItemStackNumberProviderType>
    
    COUNT = REGISTRATE.lootItemStackNumberProviderType("count", MapCodec.unit(CountItemStackNumberProvider::new)),
    ITEM_DATA_COMPONENT = REGISTRATE.lootItemStackNumberProviderType("component", DataComponentNumberProvider.CODEC),
    ENCHANTMENT_LEVEL = REGISTRATE.lootItemStackNumberProviderType("enchantment_level", EnchantmentLevelItemStackNumberProvider.CODEC),
    FLAT_ITEM_STACK = REGISTRATE.lootItemStackNumberProviderType("flat", FlatItemStackNumberProvider.CODEC),
    ITEM_PREDICATE = REGISTRATE.lootItemStackNumberProviderType("predicate", ItemPredicateNumberProvider.CODEC),
    ITEM_QUALITY = REGISTRATE.lootItemStackNumberProviderType("quality", QualityItemStackNumberProvider.CODEC);

    public static final RegistryEntry<LootTeamNumberProviderType, LootTeamNumberProviderType>

    FLAT_TEAM = REGISTRATE.lootTeamNumberProviderType("flat", FlatTeamNumberProvider.CODEC),
    MEMBER_COUNT = REGISTRATE.lootTeamNumberProviderType("member_count", MapCodec.unit(MemberCountTeamNumberProvider::new)),
    MEMBER_REDUCTION = REGISTRATE.lootTeamNumberProviderType("member_reduction", MemberReductionTeamNumberProvider.CODEC),
    TEAM_DATA_COMPONENT = REGISTRATE.lootTeamNumberProviderType("component", DataComponentNumberProvider.CODEC);

    public static final void register() {};
};
