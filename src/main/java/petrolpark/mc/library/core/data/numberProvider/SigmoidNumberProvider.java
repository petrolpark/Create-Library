package petrolpark.mc.library.core.data.numberProvider;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import petrolpark.mc.library.registry.PetrolparkNumberProviderTypes;

/**
 * <p>{@code petrolpark:sigmoid}</p>
 * 
 * Get the output of a sigmoid whose inputs are other {@link NumberProvider}s.
 * 
 * Arguments:
 * <ul>
 * <li> {@code shallowness} - A {@link NumberProvider}
 * <li> {@code midpoint} - A {@link NumberProvider}
 * <li> {@code value} - A {@link NumberProvider}
 * </ul>
 * 
 * The output is then {@code 1 / (1 + exp((midpoint - value) / shallowness))}.
 * 
 * @author petrolpark
 */
@ParametersAreNonnullByDefault
public record SigmoidNumberProvider(NumberProvider shallowness, NumberProvider midpoint, NumberProvider value) implements IEstimableNumberProvider {

    public static final MapCodec<SigmoidNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        NumberProviders.CODEC.fieldOf("shallowness").forGetter(SigmoidNumberProvider::shallowness),
        NumberProviders.CODEC.fieldOf("midpoint").forGetter(SigmoidNumberProvider::midpoint),
        NumberProviders.CODEC.fieldOf("value").forGetter(SigmoidNumberProvider::value)
    ).apply(instance, SigmoidNumberProvider::new));

    @Override
    public float getFloat(LootContext lootContext) {
        float shallowness = this.shallowness.getFloat(lootContext);
        if (shallowness == 0f) return 1f;
        return 1f / (1f + (float)Math.exp((midpoint.getFloat(lootContext) - value.getFloat(lootContext)) / shallowness));
    };

    @Override
    public float getMaxFloat(LootContext context) {
        return Float.MAX_VALUE;
    };
    
    @Override
    public NumberEstimate getEstimate() {
        return NumberEstimate.ONE.add(
            NumberEstimate.get(midpoint()).subtract(NumberEstimate.get(value())).divide(NumberEstimate.get(shallowness())).exp()
        ).reciprocal();
    };

    @Override
    public LootNumberProviderType getType() {
        return PetrolparkNumberProviderTypes.SIGMOID.get();
    };

    @Override
    public void validate(ValidationContext context) {
        IEstimableNumberProvider.super.validate(context);
        shallowness().validate(context.forChild(".shallowness"));
        midpoint().validate(context.forChild(".midpoint"));
        value().validate(context.forChild(".value"));
    };
    
};
