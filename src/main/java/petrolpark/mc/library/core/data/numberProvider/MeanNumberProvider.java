package petrolpark.mc.library.core.data.numberProvider;

import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import petrolpark.mc.library.registry.PetrolparkNumberProviderTypes;

/**
 * <p>{@code petrolpark:mean}</p>
 * 
 * Get the arithmetic mean of several other {@link NumberProvider}s.
 * 
 * Arguments:
 * <ul>
 * <li> {@code values} - List of {@link NumberProvider}s to average 
 * </ul>
 * 
 * @author petrolpark
 */
@ParametersAreNonnullByDefault
public class MeanNumberProvider extends FunctionNumberProvider {

    public MeanNumberProvider(List<NumberProvider> children) {
        super(children);
    };

    @Override
    public LootNumberProviderType getType() {
        return PetrolparkNumberProviderTypes.MEAN.get();
    };

    @Override
    public float applyFloat(LootContext lootContext, DoubleStream childResults) {
        return (float)childResults.average().orElse(0d);
    };

    @Override
    public int applyInt(LootContext lootContext, IntStream childResults) {
        return (int)Math.round(childResults.average().orElse(0));
    };

    @Override
    public NumberEstimate applyEstimate(Stream<NumberEstimate> estimatesStream) {
        final List<NumberEstimate> estimates = estimatesStream.toList();
        if (estimates.size() == 0) return NumberEstimate.ZERO;
        float min = 0f, max = 0f;
        boolean approximate = false;
        for (NumberEstimate estimate : estimates) {
            if (estimate.unknown()) return estimate;
            min += estimate.min();
            max += estimate.max();
            approximate |= estimate.approximate();
        };
        return NumberEstimate.ranged(min / estimates.size(), max / estimates.size(), approximate);
    };
    
};
