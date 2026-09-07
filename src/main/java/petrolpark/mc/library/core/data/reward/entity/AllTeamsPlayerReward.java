package petrolpark.mc.library.core.data.reward.entity;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;
import petrolpark.mc.library.core.data.reward.info.WrappedRewardInfo;
import petrolpark.mc.library.core.data.reward.team.ITeamReward;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.registry.PetrolparkRewardTypes;
import petrolpark.mc.library.util.codec.CodecHelper;

/**
 * <p>{@code petrolpark:}</p>
 * Issue a {@link ITeamReward} to all {@link ITeam}s of which the Player is a part.
 * 
 * Arguments:
 * <ul>
 * <li> {@code reward} - {@link ITeamReward} to award to all {@link ITeam}s
 * </ul>
 * 
 * @author petrolpark
 */
@ParametersAreNonnullByDefault
public record AllTeamsPlayerReward(Holder<ITeamReward> rewardHolder) implements IPlayerReward, IWrappedEntityReward<ITeamReward> {

    public static final MapCodec<AllTeamsPlayerReward> CODEC = CodecHelper.singleFieldMap(ITeamReward.CODEC, "reward", AllTeamsPlayerReward::rewardHolder, AllTeamsPlayerReward::new);

    @Override
    public boolean rewardPlayer(ServerPlayer player, LootContext context, float multiplier, boolean simulate) {
        if (ITeam.streamAll(player).findAny().isEmpty()) return true; // Not in any teams
        return ITeam.streamAll(player).filter(team -> rewardHolder().value().reward(team, context, multiplier, simulate)).count() > 0l;
    };

    @Override
    public AllTeamsPlayerReward.Info wrapInfo(IRewardInfo info) {
        return new AllTeamsPlayerReward.Info(rewardHolder().value().info());
    };

    public static class Info extends WrappedRewardInfo {

        public Info(IRewardInfo wrapped) {
            super(wrapped);
        };

        @Override
        public EntityRewardAndInfoType getRewardInfoType() {
            return PetrolparkRewardTypes.ENTITY_ALL_TEAMS.get();
        };

    };

    @Override
    public EntityRewardAndInfoType getType() {
        return PetrolparkRewardTypes.ENTITY_ALL_TEAMS.get();
    };
    
};
