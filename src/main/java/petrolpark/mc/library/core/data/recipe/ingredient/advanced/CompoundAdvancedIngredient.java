package petrolpark.mc.library.core.data.recipe.ingredient.advanced;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import petrolpark.mc.library.registry.PetrolparkAdvancedIngredientTypes;
import petrolpark.mc.library.util.Lang;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;

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

public record CompoundAdvancedIngredient<STACK>(List<IAdvancedIngredient<? super STACK>> ingredients, int required) implements ITypelessAdvancedIngredient<STACK>, IForcingItemAdvancedIngredient {

    public static final <STACK> MapCodec<CompoundAdvancedIngredient<STACK>> codec(Codec<IAdvancedIngredient<? super STACK>> typeCodec) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
            typeCodec.listOf().fieldOf("ingredients").forGetter(CompoundAdvancedIngredient::ingredients),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("proportion", 1).forGetter(CompoundAdvancedIngredient::required)
        ).apply(instance, CompoundAdvancedIngredient::new))
        //.validate(CompoundAdvancedIngredient::validate)
        ;
    };

    public static final <STACK> StreamCodec<RegistryFriendlyByteBuf, CompoundAdvancedIngredient<STACK>> streamCodec(StreamCodec<RegistryFriendlyByteBuf, IAdvancedIngredient<? super STACK>> typeStreamCodec) {
        return StreamCodec.composite(
            typeStreamCodec.apply(ByteBufCodecs.list()), CompoundAdvancedIngredient::ingredients,
            ByteBufCodecs.INT, CompoundAdvancedIngredient::required,
            CompoundAdvancedIngredient::new
        );
    };

    protected static final <STACK> CompoundAdvancedIngredient<STACK> typelessAnd(List<IAdvancedIngredient<? super STACK>> ingredients) {
        return new CompoundAdvancedIngredient<>(ingredients, ingredients.size());
    };

    protected static final <STACK> CompoundAdvancedIngredient<STACK> typelessOr(List<IAdvancedIngredient<? super STACK>> ingredients) {
        return new CompoundAdvancedIngredient<>(ingredients, 1);
    };

    public static final IAdvancedIngredient<ItemStack> and(List<IAdvancedIngredient<? super ItemStack>> ingredients) {
        return PetrolparkAdvancedIngredientTypes.ITEM_COMPOUND.get().create(typelessAnd(ingredients));
    };

    public static final IAdvancedIngredient<ItemStack> or(List<IAdvancedIngredient<? super ItemStack>> ingredients) {
        return PetrolparkAdvancedIngredientTypes.ITEM_COMPOUND.get().create(typelessOr(ingredients));
    };

    @Override
    public boolean test(STACK stack) {
        if (isImpossible()) return false;
        int fulfilled = 0;
        int unfulfilled = 0;
        for (IAdvancedIngredient<? super STACK> ingredient : ingredients()) {
            if (ingredient.test(stack)) fulfilled++; else unfulfilled++;
            if (unfulfilled >= ingredients().size() - required()) return false;
            if (fulfilled >= required) return true;
        };
        return false;
    };

    @Override
    public Stream<? extends STACK> streamExamples() {
        if (isImpossible()) return Stream.empty();
        Stream<STACK> stream = ingredients().stream()
            .flatMap(IAdvancedIngredient::streamExamples)
            .map(this::checkedCast);
        for (IAdvancedIngredient<? super STACK> ingredient : ingredients()) {
            stream = modifyExamples(stream, ingredient);
        };
        return stream.filter(this::test);
    };

    @Override
    public Stream<? extends STACK> streamCounterExamples() {
        if (isImpossible()) return Stream.empty();
        Stream<STACK> stream = ingredients().stream()
            .flatMap(IAdvancedIngredient::streamCounterExamples)
            .map(this::checkedCast);
        for (IAdvancedIngredient<? super STACK> ingredient : ingredients()) {
            stream = modifyCounterExamples(stream, ingredient);
        };
        return stream.filter(Predicate.not(this::test));
    };

    @Override
    public Stream<STACK> modifyExamples(Stream<STACK> exampleStacks) { // Not quite perfect but these are only examples
        if (isImpossible()) return Stream.empty();
        for (IAdvancedIngredient<? super STACK> ingredient : ingredients()) {
            exampleStacks = modifyExamples(exampleStacks, ingredient);
        }; 
        return exampleStacks;
    };

    protected <STACK_PARENT> Stream<STACK> modifyExamples(Stream<STACK> exampleStacks, IAdvancedIngredient<STACK_PARENT> ingredient) {
        return ingredient.modifyExamples(exampleStacks.map(ingredient::checkedCast)).map(this::checkedCast);
    };

    @Override
    public Stream<STACK> modifyCounterExamples(Stream<STACK> counterExampleStacks) {
        if (isImpossible()) return counterExampleStacks;
        for (IAdvancedIngredient<? super STACK> ingredient : ingredients()) {
            counterExampleStacks = modifyCounterExamples(counterExampleStacks, ingredient);
        }; 
        return counterExampleStacks;
    };

    protected <STACK_PARENT> Stream<STACK> modifyCounterExamples(Stream<STACK> exampleStacks, IAdvancedIngredient<STACK_PARENT> ingredient) {
        return ingredient.modifyCounterExamples(exampleStacks.map(ingredient::checkedCast)).map(this::checkedCast);
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder description) {
        if (isOr()) description.add(translate("any"));
        else if (isAnd()) description.add(translate("all"));
        else description.add(translate("at_least", required()));
        description.indent();
        ingredients().forEach(ingredient -> ingredient.addToDescription(description));
        description.unindent();
    };

    @Override
    public void addToCounterDescription(IndentedTooltipBuilder description) {
        if (isOr()) description.add(translate("none"));
        else description.add(translate("at_most", required() - 1));
        description.indent();
        ingredients().forEach(ingredient -> ingredient.addToDescription(description));
        description.unindent();
    };

    @Override
    public @Nonnull Optional<ItemStack> forceLootItemFunction(LootItemFunction function, LootContext context, ItemStack stack) {
        boolean forced = false;
        ItemStack forcedStack = stack;
        for (IAdvancedIngredient<? super STACK> ingredient : ingredients()) {
            if (ingredient instanceof IForcingItemAdvancedIngredient functionForcingIngredient) {
                Optional<ItemStack> forcedStackOp = functionForcingIngredient.forceLootItemFunction(function, context, forcedStack);
                if (forcedStackOp.isPresent()) {
                    forced = true;
                    forcedStack = forcedStackOp.get();
                };
            };
        };
        return forced ? Optional.of(forcedStack) : Optional.empty();
    };

    @Override
    public @Nonnull Optional<ItemStack> forbidLootItemFunction(LootItemFunction function, LootContext context, ItemStack stack) {
        boolean forbidden = false;
        ItemStack forbiddenStack = stack;
        for (IAdvancedIngredient<? super STACK> ingredient : ingredients()) {
            if (ingredient instanceof IForcingItemAdvancedIngredient functionForcingIngredient) {
                Optional<ItemStack> forbiddenStackOp = functionForcingIngredient.forbidLootItemFunction(function, context, forbiddenStack);
                if (forbiddenStackOp.isPresent()) {
                    forbidden = true;
                    forbiddenStack = forbiddenStackOp.get();
                };
            };
        };
        return forbidden ? Optional.of(forbiddenStack) : Optional.empty();
    };

    @Override
    public @Nullable Optional<MerchantOffer> forceTradeListing(ItemListing tradeListing, Entity trader, RandomSource random) {
        for (IAdvancedIngredient<? super STACK> ingredient : ingredients()) {
            if (ingredient instanceof IForcingItemAdvancedIngredient functionForcingIngredient) {
                Optional<MerchantOffer> forcedOffer = functionForcingIngredient.forceTradeListing(tradeListing, trader, random);
                if (forcedOffer != null) return forcedOffer;
            };
        };
        return null;
    };

    @Override
    public @Nullable Optional<MerchantOffer> forbidTradeListing(ItemListing tradeListing, Entity trader, RandomSource random) {
        for (IAdvancedIngredient<? super STACK> ingredient : ingredients()) {
            if (ingredient instanceof IForcingItemAdvancedIngredient functionForcingIngredient) {
                Optional<MerchantOffer> forbiddenOffer = functionForcingIngredient.forbidTradeListing(tradeListing, trader, random);
                if (forbiddenOffer != null) return forbiddenOffer;
            };
        };
        return null;
    };

    @Override
    public ITypelessAdvancedIngredient<? super STACK> simplify() {
        if (ingredients().size() == 1) return ingredients().get(0).simplify();
        ingredients().replaceAll(IAdvancedIngredient::simplify);
        if (isAnd() || isOr()) {
            Iterator<IAdvancedIngredient<? super STACK>> iterator = ingredients().iterator();
            while (iterator.hasNext()) {
                cast(iterator.next()).ifPresent(compoundIngredient -> {
                    if (compoundIngredient.isAnd() == isAnd() || compoundIngredient.isOr() == isOr()) {
                        ingredients().addAll(compoundIngredient.ingredients());
                        iterator.remove();
                    };
                });
            };
        };
        return this;
    };

    public boolean isImpossible() {
        return required() > ingredients().size();
    };

    public boolean isOr() {
        return required() == 1;
    };

    public boolean isAnd() {
        return required() == ingredients().size();
    };

    @SuppressWarnings("unchecked")
    protected Optional<CompoundAdvancedIngredient<? super STACK>> cast(IAdvancedIngredient<? super STACK> ingredient) {
        if (ingredient instanceof TypeAttachedAdvancedIngredient typedIngredient && typedIngredient.untypedIngredient() instanceof CompoundAdvancedIngredient compoundIngredient) return Optional.of((CompoundAdvancedIngredient<? super STACK>)compoundIngredient);
        return Optional.empty();
    };

    protected Component translate(String keyPostfix, Object... args) {
        return Lang.translate("advancedIngredient.compound." + keyPostfix, args);
    };

    // Doesn't compile for some reason
    // public DataResult<CompoundAdvancedIngredient<STACK>> validate() {
    //     if (required() > ingredients().size()) return DataResult.error(() -> "Number of required ingredients cannot be greater than number of ingredients");
    //     return DataResult.success(this);
    // };
    
};
