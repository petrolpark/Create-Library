package petrolpark.mc.library.core.data.recipe.ingredient.advanced;

import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.Holder;
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
import petrolpark.mc.library.core.flags.Flag;
import petrolpark.mc.library.core.flags.IFlagPole;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;
import petrolpark.mc.library.util.codec.CodecHelper;

public record FlaggedAdvancedIngredient<STACK extends MutableDataComponentHolder>(Holder<Flag> flag) implements ITypelessAdvancedIngredient<STACK>, IForcingItemAdvancedIngredient {

    @Override
    public boolean test(MutableDataComponentHolder stack) {
        return IFlagPole.get(stack).map(flags -> flags.has(flag)).orElse(false);
    };

    @Override
    public Stream<STACK> modifyExamples(Stream<STACK> exampleStacks) {
        return exampleStacks.map(stack -> {
            IFlagPole.get(stack).ifPresent(c -> c.flag(flag()));
            return stack;
        });
    };

    @Override
    public Stream<STACK> modifyCounterExamples(Stream<STACK> counterExampleStacks) {
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

    public static final class Type<STACK extends MutableDataComponentHolder> extends GenericAdvancedIngredientType<STACK, FlaggedAdvancedIngredient<STACK>> {

        public Type() {
            super(CodecHelper.singleFieldMap(Flag.CODEC, "flag", FlaggedAdvancedIngredient::flag, FlaggedAdvancedIngredient::new), StreamCodec.composite(Flag.STREAM_CODEC, FlaggedAdvancedIngredient::flag, FlaggedAdvancedIngredient::new));
        };

        @Override
        public Stream<FlaggedAdvancedIngredient<STACK>> streamApplicableTypelessIngredients(Level level, STACK stack) {
            return IFlagPole.get(stack).stream()
                .flatMap(IFlagPole::streamAllFlags)
                .map(FlaggedAdvancedIngredient::new);
        };

    };
    
};
