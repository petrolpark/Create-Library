package com.petrolpark.core.recipe.ingredient.modifier;

import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.petrolpark.util.Lang.IndentedTooltipBuilder;

public interface ITypelessIngredientModifier<STACK> {
    
    public boolean test(STACK stack);

    /**
     * All (or an exemplary subset) of the stacks which {@link ITypelessIngredientModifier#test(Object) fulfill} this {@link IIngredientModifier}.
     * This is <b>not</b> guaranteed to have every possible stack, and should never be used that way.
     * @return Non-{@code null}, potentially empty Stream
     */
    public default Stream<? extends STACK> streamExamples() {
        return Stream.empty();
    };

    /**
     * All (or an exemplary subset) of the stacks which do not {@link ITypelessIngredientModifier#test(Object) fulfill} this {@link IIngredientModifier}.
     * This is <b>not</b> guaranteed to have every possible stack (and in fact will usually be empty), and should never be used that way.
     * @return Non-{@code null}, potentially empty Stream
     */
    public default Stream<? extends STACK> streamCounterExamples() {
        return Stream.empty();
    };

    /**
     * Modify an example stack so it {@link ITypelessIngredientModifier#test(Object) fulfill} this {@link IIngredientModifier}.
     * @param exampleStacks May be modified
     * @return A stack fulfilling this Modifier, possibly the same object reference, or {@code null} if that stack can never fulfill this Modifier
     */
    public default Stream<STACK> modifyExamples(Stream<STACK> exampleStacks) { // Sadly can't add new example stacks or modify anything - can only remove ones we know don't fit
        return exampleStacks.filter(this::test);
    };

    /**
     * Modify an example stack so it does not {@link ITypelessIngredientModifier#test(Object) fulfill} this {@link IIngredientModifier}.
     * @param counterExampleStacks May be modified
     * @return A stack not fulfilling this Modifier, possibly the same object reference, or {@code null} if that stack always fulfills this Modifier
     */
    public default Stream<STACK> modifyCounterExamples(Stream<STACK> counterExampleStacks) {
        return counterExampleStacks.dropWhile(this::test);
    };

    public void addToDescription(IndentedTooltipBuilder description);

    public void addToCounterDescription(IndentedTooltipBuilder description);

    /**
     * The {@link IIngredientModifier} (not necessarily of the same {@link IIngredientModifier#getType() type}) which is the exact same {@link ITypelessIngredientModifier#test predicate} as this, but which has the smallest possible overhead.
     * It is acceptable to mutate this Modifier while calling this method, so careful caching the unsimplified version.
     * Usually this will just return {@code this}.
     */
    public default ITypelessIngredientModifier<? super STACK> simplify() {
        return this;
    };

    /**
     * Check and cast the given object to the correct type for this Modifier.
     * This is just to bypass compilation problems with generics, and should only be used when you are certain the object is of the right type.
     * Mainly for use by {@link ITypelessIngredientModifier generic} Modifiers.
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
