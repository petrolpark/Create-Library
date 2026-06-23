package petrolpark.mc.library.core.data.numberProvider;

import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import petrolpark.mc.library.registry.PetrolparkNumberProviderTypes;

/**
 * <p>{@code petrolpark:min}</p>
 * 
 * Get the minimum of several other {@link NumberProvider}s.
 * 
 * Arguments:
 * <ul>
 * <li> {@code values} - List of {@link NumberProvider}s to minimize 
 * </ul>
 * 
 * @author petrolpark
 */
public class MinNumberProvider extends FunctionNumberProvider {

    public MinNumberProvider(List<NumberProvider> children) {
        super(children);
    };

    @Override
    public float applyFloat(LootContext lootContext, DoubleStream childResults) {
        return (float)childResults.min().orElse(0d);
    };

    @Override
    public int applyInt(LootContext lootContext, IntStream childResults) {
        return childResults.min().orElse(0);
    };

    @Override
    public NumberEstimate applyEstimate(Stream<NumberEstimate> estimates) {
        float min = 0f;
        float max = 0f;
        boolean approximate = false;
        for (NumberEstimate estimate : estimates.toList()) {
            if (estimate.unknown()) return estimate;
            min = Math.min(min, estimate.min());
            max = Math.min(max, estimate.max());
            approximate |= estimate.approximate;
        };
        return NumberEstimate.ranged(min, max, approximate);
    };

    @Override
    public LootNumberProviderType getType() {
        return PetrolparkNumberProviderTypes.MIN.get();
    };
    
};
