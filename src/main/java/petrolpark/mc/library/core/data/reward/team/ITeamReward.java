package petrolpark.mc.library.core.data.reward.team;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.storage.loot.LootContext;
import petrolpark.mc.library.core.data.reward.IAbstractReward;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.registry.PetrolparkRegistries;

@ParametersAreNonnullByDefault
public interface ITeamReward extends IAbstractReward<ITeamReward.Type> {

    /**
     * Use {@link ITeamReward#DIRECT_CODEC} instead.
     */
    static final Codec<ITeamReward> TYPED_CODEC = PetrolparkRegistries.TEAM_REWARD_TYPES
        .byNameCodec()
        .dispatch("team_reward_type", ITeamReward::getType, ITeamReward.Type::teamRewardCodec);

    public static final Codec<ITeamReward> DIRECT_CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, MembersTeamReward.INLINE_CODEC));

    public static final Codec<Holder<ITeamReward>> CODEC = RegistryFileCodec.create(PetrolparkRegistries.Keys.TEAM_REWARD, DIRECT_CODEC);
    
    public boolean reward(ITeam team, LootContext context, float multiplier, boolean simulate);

    public interface Type {

        public MapCodec<? extends ITeamReward> teamRewardCodec();
    };
};
