package petrolpark.mc.library.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import petrolpark.mc.library.core.data.reward.ContextEntityReward;
import petrolpark.mc.library.core.data.reward.ContextTeamReward;
import petrolpark.mc.library.core.data.reward.RewardType;
import petrolpark.mc.library.core.data.reward.entity.AllTeamsPlayerReward;
import petrolpark.mc.library.core.data.reward.entity.EntityRewardType;
import petrolpark.mc.library.core.data.reward.entity.GiveItemEntityReward;
import petrolpark.mc.library.core.data.reward.entity.GiveLootEntityReward;
import petrolpark.mc.library.core.data.reward.entity.GrantExperiencePlayerReward;
import petrolpark.mc.library.core.data.reward.entity.UnlockTradeEntityReward;
import petrolpark.mc.library.core.data.reward.entity.VillagerGossipEntityReward;
import petrolpark.mc.library.core.data.reward.team.GrantRestaurantXPTeamReward;
import petrolpark.mc.library.core.data.reward.team.MembersTeamReward;
import petrolpark.mc.library.core.data.reward.team.TeamRewardType;
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
    GRANT_RESTAURANT_XP = REGISTRATE.teamRewardType("grant_restaurant_xp", GrantRestaurantXPTeamReward.CODEC);
  
    public static final void register() {};
};
