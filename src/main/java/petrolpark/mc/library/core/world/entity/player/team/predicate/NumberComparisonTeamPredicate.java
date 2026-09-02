package petrolpark.mc.library.core.world.entity.player.team.predicate;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.storage.loot.LootContext;
import petrolpark.mc.library.core.data.loot.condition.NumberComparisonLootCondition.Comparison;
import petrolpark.mc.library.core.data.numberProvider.team.TeamNumberProvider;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.registry.PetrolparkDataSubPredicates;

@ParametersAreNonnullByDefault
public record NumberComparisonTeamPredicate(TeamNumberProvider first, TeamNumberProvider second, Comparison comparison, boolean useInts) implements ITeamPredicate {

    public static final MapCodec<NumberComparisonTeamPredicate> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            TeamNumberProvider.CODEC.fieldOf("first").forGetter(NumberComparisonTeamPredicate::first),
            TeamNumberProvider.CODEC.fieldOf("second").forGetter(NumberComparisonTeamPredicate::second),
            Comparison.CODEC.fieldOf("operation").forGetter(NumberComparisonTeamPredicate::comparison),
            Codec.BOOL.optionalFieldOf("compare_integers", false).forGetter(NumberComparisonTeamPredicate::useInts)
        ).apply(instance, NumberComparisonTeamPredicate::new)
    );

    @Override
    public boolean test(LootContext context, ITeam team) {
        return useInts()
            ? comparison().compareInt(first().getInt(team, context), second().getInt(team, context))
            : comparison().compareFloat(first().getFloat(team, context), second().getFloat(team, context));
    };

    @Override
    public ITeamPredicate.Type getTeamPredicateType() {
        return PetrolparkDataSubPredicates.TEAM_NUMBER_COMPARISON.get();
    };

};
