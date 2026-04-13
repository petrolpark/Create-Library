package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.core.data.reward.ContextEntityReward;
import com.petrolpark.core.data.reward.ContextTeamReward;
import com.petrolpark.core.data.reward.RewardType;
import com.petrolpark.core.data.reward.entity.AllTeamsPlayerReward;
import com.petrolpark.core.data.reward.entity.EntityRewardType;
import com.petrolpark.core.data.reward.entity.GiveItemEntityReward;
import com.petrolpark.core.data.reward.entity.GiveLootEntityReward;
import com.petrolpark.core.data.reward.entity.GrantExperiencePlayerReward;
import com.petrolpark.core.data.reward.entity.UnlockTradeEntityReward;
import com.petrolpark.core.data.reward.entity.VillagerGossipEntityReward;
import com.petrolpark.core.data.reward.team.GrantShopXPTeamReward;
import com.petrolpark.core.data.reward.team.MembersTeamReward;
import com.petrolpark.core.data.reward.team.TeamRewardType;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class PetrolparkRewardTypes {

    public static final RegistryEntry<RewardType, RewardType>
    
    CONTEXT_ENTITY = REGISTRATE.rewardType("to_entity", ContextEntityReward.CODEC),
    CONTEXT_TEAM = REGISTRATE.rewardType("to_team", ContextTeamReward.CODEC);

    public static final RegistryEntry<EntityRewardType, EntityRewardType>

    GIVE_ITEM = REGISTRATE.entityRewardType("give_item", GiveItemEntityReward.CODEC),
    GIVE_LOOT = REGISTRATE.entityRewardType("give_loot", GiveLootEntityReward.CODEC),
    GRANT_EXPERIENCE = REGISTRATE.entityRewardType("grant_experience", GrantExperiencePlayerReward.CODEC),
    UNLOCK_TRADE = REGISTRATE.entityRewardType("unlock_trade", UnlockTradeEntityReward.CODEC),
    GOSSIP = REGISTRATE.entityRewardType("villager_gossip", VillagerGossipEntityReward.CODEC),
    ALL_TEAMS = REGISTRATE.entityRewardType("all_teams", AllTeamsPlayerReward.CODEC);

    public static final RegistryEntry<TeamRewardType, TeamRewardType>

    MEMBERS = REGISTRATE.teamRewardType("members", MembersTeamReward.CODEC),
    GRANT_SHOP_XP = REGISTRATE.teamRewardType("grant_shop_xp", GrantShopXPTeamReward.CODEC);
  
    public static final void register() {};
};
