package petrolpark.mc.library.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.core.data.loot.numberprovider.ConditionalNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.ContextEntityNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.ContextTeamNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.ContextToolNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.CustomerWaitTimeNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.DataComponentNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.MaxNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.MeanNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.MinNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.PolynomialNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.ProductNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.SigmoidNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.SumNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.entity.AttributeEntityNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.entity.EntityPredicateNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.entity.EquipmentNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.entity.ExperienceLevelNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.entity.FlatEntityNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.entity.LootEntityNumberProviderType;
import petrolpark.mc.library.core.data.loot.numberprovider.itemstack.CountItemStackNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.itemstack.EnchantmentLevelItemStackNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.itemstack.FlatItemStackNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.itemstack.ItemPredicateNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.itemstack.LootItemStackNumberProviderType;
import petrolpark.mc.library.core.data.loot.numberprovider.team.FlatTeamNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.team.LootTeamNumberProviderType;
import petrolpark.mc.library.core.data.loot.numberprovider.team.MemberCountTeamNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.team.MemberReductionTeamNumberProvider;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;

public class PetrolparkNumberProviderTypes {
    
    public static final RegistryEntry<LootNumberProviderType, LootNumberProviderType>
    
    MAX = REGISTRATE.functionLootNumberProviderType("max", MaxNumberProvider::new),
    MIN = REGISTRATE.functionLootNumberProviderType("max", MinNumberProvider::new),
    SUM = REGISTRATE.functionLootNumberProviderType("max", SumNumberProvider::new),
    PRODUCT = REGISTRATE.functionLootNumberProviderType("max", ProductNumberProvider::new),
    MEAN = REGISTRATE.functionLootNumberProviderType("max", MeanNumberProvider::new),
    POLYNOMIAL = REGISTRATE.lootNumberProviderType("polynomial", PolynomialNumberProvider.CODEC),
    SIGMOID = REGISTRATE.lootNumberProviderType("sigmoid", SigmoidNumberProvider.CODEC),
    CONDITIONAL = REGISTRATE.lootNumberProviderType("conditional", ConditionalNumberProvider.CODEC),

    CUSTOMER_WAIT_TIME = REGISTRATE.lootNumberProviderType("customer_wait_time", MapCodec.unit(CustomerWaitTimeNumberProvider::new)),

    CONTEXT_ENTITY = REGISTRATE.lootNumberProviderType("entity_property", ContextEntityNumberProvider.CODEC),
    CONTEXT_TEAM = REGISTRATE.lootNumberProviderType("team_property", ContextTeamNumberProvider.CODEC),
    CONTEXT_TOOL = REGISTRATE.lootNumberProviderType("tool_property", ContextToolNumberProvider.CODEC);

    public static final RegistryEntry<LootEntityNumberProviderType, LootEntityNumberProviderType>
    
    ATTRIBUTE = REGISTRATE.lootEntityNumberProviderType("attribute", AttributeEntityNumberProvider.CODEC),
    ENTITY_PREDICATE = REGISTRATE.lootEntityNumberProviderType("predicate", EntityPredicateNumberProvider.CODEC),
    EQUIPMENT = REGISTRATE.lootEntityNumberProviderType("equipment_property", EquipmentNumberProvider.CODEC),
    EXPERIENCE_LEVEL = REGISTRATE.lootEntityNumberProviderType("experience_level", MapCodec.unit(ExperienceLevelNumberProvider::new)),
    FLAT_ENTITY = REGISTRATE.lootEntityNumberProviderType("flat", FlatEntityNumberProvider.CODEC);

    public static final RegistryEntry<LootItemStackNumberProviderType, LootItemStackNumberProviderType>
    
    COUNT = REGISTRATE.lootItemStackNumberProviderType("count", MapCodec.unit(CountItemStackNumberProvider::new)),
    ITEM_DATA_COMPONENT = REGISTRATE.lootItemStackNumberProviderType("component", DataComponentNumberProvider.CODEC),
    ENCHANTMENT_LEVEL = REGISTRATE.lootItemStackNumberProviderType("enchantment_level", EnchantmentLevelItemStackNumberProvider.CODEC),
    FLAT_ITEM_STACK = REGISTRATE.lootItemStackNumberProviderType("flat", FlatItemStackNumberProvider.CODEC),
    ITEM_PREDICATE = REGISTRATE.lootItemStackNumberProviderType("predicate", ItemPredicateNumberProvider.CODEC);

    public static final RegistryEntry<LootTeamNumberProviderType, LootTeamNumberProviderType>

    FLAT_TEAM = REGISTRATE.lootTeamNumberProviderType("flat", FlatTeamNumberProvider.CODEC),
    MEMBER_COUNT = REGISTRATE.lootTeamNumberProviderType("member_count", MapCodec.unit(MemberCountTeamNumberProvider::new)),
    MEMBER_REDUCTION = REGISTRATE.lootTeamNumberProviderType("member_reduction", MemberReductionTeamNumberProvider.CODEC),
    TEAM_DATA_COMPONENT = REGISTRATE.lootTeamNumberProviderType("component", DataComponentNumberProvider.CODEC);

    public static final void register() {};
};
