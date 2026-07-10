package petrolpark.mc.library.core.data.reward;

import net.minecraft.world.level.storage.loot.LootContextUser;
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;

public interface IAbstractReward<TYPE> extends LootContextUser {
  
    public IRewardInfo info();
    
    public TYPE getType();
};
