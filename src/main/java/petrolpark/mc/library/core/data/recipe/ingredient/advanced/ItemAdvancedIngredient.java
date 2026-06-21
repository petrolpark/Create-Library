package petrolpark.mc.library.core.data.recipe.ingredient.advanced;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;
import petrolpark.mc.library.registry.PetrolparkRegistries;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

public interface ItemAdvancedIngredient extends IAdvancedIngredient<ItemStack>, IForcingItemAdvancedIngredient {

    /**
     * Use {@link ItemAdvancedIngredient#CODEC instead}.
     */
    @ApiStatus.Internal
    static final Codec<IAdvancedIngredient<? super ItemStack>> TYPED_CODEC = PetrolparkRegistries.ADVANCED_ITEM_INGREDIENT_TYPES
        .byNameCodec()
        .dispatch(IAdvancedIngredient::getType, IAdvancedIngredientType::codec);

    public static final Codec<IAdvancedIngredient<? super ItemStack>> CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, Codec.unit(PassAdvancedIngredient.INSTANCE)));

    public static final StreamCodec<RegistryFriendlyByteBuf, IAdvancedIngredient<? super ItemStack>> STREAM_CODEC = ByteBufCodecs.registry(PetrolparkRegistries.Keys.ADVANCED_ITEM_INGREDIENT_TYPE)
        .dispatch(IAdvancedIngredient::getType, IAdvancedIngredientType::streamCodec);

    @Override
    public boolean test(ItemStack stack);

    default Component translate(String postfix, Object... translationArgs) {
        return Component.translatable(getType().translationKey() + "." + postfix, translationArgs);
    };

    default Component translateSimple(Object... translationArgs) {
        return Component.translatable(getType().translationKey());
    };

    default Component translateInverse(Object... translationArgs) {
        return translate("inverse", translationArgs);
    };

    @Override
    public default @Nonnull Optional<ItemStack> forceLootItemFunction(LootItemFunction function, LootContext context, ItemStack stack) {
        return Optional.empty();
    };

    @Override
    public default @Nonnull Optional<ItemStack> forbidLootItemFunction(LootItemFunction function, LootContext context, ItemStack stack) {
        return Optional.empty();
    };

    @Override
    public default @Nullable Optional<MerchantOffer> forceTradeListing(ItemListing tradeListing, Entity trader, RandomSource random) {
        return null;
    };

    @Override
    public default @Nullable Optional<MerchantOffer> forbidTradeListing(ItemListing tradeListing, Entity trader, RandomSource random) {
        return null;
    };

    @Override
    public INamedAdvancedIngredientType<ItemStack> getType();
};
