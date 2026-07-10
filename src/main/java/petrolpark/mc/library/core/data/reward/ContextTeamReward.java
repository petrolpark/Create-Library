package petrolpark.mc.library.core.data.reward;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;

import net.minecraft.world.level.storage.loot.LootContext;
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;
import petrolpark.mc.library.core.data.reward.info.WrappedRewardInfo;
import petrolpark.mc.library.core.data.reward.team.ITeamReward;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.registry.PetrolparkLootContextParams;
import petrolpark.mc.library.registry.PetrolparkRewardTypes;
import petrolpark.mc.library.util.codec.CodecHelper;

@ParametersAreNonnullByDefault
public record ContextTeamReward(ITeamReward reward) implements IWrappedReward<ITeamReward> {

    public static final MapCodec<ContextTeamReward> CODEC = CodecHelper.singleFieldMap(ITeamReward.CODEC, "reward", ContextTeamReward::reward, ContextTeamReward::new);

    @Override
    public boolean reward(LootContext context, float multiplier, boolean simulate) {
        final ITeam team = context.getParam(PetrolparkLootContextParams.TEAM);
        return team == null ? false : reward().reward(team, context, multiplier, simulate);
    };

    @Override
    public WrappedRewardInfo wrapInfo(IRewardInfo info) {
        return new ContextTeamReward.Info(info);
    };

    public static class Info extends WrappedRewardInfo {

        public Info(IRewardInfo wrapped) {
            super(wrapped);
        };

        @Override
        public RewardAndInfoType getRewardInfoType() {
            return PetrolparkRewardTypes.CONTEXT_TEAM.get();
        };

    };

    @Override
    public RewardAndInfoType getType() {
        return PetrolparkRewardTypes.CONTEXT_TEAM.get();
    };
    
};
