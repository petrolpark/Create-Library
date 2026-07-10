package petrolpark.mc.library.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import com.tterrag.registrate.util.entry.RegistryEntry;

import petrolpark.mc.library.core.data.reward.ContextEntityReward;
import petrolpark.mc.library.core.data.reward.ContextTeamReward;
import petrolpark.mc.library.core.data.reward.GiveItemReward;
import petrolpark.mc.library.core.data.reward.GiveLootReward;
import petrolpark.mc.library.core.data.reward.IReward;
import petrolpark.mc.library.core.data.reward.RewardAndInfoType;
import petrolpark.mc.library.core.data.reward.entity.AllTeamsPlayerReward;
import petrolpark.mc.library.core.data.reward.entity.EntityRewardAndInfoType;
import petrolpark.mc.library.core.data.reward.entity.GrantAdvancementPlayerReward;
import petrolpark.mc.library.core.data.reward.entity.GrantExperiencePlayerReward;
import petrolpark.mc.library.core.data.reward.entity.IEntityReward;
import petrolpark.mc.library.core.data.reward.entity.UnlockTradeEntityReward;
import petrolpark.mc.library.core.data.reward.entity.VillagerGossipEntityReward;
import petrolpark.mc.library.core.data.reward.info.WrappedRewardInfo;
import petrolpark.mc.library.core.data.reward.team.GrantRestaurantXPTeamReward;
import petrolpark.mc.library.core.data.reward.team.ITeamReward;
import petrolpark.mc.library.core.data.reward.team.MembersTeamReward;
import petrolpark.mc.library.core.data.reward.team.TeamRewardAndInfoType;

public class PetrolparkRewardTypes {

    public static final RegistryEntry<IReward.Type, RewardAndInfoType>
    
    CONTEXT_ENTITY = REGISTRATE.rewardAndWrappedInfoTypes("to_entity", ContextEntityReward.CODEC, ContextEntityReward.Info::new),
    CONTEXT_TEAM = REGISTRATE.rewardAndWrappedInfoTypes("to_team", ContextTeamReward.CODEC, ContextEntityReward.Info::new),
    GIVE_ITEM = REGISTRATE.simpleRewardType("give_item", GiveItemReward.CODEC, GiveItemReward.STREAM_CODEC),
    GIVE_LOOT = REGISTRATE.rewardAndInfoTypes("give_loot", GiveLootReward.CODEC, GiveLootReward.Info.CODEC, GiveLootReward.Info.STREAM_CODEC);

    public static final RegistryEntry<IEntityReward.Type, EntityRewardAndInfoType>

    GRANT_ADVANCEMENT = REGISTRATE.entityRewardAndInfoTypes("grant_advancement", GrantAdvancementPlayerReward.CODEC, GrantAdvancementPlayerReward.Info.CODEC, GrantAdvancementPlayerReward.Info.STREAM_CODEC),
    GRANT_EXPERIENCE = REGISTRATE.entityRewardAndInfoTypes("grant_experience", GrantExperiencePlayerReward.CODEC, GrantExperiencePlayerReward.Info.CODEC, GrantExperiencePlayerReward.Info.STREAM_CODEC),
    UNLOCK_TRADE = REGISTRATE.simpleEntityRewardType("unlock_trade", UnlockTradeEntityReward.CODEC, UnlockTradeEntityReward.STREAM_CODEC),
    GOSSIP = REGISTRATE.entityRewardAndInfoTypes("villager_gossip", VillagerGossipEntityReward.CODEC, VillagerGossipEntityReward.Info.CODEC, VillagerGossipEntityReward.Info.STREAM_CODEC),
    ALL_TEAMS = REGISTRATE.entityRewardAndWrappedInfoTypes("all_teams", AllTeamsPlayerReward.CODEC, AllTeamsPlayerReward.Info::new);

    public static final RegistryEntry<ITeamReward.Type, TeamRewardAndInfoType>

    MEMBERS = REGISTRATE.teamRewardAndInfoTypes("members", MembersTeamReward.CODEC, WrappedRewardInfo.codec(MembersTeamReward.Info::new), WrappedRewardInfo.streamCodec(MembersTeamReward.Info::new)),
    GRANT_RESTAURANT_XP = REGISTRATE.teamRewardAndInfoTypes("grant_restaurant_xp", GrantRestaurantXPTeamReward.CODEC, GrantRestaurantXPTeamReward.Info.CODEC, GrantRestaurantXPTeamReward.Info.STREAM_CODEC);
  
    public static final void register() {};
};
