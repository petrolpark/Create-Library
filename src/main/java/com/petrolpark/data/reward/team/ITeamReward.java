package com.petrolpark.data.reward.team;

import com.mojang.serialization.Codec;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.data.reward.ITypedReward;
import com.petrolpark.team.ITeam;

import net.minecraft.world.level.storage.loot.LootContext;

public interface ITeamReward extends ITypedReward<TeamRewardType> {

    /**
     * Use {@link ITeamReward#CODEC} instead.
     */
    static final Codec<ITeamReward> TYPED_CODEC = PetrolparkRegistries.TEAM_REWARD_TYPES
        .byNameCodec()
        .dispatch(ITeamReward::getType, TeamRewardType::codec);

    public static final Codec<ITeamReward> CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, MembersTeamReward.INLINE_CODEC));
    
    public void reward(ITeam<?> team, LootContext context, float multiplier);
};
