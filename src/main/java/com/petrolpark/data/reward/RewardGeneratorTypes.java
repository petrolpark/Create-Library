package com.petrolpark.data.reward;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.data.reward.generator.CombinedRewardGenerator;
import com.petrolpark.data.reward.generator.DirectRewardGenerator;
import com.petrolpark.data.reward.generator.LootTableRewardGenerator;
import com.petrolpark.data.reward.generator.RewardGeneratorType;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class RewardGeneratorTypes {
    
    public static final RegistryEntry<RewardGeneratorType> DIRECT = REGISTRATE.get().rewardGeneratorType("direct", new DirectRewardGenerator.Serializer());
    public static final RegistryEntry<RewardGeneratorType> COMBINED = REGISTRATE.get().rewardGeneratorType("combined", new CombinedRewardGenerator.Serializer());
    public static final RegistryEntry<RewardGeneratorType> LOOT_TABLE = REGISTRATE.get().rewardGeneratorType("loot_table", new LootTableRewardGenerator.Serializer());

    public static final void register() {};
};
