package petrolpark.mc.library.core.data.recipe.ingredient.advanced;

import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.core.flags.Flag;
import petrolpark.mc.library.core.flags.IFlagPole;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;
import petrolpark.mc.library.util.codec.CodecHelper;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.neoforged.neoforge.common.MutableDataComponentHolder;

public record FlaggedAdvancedIngredient(Holder<Flag> flag) implements IAdvancedIngredient<MutableDataComponentHolder>, IForcingItemAdvancedIngredient {

    public static final MapCodec<FlaggedAdvancedIngredient> CODEC = CodecHelper.singleFieldMap(Flag.CODEC, "flag", FlaggedAdvancedIngredient::flag, FlaggedAdvancedIngredient::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, FlaggedAdvancedIngredient> STREAM_CODEC = StreamCodec.composite(Flag.STREAM_CODEC, FlaggedAdvancedIngredient::flag, FlaggedAdvancedIngredient::new);
    public static final IAdvancedIngredientType<MutableDataComponentHolder> TYPE = new Type();

    @Override
    public boolean test(MutableDataComponentHolder stack) {
        return IFlagPole.get(stack).map(flags -> flags.has(flag)).orElse(false);
    };

    @Override
    public Stream<MutableDataComponentHolder> modifyExamples(Stream<MutableDataComponentHolder> exampleStacks) {
        return exampleStacks.map(stack -> {
            IFlagPole.get(stack).ifPresent(c -> c.flag(flag));
            return stack;
        });
    };

    @Override
    public Stream<MutableDataComponentHolder> modifyCounterExamples(Stream<MutableDataComponentHolder> counterExampleStacks) {
        return counterExampleStacks.map(stack -> {
            IFlagPole.get(stack).ifPresent(c -> c.unflagOnly(flag));
            return stack;
        });
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder description) {
        description.add(Flag.getNameColored(flag));
    };

    @Override
    public void addToCounterDescription(IndentedTooltipBuilder description) {
        description.add(Flag.getAbsentNameColored(flag));
    };

    @Override
    public @Nonnull Optional<ItemStack> forceLootItemFunction(LootItemFunction function, LootContext context, ItemStack stack) {
        // TODO Auto-generated method stub
        return Optional.empty();
    };

    @Override
    public @Nonnull Optional<ItemStack> forbidLootItemFunction(LootItemFunction function, LootContext context, ItemStack stack) {
        // TODO Auto-generated method stub
        return Optional.empty();
    };

    @Override
    public @Nullable Optional<MerchantOffer> forceTradeListing(ItemListing tradeListing, Entity trader, RandomSource random) {
        // TODO Auto-generated method stub
        return null;
    };

    @Override
    public @Nullable Optional<MerchantOffer> forbidTradeListing(ItemListing tradeListing, Entity trader, RandomSource random) {
        // TODO Auto-generated method stub
        return null;
    };

    @Override
    public IAdvancedIngredientType<MutableDataComponentHolder> getType() {
        return TYPE;
    };

    public static final class Type implements IAdvancedIngredientType<MutableDataComponentHolder> {

        @Override
        public MapCodec<FlaggedAdvancedIngredient> codec() {
            return CODEC;
        };

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FlaggedAdvancedIngredient> streamCodec() {
            return STREAM_CODEC;
        };

        @Override
        public Stream<FlaggedAdvancedIngredient> streamApplicableIngredients(Level level, MutableDataComponentHolder stack) {
            return IFlagPole.get(stack).stream()
                .flatMap(IFlagPole::streamAllFlags)
                .map(FlaggedAdvancedIngredient::new);
        };

    };
    
};
