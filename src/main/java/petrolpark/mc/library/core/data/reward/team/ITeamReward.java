package petrolpark.mc.library.core.data.reward.team;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.world.level.storage.loot.LootContext;
import petrolpark.mc.library.core.data.reward.IAbstractReward;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.registry.PetrolparkRegistries;

@ParametersAreNonnullByDefault
public interface ITeamReward extends IAbstractReward<ITeamReward.Type> {

    /**
     * Use {@link ITeamReward#CODEC} instead.
     */
    static final Codec<ITeamReward> TYPED_CODEC = PetrolparkRegistries.TEAM_REWARD_TYPES
        .byNameCodec()
        .dispatch("team_reward_type", ITeamReward::getType, ITeamReward.Type::teamRewardCodec);

    public static final Codec<ITeamReward> CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, MembersTeamReward.INLINE_CODEC));
    
    public boolean reward(ITeam team, LootContext context, float multiplier, boolean simulate);

    public interface Type {

        public MapCodec<? extends ITeamReward> teamRewardCodec();
    };
};
