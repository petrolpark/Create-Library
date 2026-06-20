package com.petrolpark.registry;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.core.data.reward.generator.CombinedRewardGenerator;
import com.petrolpark.core.data.reward.generator.DirectRewardGenerator;
import com.petrolpark.core.data.reward.generator.LootTableRewardGenerator;
import com.petrolpark.core.data.reward.generator.RewardGeneratorType;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class PetrolparkRewardGeneratorTypes {
    
    public static final RegistryEntry<RewardGeneratorType, RewardGeneratorType>
    
    DIRECT = REGISTRATE.rewardGeneratorType("direct", DirectRewardGenerator.CODEC),
    COMBINED = REGISTRATE.rewardGeneratorType("combined", CombinedRewardGenerator.CODEC),
    LOOT_TABLE = REGISTRATE.rewardGeneratorType("loot_table", LootTableRewardGenerator.CODEC);

    public static final void register() {};
};
