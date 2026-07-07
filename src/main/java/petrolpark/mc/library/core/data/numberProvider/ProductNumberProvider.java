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
 * <p>{@code petrolpark:product}</p>
 * 
 * Get the product of several other {@link NumberProvider}s.
 * 
 * Arguments:
 * <ul>
 * <li> {@code values} - List of {@link NumberProvider}s to multiply 
 * </ul>
 * 
 * @author petrolpark
 */
@ParametersAreNonnullByDefault
public class ProductNumberProvider extends FunctionNumberProvider {

    public ProductNumberProvider(List<NumberProvider> children) {
        super(children);
    };

    @Override
    public float applyFloat(LootContext lootContext, DoubleStream children) {
        return (float)children.reduce(1d, (a, b) -> a * b);
    };

    @Override
    public int applyInt(LootContext lootContext, IntStream childResults) {
        return childResults.reduce(1, (a, b) -> a * b);
    };

    @Override
    public NumberEstimate applyEstimate(Stream<NumberEstimate> estimates) {
        return estimates.reduce(NumberEstimate::multiply).orElse(NumberEstimate.UNKNOWN);
    };

    @Override
    public LootNumberProviderType getType() {
        return PetrolparkNumberProviderTypes.PRODUCT.get();
    };
    
};
