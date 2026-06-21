package petrolpark.mc.library.core.data.loot.numberprovider;

import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import petrolpark.mc.library.core.data.loot.numberprovider.entity.EntityPredicateNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.itemstack.ItemStackNumberProvider;
import petrolpark.mc.library.registry.PetrolparkNumberProviderTypes;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

/**
 * <p>{@code petrolpark:conditional}</p>
 * 
 * Choose between two {@link NumberProvider}s based on whether a {@link LootItemCondition} passes.
 * 
 * Arguments:
 * <ul>
 * <li> {@code condition} - {@link LootItemCondition} to test
 * <li> {@code pass} - {@link NumberProvider} to call if the condition is met
 * <li> {@code fail} - {@link NumberProvider} to call if the condition is not met
 * </ul>
 * 
 * @author petrolpark
 * 
 * @see EntityPredicateNumberProvider Entity equivalent
 * @see ItemStackNumberProvider Item Stack equivalent
 */
public record ConditionalNumberProvider(LootItemCondition condition, NumberProvider pass, NumberProvider fail) implements IEstimableNumberProvider {

    public static final MapCodec<ConditionalNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        LootItemCondition.DIRECT_CODEC.fieldOf("condition").forGetter(ConditionalNumberProvider::condition),
        NumberProviders.CODEC.fieldOf("pass").forGetter(ConditionalNumberProvider::pass),
        NumberProviders.CODEC.fieldOf("fail").forGetter(ConditionalNumberProvider::fail)
    ).apply(instance, ConditionalNumberProvider::new));

    public NumberProvider get(LootContext context) {
        return condition().test(context) ? pass() : fail();
    };

    @Override
    public float getFloat(@Nonnull LootContext lootContext) {
        return get(lootContext).getFloat(lootContext);
    };

    @Override
    public int getInt(@Nonnull LootContext lootContext) {
        return get(lootContext).getInt(lootContext);
    };

    @Override
    public LootNumberProviderType getType() {
        return PetrolparkNumberProviderTypes.CONDITIONAL.get();
    };

    @Override
    public NumberEstimate getEstimate() {
        return NumberEstimate.get(pass()).or(NumberEstimate.get(fail()));
    };

    @Override
    public float getMaxFloat(LootContext context) {
        return NumberEstimate.getMax(context, get(context));
    };

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Sets.union(condition().getReferencedContextParams(), Sets.union(pass().getReferencedContextParams(), fail().getReferencedContextParams()));
    };
    
};
