package petrolpark.mc.library.core.data.numberProvider;

import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import petrolpark.mc.library.registry.PetrolparkNumberProviderTypes;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

/**
 * <p>{@code petrolpark:max}</p>
 * 
 * Get the maximum of several other {@link NumberProvider}s.
 * 
 * Arguments:
 * <ul>
 * <li> {@code values} - List of {@link NumberProvider}s to maximize 
 * </ul>
 * 
 * @author petrolpark
 */
public class MaxNumberProvider extends FunctionNumberProvider {

    public MaxNumberProvider(List<NumberProvider> children) {
        super(children);
    };

    @Override
    public float applyFloat(LootContext lootContext, DoubleStream childResults) {
        return (float)childResults.max().orElse(0f);
    };

    @Override
    public int applyInt(LootContext lootContext, IntStream childResults) {
        return childResults.max().orElse(0);
    };

    @Override
    public NumberEstimate applyEstimate(Stream<NumberEstimate> estimates) {
        float min = 0f;
        float max = 0f;
        boolean approximate = false;
        for (NumberEstimate estimate : estimates.toList()) {
            if (estimate.unknown()) return estimate;
            min = Math.max(min, estimate.min());
            max = Math.max(max, estimate.max());
            approximate |= estimate.approximate;
        };
        return NumberEstimate.ranged(min, max, approximate);
    };

    @Override
    public LootNumberProviderType getType() {
        return PetrolparkNumberProviderTypes.MAX.get();
    };
    
};
