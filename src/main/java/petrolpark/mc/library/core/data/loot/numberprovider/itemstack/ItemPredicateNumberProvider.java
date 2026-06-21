package petrolpark.mc.library.core.data.loot.numberprovider.itemstack;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import petrolpark.mc.library.core.data.loot.numberprovider.ConditionalNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.NumberEstimate;
import petrolpark.mc.library.core.data.loot.numberprovider.entity.EntityPredicateNumberProvider;
import petrolpark.mc.library.registry.PetrolparkNumberProviderTypes;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

/**
 * <p>{@code petrolpark:predicate}</p>
 * 
 * Choose between two {@link ItemStackNumberProvider}s based on whether a {@link ItemPredicate} passes.
 * 
 * Arguments:
 * <ul>
 * <li> {@code predicate} - {@link ItemPredicate} to test
 * <li> {@code pass} - {@link ItemStackNumberProvider} or {@link NumberProvider} to call if the predicate is met
 * <li> {@code fail} - {@link ItemStackNumberProvider} or {@link NumberProvider} to call if the predicate is not met
 * </ul>
 * 
 * @author petrolpark
 * 
 * @see ConditionalNumberProvider Generic equivalent
 * @see EntityPredicateNumberProvider Entity equivalent
 * 
 */
public record ItemPredicateNumberProvider(ItemPredicate predicate, ItemStackNumberProvider pass, ItemStackNumberProvider fail) implements ItemStackNumberProvider {
    
    public static final MapCodec<ItemPredicateNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ItemPredicate.CODEC.fieldOf("predicate").forGetter(ItemPredicateNumberProvider::predicate),
        ItemStackNumberProvider.CODEC.fieldOf("pass").forGetter(ItemPredicateNumberProvider::pass),
        ItemStackNumberProvider.CODEC.fieldOf("fail").forGetter(ItemPredicateNumberProvider::fail)
    ).apply(instance, ItemPredicateNumberProvider::new));

    @Override
    public float getFloat(ItemStack stack, LootContext lootContext) {
        return (predicate().test(stack) ? pass : fail).getFloat(stack, lootContext);
    };

    @Override
    public NumberEstimate getEstimate() {
        return pass.getEstimate().or(fail.getEstimate());
    };

    @Override
    public LootItemStackNumberProviderType getItemStackNumberProviderType() {
        return PetrolparkNumberProviderTypes.ITEM_PREDICATE.get();
    };
};
