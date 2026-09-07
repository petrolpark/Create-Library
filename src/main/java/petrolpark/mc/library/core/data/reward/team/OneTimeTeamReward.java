package petrolpark.mc.library.core.data.reward.team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import petrolpark.mc.library.core.data.reward.IAbstractReward;
import petrolpark.mc.library.core.data.reward.entity.IEntityReward;
import petrolpark.mc.library.core.data.reward.info.INamedRewardInfo;
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;
import petrolpark.mc.library.core.data.reward.info.WrappedRewardInfo;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.registry.PetrolparkDataComponentTypes;
import petrolpark.mc.library.registry.PetrolparkRewardTypes;
import petrolpark.mc.library.util.DataValidationHelper;

@ParametersAreNonnullByDefault
public interface OneTimeTeamReward extends ITeamReward {

    public static final Codec<Set<ResourceLocation>> IDS_CODEC = Codec.list(ResourceLocation.CODEC).xmap(HashSet::new, ArrayList::new);

    public ResourceLocation id();

    public Holder<? extends IAbstractReward<?>> oneTimeRewardHolder();

    @Override
    public default boolean reward(ITeam team, LootContext context, float multiplier, boolean simulate) {
        final Set<ResourceLocation> existingAwards = team.getOrDefault(PetrolparkDataComponentTypes.TEAM_ONE_TIME_REWARDS, Collections.emptySet());
        if (existingAwards.contains(id())) return true;
        final boolean success = rewardInner(team, context, multiplier, simulate);
        if (success && !simulate) {
            final Set<ResourceLocation> newAwards = new HashSet<>(existingAwards);
            newAwards.add(id());
            team.set(PetrolparkDataComponentTypes.TEAM_ONE_TIME_REWARDS, newAwards);
        };
        return success;
    };

    public boolean rewardInner(ITeam team, LootContext context, float multiplier, boolean simulate);

    public record Team(ResourceLocation id, Holder<ITeamReward> oneTimeRewardHolder) implements OneTimeTeamReward {

        public static final MapCodec<OneTimeTeamReward.Team> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(OneTimeTeamReward::id),
            ITeamReward.CODEC.fieldOf("reward").forGetter(OneTimeTeamReward.Team::oneTimeRewardHolder)
        ).apply(instance, OneTimeTeamReward.Team::new));

        @Override
        public boolean rewardInner(ITeam team, LootContext context, float multiplier, boolean simulate) {
            return oneTimeRewardHolder().value().reward(team, context, multiplier, simulate);
        };

        @Override
        public TeamRewardType getType() {
            return PetrolparkRewardTypes.TEAM_ONE_TIME.get();
        };

    };

    public record Player(ResourceLocation id, Holder<IEntityReward> oneTimeRewardHolder, boolean mustBeAdmin) implements OneTimeTeamReward {

        public static final MapCodec<OneTimeTeamReward.Player> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(OneTimeTeamReward::id),
            IEntityReward.CODEC.fieldOf("reward").forGetter(OneTimeTeamReward.Player::oneTimeRewardHolder),
            Codec.BOOL.fieldOf("must_be_admin").forGetter(OneTimeTeamReward.Player::mustBeAdmin)
        ).apply(instance, OneTimeTeamReward.Player::new));

        @Override
        public boolean rewardInner(ITeam team, LootContext context, float multiplier, boolean simulate) {
            return team.streamOnlineMembers().filter(player -> team.isAdmin(player))
                .findAny()
                .or(mustBeAdmin ? Optional::empty : () -> team.streamOnlineMembers().findAny())
                .map(player -> oneTimeRewardHolder().value().reward(player, context, multiplier, simulate))
                .orElse(false);
        };

        @Override
        public TeamRewardType getType() {
            return PetrolparkRewardTypes.TEAM_ONE_TIME.get();
        };

    };

    @Override
    public default IRewardInfo info() {
        return new OneTimeTeamReward.Info(oneTimeRewardHolder().value().info());
    };

    public static class Info extends WrappedRewardInfo {

        public Info(IRewardInfo wrapped) {
            super(wrapped);
        };

        @Override
        public INamedRewardInfo.Type getRewardInfoType() {
            return PetrolparkRewardTypes.INFO_ONE_TIME_TEAM.get();
        };

    };

    @Override
    public default void validate(ValidationContext context) {
        ITeamReward.super.validate(context);
        DataValidationHelper.validateHolder(oneTimeRewardHolder(), context, ".oneTimeReward");
    };
};
