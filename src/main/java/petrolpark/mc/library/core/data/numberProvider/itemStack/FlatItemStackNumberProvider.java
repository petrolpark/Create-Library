package petrolpark.mc.library.core.data.numberProvider.itemStack;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import petrolpark.mc.library.core.data.numberProvider.NumberEstimate;
import petrolpark.mc.library.registry.PetrolparkNumberProviderTypes;
import petrolpark.mc.library.util.codec.CodecHelper;

/**
 * <p>{@code petrolpark:flat}</p>
 * 
 * Get a simple {@link NumberProvider} value, without reference to the Item Stack.
 * 
 * Arguments:
 * <ul>
 * <li> {@code value} - A {@link NumberProvider}
 * </ul>
 * 
 * Note that this has inline serialization. You can just refer to a {@link NumberProvider} directly when an {@link ItemStackNumberProvider} is expected and it will be converted.
 * 
 * @author petrolpark
 */
@ParametersAreNonnullByDefault
public record FlatItemStackNumberProvider(NumberProvider numberProvider) implements ItemStackNumberProvider {

    public static final MapCodec<FlatItemStackNumberProvider> CODEC = CodecHelper.singleFieldMap(NumberProviders.CODEC, "provider", FlatItemStackNumberProvider::numberProvider, FlatItemStackNumberProvider::new);

    @Override
    public float getFloat(ItemStack ItemStack, LootContext lootContext) {
        return numberProvider().getFloat(lootContext);
    };

    @Override
    public NumberEstimate getEstimate() {
        return NumberEstimate.get(numberProvider());
    };

    @Override
    public LootItemStackNumberProviderType getItemStackNumberProviderType() {
        return PetrolparkNumberProviderTypes.FLAT_ITEM_STACK.get();
    };

    @Override
    public void validate(ValidationContext context) {
        ItemStackNumberProvider.super.validate(context);
        numberProvider().validate(context.forChild(".flat_provider"));
    };
    
};
