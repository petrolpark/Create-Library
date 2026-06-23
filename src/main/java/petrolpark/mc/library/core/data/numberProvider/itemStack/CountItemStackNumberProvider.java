package petrolpark.mc.library.core.data.numberProvider.itemStack;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import petrolpark.mc.library.core.data.numberProvider.NumberEstimate;
import petrolpark.mc.library.registry.PetrolparkNumberProviderTypes;

/**
 * <p>{@code petrolpark:count}</p>
 * 
 * {@link ItemStack#getCount Get the count of the Item Stack.} No arguments.
 * 
 * @author petrolpark
 */
public class CountItemStackNumberProvider implements ItemStackNumberProvider {

    @Override
    public float getFloat(ItemStack stack, LootContext lootContext) {
        return getInt(stack, lootContext);
    };

    @Override
    public int getInt(ItemStack stack, LootContext lootContext) {
        return stack.getCount();
    };

    @Override
    public NumberEstimate getEstimate() {
        return NumberEstimate.ranged(0f, 99f); // 99 is global max stack size
    };

    @Override
    public float getMaxFloat(ItemStack stack, LootContext lootContext) {
        return getFloat(stack, lootContext); 
    };

    @Override
    public LootItemStackNumberProviderType getItemStackNumberProviderType() {
        return PetrolparkNumberProviderTypes.COUNT.get();
    };
    
};
