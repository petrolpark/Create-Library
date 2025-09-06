package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.mojang.serialization.MapCodec;
import com.petrolpark.core.data.loot.numberprovider.ContextEntityNumberProvider;
import com.petrolpark.core.data.loot.numberprovider.ContextTeamNumberProvider;
import com.petrolpark.core.data.loot.numberprovider.ContextToolNumberProvider;
import com.petrolpark.core.data.loot.numberprovider.CustomerWaitTimeNumberProvider;
import com.petrolpark.core.data.loot.numberprovider.FunctionNumberProvider;
import com.petrolpark.core.data.loot.numberprovider.MaxNumberProvider;
import com.petrolpark.core.data.loot.numberprovider.MinNumberProvider;
import com.petrolpark.core.data.loot.numberprovider.PolynomialNumberProvider;
import com.petrolpark.core.data.loot.numberprovider.ProductNumberProvider;
import com.petrolpark.core.data.loot.numberprovider.SigmoidNumberProvider;
import com.petrolpark.core.data.loot.numberprovider.SumNumberProvider;
import com.petrolpark.core.data.loot.numberprovider.entity.EntityPredicateNumberProvider;
import com.petrolpark.core.data.loot.numberprovider.entity.EquipmentNumberProvider;
import com.petrolpark.core.data.loot.numberprovider.entity.ExperienceLevelNumberProvider;
import com.petrolpark.core.data.loot.numberprovider.entity.LootEntityNumberProviderType;
import com.petrolpark.core.data.loot.numberprovider.itemstack.CountItemStackNumberProvider;
import com.petrolpark.core.data.loot.numberprovider.itemstack.EnchantmentLevelItemStackNumberProvider;
import com.petrolpark.core.data.loot.numberprovider.itemstack.LootItemStackNumberProviderType;
import com.petrolpark.core.data.loot.numberprovider.team.LootTeamNumberProviderType;
import com.petrolpark.core.data.loot.numberprovider.team.MembersTeamNumberProvider;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;

public class PetrolparkNumberProviderTypes {
    
    public static final RegistryEntry<LootNumberProviderType, LootNumberProviderType>
    
    MAX = REGISTRATE.lootNumberProviderType("max", FunctionNumberProvider.codec(MaxNumberProvider::new)),
    MIN = REGISTRATE.lootNumberProviderType("min", FunctionNumberProvider.codec(MinNumberProvider::new)),
    SUM = REGISTRATE.lootNumberProviderType("sum", FunctionNumberProvider.codec(SumNumberProvider::new)),
    PRODUCT = REGISTRATE.lootNumberProviderType("product", FunctionNumberProvider.codec(ProductNumberProvider::new)),
    POLYNOMIAL = REGISTRATE.lootNumberProviderType("polynomial", PolynomialNumberProvider.CODEC),
    SIGMOID = REGISTRATE.lootNumberProviderType("sigmoid", SigmoidNumberProvider.CODEC),

    CUSTOMER_WAIT_TIME = REGISTRATE.lootNumberProviderType("customer_wait_time", MapCodec.unit(CustomerWaitTimeNumberProvider::new)),

    CONTEXT_ENTITY = REGISTRATE.lootNumberProviderType("entity_property", ContextEntityNumberProvider.CODEC),
    CONTEXT_TEAM = REGISTRATE.lootNumberProviderType("team_property", ContextTeamNumberProvider.CODEC),
    CONTEXT_TOOL = REGISTRATE.lootNumberProviderType("tool_property", ContextToolNumberProvider.CODEC);


    public static final RegistryEntry<LootEntityNumberProviderType, LootEntityNumberProviderType>
    
    ENTITY_PREDICATE = REGISTRATE.lootEntityNumberProviderType("predicate", EntityPredicateNumberProvider.CODEC),
    EQUIPMENT = REGISTRATE.lootEntityNumberProviderType("equipment_property", EquipmentNumberProvider.CODEC),
    EXPERIENCE_LEVEL = REGISTRATE.lootEntityNumberProviderType("experience_level", MapCodec.unit(ExperienceLevelNumberProvider::new));


    public static final RegistryEntry<LootItemStackNumberProviderType, LootItemStackNumberProviderType>
    
    COUNT = REGISTRATE.lootItemStackNumberProviderType("count", MapCodec.unit(CountItemStackNumberProvider::new)),
    ENCHANTMENT_LEVEL = REGISTRATE.lootItemStackNumberProviderType("enchantment_level", EnchantmentLevelItemStackNumberProvider.CODEC);


    public static final RegistryEntry<LootTeamNumberProviderType, LootTeamNumberProviderType>

    MEMBER_COUNT = REGISTRATE.lootTeamNumberProviderType("members", MapCodec.unit(MembersTeamNumberProvider::new));


    public static final void register() {};
};
