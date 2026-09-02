package petrolpark.mc.library.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import petrolpark.mc.library.core.data.reward.RewardCommand;

public class PetrolparkCommandArgumentTypes {

    public static final RegistryEntry<ArgumentTypeInfo<?, ?>, SingletonArgumentInfo<RewardCommand.RewardArgument>> REWARD = REGISTRATE
        .commandArgumentType("reward", RewardCommand.RewardArgument.class, SingletonArgumentInfo.contextAware(RewardCommand.RewardArgument::new));

    public static final RegistryEntry<ArgumentTypeInfo<?, ?>, SingletonArgumentInfo<RewardCommand.EntityRewardArgument>> ENTITY_REWARD = REGISTRATE
        .commandArgumentType("entity_reward", RewardCommand.EntityRewardArgument.class, SingletonArgumentInfo.contextAware(RewardCommand.EntityRewardArgument::new));
  
    public static final void register() {};
};
