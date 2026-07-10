package petrolpark.mc.library.core.data.reward.team;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import petrolpark.mc.library.core.data.reward.entity.IEntityReward;
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;
import petrolpark.mc.library.core.data.reward.info.WrappedRewardInfo;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.registry.PetrolparkRewardTypes;

/**
 * Rewards a proportion of members of a {@link ITeam} with an {@link IEntityReward}.
 */
@ParametersAreNonnullByDefault
public record MembersTeamReward(IEntityReward reward, Either<NumberProvider, NumberProvider> who, boolean random) implements ITeamReward {

    public static final MapCodec<MembersTeamReward> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        IEntityReward.CODEC.fieldOf("reward").forGetter(MembersTeamReward::reward),
        Codec.mapEither(
            NumberProviders.CODEC.fieldOf("count"),
            NumberProviders.CODEC.optionalFieldOf("proportion", ConstantValue.exactly(1f))
        ).forGetter(MembersTeamReward::who),
        Codec.BOOL.optionalFieldOf("random", false).forGetter(MembersTeamReward::random)
    ).apply(instance, MembersTeamReward::new));

    public static final Codec<MembersTeamReward> INLINE_CODEC = IEntityReward.CODEC.xmap(entityReward -> new MembersTeamReward(entityReward, Either.right(ConstantValue.exactly(1f)), false), MembersTeamReward::reward);

    @Override
    public boolean reward(ITeam team, LootContext context, float multiplier, boolean simulate) {
        int count = who().map(
            absoluteCount -> 
                Mth.clamp(absoluteCount.getInt(context), 0, team.memberCount()),
            proportion -> 
                (int)((Mth.clamp(proportion.getFloat(context), 0f, 1f) * team.memberCount()))
        );
        if (count == 0) return true;
        final List<Player> members = team.streamMembers().collect(Collectors.toList());
        if (count < team.memberCount() && random()) Collections.shuffle(members);
        boolean success = true;
        for (int i = 0; i < count && i < members.size(); i++) {
            if (!reward().reward(members.get(i), context, multiplier, simulate)) success = false;
        };
        return success;
    };

    @Override
    public MembersTeamReward.Info info() {
        return new MembersTeamReward.Info(reward().info());
    };

    public static class Info extends WrappedRewardInfo {

        public Info(IRewardInfo wrapped) {
            super(wrapped);
        };

        @Override
        public TeamRewardAndInfoType getRewardInfoType() {
            return PetrolparkRewardTypes.MEMBERS.get();
        };

    };

    @Override
    public TeamRewardAndInfoType getType() {
        return PetrolparkRewardTypes.MEMBERS.get();
    };

    @Override
    public void validate(ValidationContext context) {
        ITeamReward.super.validate(context);
        reward().validate(context.forChild(".member_reward"));
    };
    
};
