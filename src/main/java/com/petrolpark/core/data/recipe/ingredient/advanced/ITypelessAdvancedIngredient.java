package com.petrolpark.core.data.recipe.ingredient.advanced;

import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.petrolpark.util.Lang.IndentedTooltipBuilder;

public interface ITypelessAdvancedIngredient<STACK> extends Predicate<STACK> {

    /**
     * All (or an exemplary subset) of the stacks which {@link ITypelessAdvancedIngredient#test(Object) fulfill} this {@link IAdvancedIngredient}.
     * This is <b>not</b> guaranteed to have every possible stack, and should never be used that way.
     * @return Non-{@code null}, potentially empty Stream
     */
    public default Stream<? extends STACK> streamExamples() {
        return Stream.empty();
    };

    /**
     * All (or an exemplary subset) of the stacks which do not {@link ITypelessAdvancedIngredient#test(Object) fulfill} this {@link IAdvancedIngredient}.
     * This is <b>not</b> guaranteed to have every possible stack (and in fact will usually be empty), and should never be used that way.
     * @return Non-{@code null}, potentially empty Stream
     */
    public default Stream<? extends STACK> streamCounterExamples() {
        return Stream.empty();
    };

    /**
     * Modify an example stack so it {@link ITypelessAdvancedIngredient#test(Object) fulfill} this {@link IAdvancedIngredient}.
     * @param exampleStacks May be modified
     * @return A stack fulfilling this Ingredient, possibly the same object reference, or {@code null} if that stack can never fulfill this Ingredient
     */
    public default Stream<STACK> modifyExamples(Stream<STACK> exampleStacks) { // Sadly can't add new example stacks or modify anything - can only remove ones we know don't fit
        return exampleStacks.filter(this::test);
    };

    /**
     * Modify an example stack so it does not {@link ITypelessAdvancedIngredient#test(Object) fulfill} this {@link IAdvancedIngredient}.
     * @param counterExampleStacks May be modified
     * @return A stack not fulfilling this Ingredient, possibly the same object reference, or {@code null} if that stack always fulfills this Ingredient
     */
    public default Stream<STACK> modifyCounterExamples(Stream<STACK> counterExampleStacks) {
        return counterExampleStacks.filter(Predicate.not(this::test));
    };

    public void addToDescription(IndentedTooltipBuilder description);

    public void addToCounterDescription(IndentedTooltipBuilder description);

    /**
     * The {@link IAdvancedIngredient} (not necessarily of the same {@link IAdvancedIngredient#getType() type}) which is the exact same {@link ITypelessAdvancedIngredient#test predicate} as this, but which has the smallest possible overhead.
     * It is acceptable to mutate this Ingredient while calling this method, so careful caching the unsimplified version.
     * Usually this will just return {@code this}.
     */
    public default ITypelessAdvancedIngredient<? super STACK> simplify() {
        return this;
    };

    /**
     * Check and cast the given object to the correct type for this Ingredient.
     * This is just to bypass compilation problems with generics, and should only be used when you are certain the object is of the right type.
     * Mainly for use by {@link ITypelessAdvancedIngredient generic} Ingredients.
     * @param stack
     * @return Potentially {@code null}
     */
    @Nullable
    @SuppressWarnings("unchecked")
    default STACK checkedCast(Object stack) {
        try {
            return (STACK)stack;
        } catch (ClassCastException e) {
            return null;
        }
    };
};
