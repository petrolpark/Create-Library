package petrolpark.mc.library.core.data.numberProvider.itemStack;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import petrolpark.mc.library.core.data.numberProvider.ContextToolNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.NumberEstimate;
import petrolpark.mc.library.core.data.numberProvider.entity.EntityNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.team.TeamNumberProvider;
import petrolpark.mc.library.registry.PetrolparkRegistries;

/**
 * Item Stack-specific version of {@link NumberProvider}.
 * 
 * @see ContextToolNumberProvider Get a value from the tool Item Stack in the LootContext
 * @see EntityNumberProvider Entity equivalent
 * @see TeamNumberProvider Team equivalent
 */
public interface ItemStackNumberProvider extends LootContextUser {

    /**
     * Use {@link ItemStackNumberProvider#CODEC} instead.
     */
    @ApiStatus.Internal
    static final Codec<ItemStackNumberProvider> TYPED_CODEC = PetrolparkRegistries.LOOT_ITEM_STACK_NUMBER_PROVIDER_TYPES
        .byNameCodec()
        .dispatch(ItemStackNumberProvider::getItemStackNumberProviderType, LootItemStackNumberProviderType::codec);

    public static final Codec<ItemStackNumberProvider> CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, NumberProviders.CODEC.xmap(FlatItemStackNumberProvider::new, FlatItemStackNumberProvider::numberProvider)));
    
    public float getFloat(ItemStack stack, LootContext lootContext);

    public default int getInt(ItemStack stack, LootContext lootContext) {
        return Math.round(getFloat(stack, lootContext));
    }

    public default float getMaxFloat(ItemStack stack, LootContext lootContext) {
        return getFloat(stack, lootContext);
    };

    /**
     * Get the approximate bounds for the {@link ItemStackNumberProvider#getFloat(ItemStack, LootContext) output} of this {@link ItemStackNumberProvider} on a best-effort basis.
     * @see NumberEstimate#unknown() 
     */
    public NumberEstimate getEstimate();

    public LootItemStackNumberProviderType getItemStackNumberProviderType();
};
