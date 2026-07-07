package petrolpark.mc.library.core.data.numberProvider;

import java.util.List;

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
 * <p>{@code petrolpark:polynomial}</p>
 * 
 * Get the value of a polynomial whose input and coefficients are other {@link NumberProvider}s.
 * 
 * Arguments:
 * <ul>
 * <li> {@code value} - A {@link NumberProvider} to act as input ("x") to the polynomial
 * <li> {@code coefficients} - A list of {@link NumberProvider}s which are the ascending ordered coefficients of the polynomial, including the constant
 * </ul>
 * 
 * @author petrolpark
 */
@ParametersAreNonnullByDefault
public record PolynomialNumberProvider(NumberProvider value, List<NumberProvider> coefficients) implements IEstimableNumberProvider {

    public static final MapCodec<PolynomialNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        NumberProviders.CODEC.fieldOf("value").forGetter(PolynomialNumberProvider::value),
        NumberProviders.CODEC.listOf().fieldOf("coefficients").forGetter(PolynomialNumberProvider::coefficients)
    ).apply(instance, PolynomialNumberProvider::new));

    @Override
    public float getFloat(LootContext lootContext) {
        float total = 0f;
        float value = value().getFloat(lootContext);
        int power = 0;
        for (NumberProvider coeff : coefficients()) {
            total += coeff.getFloat(lootContext) * Math.pow(value, power);
            power++;
        };
        return total;
    };

    @Override
    public int getInt(LootContext lootContext) {
        int total = 0;
        int value = value().getInt(lootContext);
        int power = 0;
        for (NumberProvider coeff : coefficients()) {
            total += coeff.getInt(lootContext) * (int)Math.pow(value, power);
            power++;
        };
        return total;
    };
    
    @Override
    public NumberEstimate getEstimate() {
        NumberEstimate total = NumberEstimate.ZERO;
        NumberEstimate value = NumberEstimate.get(value());
        int power = 0;
        for (NumberProvider coeff : coefficients()) {
            total = total.add(NumberEstimate.get(coeff).multiply(value.pow(power)));
            power++;
        };
        return total;
    };

    @Override
    public float getMaxFloat(LootContext context) {
        float total = 0f;
        float value = NumberEstimate.getMax(context, value()); //TODO scrap
        int power = 0;
        for (NumberProvider coeff : coefficients()) {
            total += NumberEstimate.getMax(context, coeff) * Math.pow(value, power);
            power++;
        };
        return total;
    };

    @Override
    public LootNumberProviderType getType() {
        return PetrolparkNumberProviderTypes.POLYNOMIAL.get();
    };

    @Override
    public void validate(ValidationContext context) {
        IEstimableNumberProvider.super.validate(context);
        value().validate(context.forChild(".value"));
        for (int i = 0; i < coefficients().size(); i++) {
            coefficients().get(i).validate(context.forChild(".coefficient[" + i + "]"));
        };
    };
    
};
