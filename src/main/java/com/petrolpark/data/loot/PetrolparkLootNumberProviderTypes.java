package com.petrolpark.data.loot;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.mojang.serialization.MapCodec;
import com.petrolpark.data.loot.numberprovider.ContextEntityNumberProvider;
import com.petrolpark.data.loot.numberprovider.ContextTeamNumberProvider;
import com.petrolpark.data.loot.numberprovider.CustomerWaitTimeNumberProvider;
import com.petrolpark.data.loot.numberprovider.FunctionNumberProvider;
import com.petrolpark.data.loot.numberprovider.MaxNumberProvider;
import com.petrolpark.data.loot.numberprovider.MinNumberProvider;
import com.petrolpark.data.loot.numberprovider.ProductNumberProvider;
import com.petrolpark.data.loot.numberprovider.SigmoidNumberProvider;
import com.petrolpark.data.loot.numberprovider.SumNumberProvider;
import com.petrolpark.data.loot.numberprovider.ToolNumberProvider;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;

public class PetrolparkLootNumberProviderTypes {
    
    public static final RegistryEntry<LootNumberProviderType, LootNumberProviderType>
    
    MAX = REGISTRATE.lootNumberProviderType("max", FunctionNumberProvider.codec(MaxNumberProvider::new)),
    MIN = REGISTRATE.lootNumberProviderType("min", FunctionNumberProvider.codec(MinNumberProvider::new)),
    SUM = REGISTRATE.lootNumberProviderType("sum", FunctionNumberProvider.codec(SumNumberProvider::new)),
    PRODUCT = REGISTRATE.lootNumberProviderType("product", FunctionNumberProvider.codec(ProductNumberProvider::new)),
    SIGMOID = REGISTRATE.lootNumberProviderType("sigmoid", SigmoidNumberProvider.CODEC),

    CUSTOMER_WAIT_TIME = REGISTRATE.lootNumberProviderType("customer_wait_time", MapCodec.unit(CustomerWaitTimeNumberProvider::new)),

    CONTEXT_ENTITY = REGISTRATE.lootNumberProviderType("context_entity_property", ContextEntityNumberProvider.CODEC),
    CONTEXT_TEAM = REGISTRATE.lootNumberProviderType("context_team_property", ContextTeamNumberProvider.CODEC),
    TOOL = REGISTRATE.lootNumberProviderType("tool_property", ToolNumberProvider.CODEC);

    public static final void register() {};
};
