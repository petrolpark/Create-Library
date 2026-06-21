package petrolpark.mc.library.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import petrolpark.mc.library.core.data.reward.generator.CombinedRewardGenerator;
import petrolpark.mc.library.core.data.reward.generator.DirectRewardGenerator;
import petrolpark.mc.library.core.data.reward.generator.LootTableRewardGenerator;
import petrolpark.mc.library.core.data.reward.generator.RewardGeneratorType;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class PetrolparkRewardGeneratorTypes {
    
    public static final RegistryEntry<RewardGeneratorType, RewardGeneratorType>
    
    DIRECT = REGISTRATE.rewardGeneratorType("direct", DirectRewardGenerator.CODEC),
    COMBINED = REGISTRATE.rewardGeneratorType("combined", CombinedRewardGenerator.CODEC),
    LOOT_TABLE = REGISTRATE.rewardGeneratorType("loot_table", LootTableRewardGenerator.CODEC);

    public static final void register() {};
};
