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
import petrolpark.mc.library.core.data.reward.entity.EntityRewardType;
import petrolpark.mc.library.core.data.reward.entity.FlatEntityReward;
import petrolpark.mc.library.core.data.reward.entity.GrantAdvancementPlayerReward;
import petrolpark.mc.library.core.data.reward.entity.GrantExperiencePlayerReward;
import petrolpark.mc.library.core.data.reward.entity.IEntityReward;
import petrolpark.mc.library.core.data.reward.entity.MobEffectEntityReward;
import petrolpark.mc.library.core.data.reward.entity.UnlockTradeEntityReward;
import petrolpark.mc.library.core.data.reward.entity.VillagerGossipEntityReward;
import petrolpark.mc.library.core.data.reward.info.ConditionalRewardInfo;
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;
import petrolpark.mc.library.core.data.reward.info.RewardInfoType;
import petrolpark.mc.library.core.data.reward.info.WrappedRewardInfo;
import petrolpark.mc.library.core.data.reward.team.ConditionalTeamReward;
import petrolpark.mc.library.core.data.reward.team.FlatTeamReward;
import petrolpark.mc.library.core.data.reward.team.GrantRestaurantXPTeamReward;
import petrolpark.mc.library.core.data.reward.team.ITeamReward;
import petrolpark.mc.library.core.data.reward.team.MembersTeamReward;
import petrolpark.mc.library.core.data.reward.team.OneTimeTeamReward;
import petrolpark.mc.library.core.data.reward.team.TeamRewardAndInfoType;
import petrolpark.mc.library.core.data.reward.team.TeamRewardType;

public class PetrolparkRewardTypes {

    public static final RegistryEntry<IReward.Type, RewardAndInfoType>
    
    CONTEXT_ENTITY = REGISTRATE.rewardAndInfoTypes("to_entity", ContextEntityReward.CODEC, ContextEntityReward.Info.CODEC, ContextEntityReward.Info.STREAM_CODEC),
    CONTEXT_TEAM = REGISTRATE.rewardAndWrappedInfoTypes("to_team", ContextTeamReward.CODEC, ContextTeamReward.Info::new),
    GIVE_ITEM = REGISTRATE.simpleRewardType("give_item", GiveItemReward.CODEC, GiveItemReward.STREAM_CODEC),
    GIVE_LOOT = REGISTRATE.rewardAndInfoTypes("give_loot", GiveLootReward.CODEC, GiveLootReward.Info.CODEC, GiveLootReward.Info.STREAM_CODEC);

    public static final RegistryEntry<IEntityReward.Type, EntityRewardAndInfoType>

    ENTITY_ALL_TEAMS = REGISTRATE.entityRewardAndWrappedInfoTypes("all_teams", AllTeamsPlayerReward.CODEC, AllTeamsPlayerReward.Info::new),
    ENTITY_MOB_EFFECT = REGISTRATE.entityRewardAndInfoTypes("effect", MobEffectEntityReward.CODEC, MobEffectEntityReward.Info.CODEC, MobEffectEntityReward.Info.STREAM_CODEC),
    ENTITY_GOSSIP = REGISTRATE.entityRewardAndInfoTypes("villager_gossip", VillagerGossipEntityReward.CODEC, VillagerGossipEntityReward.Info.CODEC, VillagerGossipEntityReward.Info.STREAM_CODEC),
    ENTITY_GRANT_ADVANCEMENT = REGISTRATE.entityRewardAndInfoTypes("grant_advancement", GrantAdvancementPlayerReward.CODEC, GrantAdvancementPlayerReward.Info.CODEC, GrantAdvancementPlayerReward.Info.STREAM_CODEC),
    ENTITY_GRANT_EXPERIENCE = REGISTRATE.entityRewardAndInfoTypes("grant_experience", GrantExperiencePlayerReward.CODEC, GrantExperiencePlayerReward.Info.CODEC, GrantExperiencePlayerReward.Info.STREAM_CODEC),
    ENTITY_UNLOCK_TRADE = REGISTRATE.simpleEntityRewardType("unlock_trade", UnlockTradeEntityReward.CODEC, UnlockTradeEntityReward.STREAM_CODEC);
    

    public static final RegistryEntry<IEntityReward.Type, EntityRewardType>

    ENTITY_FLAT = REGISTRATE.entityRewardType("flat", FlatEntityReward.CODEC);

    public static final RegistryEntry<ITeamReward.Type, TeamRewardAndInfoType>

    TEAM_MEMBERS = REGISTRATE.teamRewardAndInfoTypes("members", MembersTeamReward.CODEC, WrappedRewardInfo.codec(MembersTeamReward.Info::new), WrappedRewardInfo.streamCodec(MembersTeamReward.Info::new)),
    TEAM_GRANT_RESTAURANT_XP = REGISTRATE.teamRewardAndInfoTypes("grant_restaurant_xp", GrantRestaurantXPTeamReward.CODEC, GrantRestaurantXPTeamReward.Info.CODEC, GrantRestaurantXPTeamReward.Info.STREAM_CODEC);

    public static final RegistryEntry<ITeamReward.Type, TeamRewardType>

    TEAM_CONDITIONAL = REGISTRATE.teamRewardType("conditional", ConditionalTeamReward.CODEC),
    TEAM_FLAT = REGISTRATE.teamRewardType("flat", FlatTeamReward.CODEC),
    TEAM_ONE_TIME = REGISTRATE.teamRewardType("one_time", OneTimeTeamReward.Team.CODEC),
    TEAM_ONE_TIME_PLAYER = REGISTRATE.teamRewardType("one_time_player", OneTimeTeamReward.Player.CODEC);

    public static final RegistryEntry<IRewardInfo.Type, RewardInfoType> 
    
    INFO_CONDITIONAL = REGISTRATE.rewardInfoType("conditional", WrappedRewardInfo.codec(ConditionalRewardInfo::new), WrappedRewardInfo.streamCodec(ConditionalRewardInfo::new)),
    INFO_ONE_TIME_TEAM = REGISTRATE.rewardInfoType("one_time_team", WrappedRewardInfo.codec(OneTimeTeamReward.Info::new), WrappedRewardInfo.streamCodec(OneTimeTeamReward.Info::new));
  
    public static final void register() {};
};
