package petrolpark.mc.library.core.data.reward.team;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;

import net.minecraft.world.level.storage.loot.LootContext;
import petrolpark.mc.library.core.data.reward.ITypedReward;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.registry.PetrolparkRegistries;

@ParametersAreNonnullByDefault
public interface ITeamReward extends ITypedReward<TeamRewardType> {

    /**
     * Use {@link ITeamReward#CODEC} instead.
     */
    static final Codec<ITeamReward> TYPED_CODEC = PetrolparkRegistries.TEAM_REWARD_TYPES
        .byNameCodec()
        .dispatch(ITeamReward::getType, TeamRewardType::codec);

    public static final Codec<ITeamReward> CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, MembersTeamReward.INLINE_CODEC));
    
    public void reward(ITeam team, LootContext context, float multiplier);
};
