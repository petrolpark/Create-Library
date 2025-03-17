package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.data.reward.ContextEntityReward;
import com.petrolpark.data.reward.ContextTeamReward;
import com.petrolpark.data.reward.RewardType;
import com.petrolpark.data.reward.entity.EntityRewardType;
import com.petrolpark.data.reward.entity.GiveItemEntityReward;
import com.petrolpark.data.reward.entity.GiveLootEntityReward;
import com.petrolpark.data.reward.entity.GrantExperienceEntityReward;
import com.petrolpark.data.reward.entity.UnlockTradeEntityReward;
import com.petrolpark.data.reward.team.GrantShopXPTeamReward;
import com.petrolpark.data.reward.team.MembersTeamReward;
import com.petrolpark.data.reward.team.TeamRewardType;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class PetrolparkRewardTypes {

    public static final RegistryEntry<RewardType, RewardType>
    
    CONTEXT_ENTITY = REGISTRATE.rewardType("to_entity", ContextEntityReward.CODEC),
    CONTEXT_TEAM = REGISTRATE.rewardType("to_team", ContextTeamReward.CODEC);

    public static final RegistryEntry<EntityRewardType, EntityRewardType>

    GIVE_ITEM = REGISTRATE.entityRewardType("give_item", GiveItemEntityReward.CODEC),
    GIVE_LOOT = REGISTRATE.entityRewardType("give_loot", GiveLootEntityReward.CODEC),
    GRANT_EXPERIENCE = REGISTRATE.entityRewardType("grant_experience", GrantExperienceEntityReward.CODEC),
    UNLOCK_TRADE = REGISTRATE.entityRewardType("unlock_trade", UnlockTradeEntityReward.CODEC);

    public static final RegistryEntry<TeamRewardType, TeamRewardType>

    MEMBERS = REGISTRATE.teamRewardType("null", MembersTeamReward.CODEC),
    GRANT_SHOP_XP = REGISTRATE.teamRewardType("grant_shop_xp", GrantShopXPTeamReward.CODEC);
  
    public static final void register() {};
};
