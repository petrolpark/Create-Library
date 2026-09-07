package petrolpark.mc.library.core.data.reward.team;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import petrolpark.mc.library.core.data.reward.IReward;
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.registry.PetrolparkRewardTypes;
import petrolpark.mc.library.util.codec.CodecHelper;

@ParametersAreNonnullByDefault
public record FlatTeamReward(Holder<IReward> rewardHolder) implements ITeamReward {

    public static final MapCodec<FlatTeamReward> CODEC = CodecHelper.singleFieldMap(IReward.CODEC, "reward", FlatTeamReward::rewardHolder, FlatTeamReward::new);
    public static final Codec<FlatTeamReward> INLINE_CODEC = IReward.CODEC.xmap(FlatTeamReward::new, FlatTeamReward::rewardHolder);

    @Override
    public IRewardInfo info() {
        return rewardHolder().value().info();
    };

    @Override
    public TeamRewardType getType() {
        return PetrolparkRewardTypes.TEAM_FLAT.get();
    };

    @Override
    public boolean reward(ITeam team, LootContext context, float multiplier, boolean simulate) {
        return rewardHolder().value().reward(context, multiplier, simulate);
    };
    
};
