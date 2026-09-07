package petrolpark.mc.library.core.data.reward.team;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import petrolpark.mc.library.core.data.reward.info.ConditionalRewardInfo;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.core.world.entity.player.team.predicate.ITeamPredicate;
import petrolpark.mc.library.registry.PetrolparkRewardTypes;
import petrolpark.mc.library.util.DataValidationHelper;

@ParametersAreNonnullByDefault
public record ConditionalTeamReward(ITeamPredicate predicate, Holder<ITeamReward> rewardHolder) implements ITeamReward {

    public static final MapCodec<ConditionalTeamReward> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ITeamPredicate.DIRECT_CODEC.fieldOf("predicate").forGetter(ConditionalTeamReward::predicate),
        ITeamReward.CODEC.fieldOf("reward").forGetter(ConditionalTeamReward::rewardHolder)  
    ).apply(instance, ConditionalTeamReward::new));

    @Override
    public ConditionalRewardInfo info() {
        return new ConditionalRewardInfo(rewardHolder().value().info());
    };

    @Override
    public TeamRewardType getType() {
        return PetrolparkRewardTypes.TEAM_CONDITIONAL.get();
    };

    @Override
    public boolean reward(ITeam team, LootContext context, float multiplier, boolean simulate) {
        return predicate().test(context, team) ? rewardHolder().value().reward(team, context, multiplier, simulate) : false;
    };

    @Override
    public void validate(ValidationContext context) {
        ITeamReward.super.validate(context);
        predicate().validate(context.forChild(".predicate"));
        DataValidationHelper.validateHolder(rewardHolder(), context, ".conditionalReward");
    };
    
};
