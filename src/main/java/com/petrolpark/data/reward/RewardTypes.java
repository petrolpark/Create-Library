package com.petrolpark.data.reward;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.tterrag.registrate.util.entry.RegistryEntry;

public class RewardTypes {

    public static final RegistryEntry<RewardType, RewardType>
    
    GIVE_ITEM = REGISTRATE.rewardType("give_item", new GiveItemReward.Serializer()),
    GIVE_LOOT = REGISTRATE.rewardType("give_loot", new GiveLootReward.Serializer()),
    GRANT_EXPERIENCE = REGISTRATE.rewardType("grant_experience", new GrantExperienceReward.Serializer()),
    GRANT_SHOP_XP = REGISTRATE.rewardType("grant_shop_xp", new GrantShopXPReward.Serializer()),
    UNLOCK_TRADE = REGISTRATE.rewardType("unlock_trade", new UnlockTradeReward.Serializer());
  
    public static final void register() {};
};
