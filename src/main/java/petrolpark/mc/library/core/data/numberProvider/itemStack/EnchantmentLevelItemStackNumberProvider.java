package petrolpark.mc.library.core.data.numberProvider.itemStack;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import petrolpark.mc.library.core.data.numberProvider.NumberEstimate;
import petrolpark.mc.library.registry.PetrolparkNumberProviderTypes;
import petrolpark.mc.library.util.codec.CodecHelper;

/**
 * <p>{@code petrolpark:enchantment_level}</p>
 * 
 * Get the level of the given Enchantment ({@code 0} if the Item Stack does not have that Enchantment).
 * 
 * <ul>
 * <li> {@code enchantment} - The ID of the Enchantment
 * </ul>
 * 
 * @author petrolpark
 */
public record EnchantmentLevelItemStackNumberProvider(Holder<Enchantment> enchantment) implements ItemStackNumberProvider {

    public static final MapCodec<EnchantmentLevelItemStackNumberProvider> CODEC = CodecHelper.singleFieldMap(Enchantment.CODEC, "enchantment", EnchantmentLevelItemStackNumberProvider::enchantment, EnchantmentLevelItemStackNumberProvider::new);

    @Override
    public float getFloat(ItemStack stack, LootContext lootContext) {
        return stack.getEnchantmentLevel(enchantment);
    };

    @Override
    public NumberEstimate getEstimate() {
        return NumberEstimate.ranged(0f, enchantment().value().getMaxLevel(), false);
    };

    @Override
    public float getMaxFloat(ItemStack stack, LootContext lootContext) {
        return getFloat(stack, lootContext);
    };

    @Override
    public LootItemStackNumberProviderType getItemStackNumberProviderType() {
        return PetrolparkNumberProviderTypes.ENCHANTMENT_LEVEL.get();
    };
    
};
