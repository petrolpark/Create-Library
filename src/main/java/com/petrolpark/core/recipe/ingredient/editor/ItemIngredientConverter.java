package com.petrolpark.core.recipe.ingredient.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.petrolpark.core.recipe.ingredient.ModifiedIngredient;
import com.petrolpark.core.recipe.ingredient.modifier.CompoundIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.IIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.ItemIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.ItemItemIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.NotIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.TagItemIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.TypeAttachedIngredientModifier;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DifferenceIngredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IntersectionIngredient;

import com.petrolpark.PetrolparkRegistries;

public class ItemIngredientConverter implements INeoForgeIngredientConverter<ItemStack, Ingredient> {

    /**
     * {@inheritDoc}
     * <p>This method is intentionally hardcoded. <b>There should never be any need to mixin into it.</b> To add compatibility with your mod's {@link ICustomIngredient}, extend and {@link PetrolparkRegistries.Keys#INGREDIENT_MODIFIER_TYPE register} an {@link ItemIngredientModifier} duplicating the Custom Ingredient's behaviour.</p>
     */
    @Override
    @SuppressWarnings("unchecked")
    public Ingredient convertToNeoForge(IIngredientModifier<? super ItemStack> modifier) throws IngredientConversionException {
        if (modifier instanceof TypeAttachedIngredientModifier typedModifier && typedModifier.untypedModifier() instanceof CompoundIngredientModifier untypedModifier) {
            CompoundIngredientModifier<ItemStack> compoundModifier;
            try {
                compoundModifier = (CompoundIngredientModifier<ItemStack>)untypedModifier;
            } catch (ClassCastException e) {
                throw new IngredientConversionException("Could not cast the Compound Ingredient type");
            };

            if (!compoundModifier.isAnd() && !compoundModifier.isOr()) throw new IngredientConversionException("Compound Ingredient Modifiers may only require any or all child Modifiers");

            final List<IIngredientModifier<? super ItemStack>> childModifiers = new ArrayList<>();
            final List<IIngredientModifier<? super ItemStack>> nottedChildModifiers = new ArrayList<>();
            final List<Ingredient.Value> vanillaIngredientValues = new ArrayList<>();
            final List<Ingredient.Value> nottedVanillaIngredientValues = new ArrayList<>();

            for (IIngredientModifier<? super ItemStack> childModifier : compoundModifier.modifiers()) {
                Optional<IIngredientModifier<? super ItemStack>> notOp = getNot(childModifier);
                boolean not = notOp.isPresent();
                if (not) childModifier = notOp.get();
                Optional<Ingredient.Value> valueOp = getVanillaIngredientValue(childModifier);
                if (valueOp.isPresent()) {
                    (not ? nottedVanillaIngredientValues : vanillaIngredientValues).add(valueOp.get());
                } else {
                    (not ? nottedChildModifiers : childModifiers).add(childModifier);
                };
            };

            final Ingredient vanillaIngredient;
            if (nottedVanillaIngredientValues.isEmpty()) {
                vanillaIngredient = Ingredient.fromValues(vanillaIngredientValues.stream());
            } else {
                vanillaIngredient = DifferenceIngredient.of(Ingredient.fromValues(vanillaIngredientValues.stream()), 
                    compoundModifier.isAnd() // de Morgan's Law
                        ? Ingredient.fromValues(nottedVanillaIngredientValues.stream())
                        : IntersectionIngredient.of(nottedVanillaIngredientValues.stream()
                            .map(Stream::of)
                            .map(Ingredient::fromValues)
                            .toArray(i -> new Ingredient[i]))
                );
            };

            if (childModifiers.isEmpty() && nottedChildModifiers.isEmpty()) { // Pure vanilla Ingredient
                if (vanillaIngredientValues.isEmpty()) {
                    throw new IngredientConversionException("Cannot have only 'not' values in a Compound");
                } else {
                    return vanillaIngredient;
                }
            } else if (childModifiers.isEmpty()) { // Not a pure vanilla Ingredient, and only 'nots'
                throw new IngredientConversionException("Cannot have only 'not' Ingredient Modifiers in a Compound");
            } else {  // Not a pure vanilla Ingredient
                List<Ingredient> yesIngredients = new ArrayList<>(childModifiers.size() + (vanillaIngredient.isEmpty() ? 0 : 1));
                if (!vanillaIngredient.isEmpty()) yesIngredients.add(vanillaIngredient);
                for (IIngredientModifier<? super ItemStack> childModifier : childModifiers) {
                    yesIngredients.add(convertToNeoForge(childModifier));
                };
                Ingredient yesIngredient = (compoundModifier.isOr() ? new CompoundIngredient(yesIngredients) : new IntersectionIngredient(yesIngredients)).toVanilla(); // If its not an and compound, it should be an or

                if (nottedChildModifiers.isEmpty()) {
                    return yesIngredient;
                } else {
                    List<Ingredient> noIngredients = new ArrayList<>(nottedChildModifiers.size());
                    for (IIngredientModifier<? super ItemStack> nottedChildModifier : nottedChildModifiers) {
                        noIngredients.add(convertToNeoForge(nottedChildModifier));
                    };
                    Ingredient noIngredient = (compoundModifier.isOr() ? new IntersectionIngredient(noIngredients) : new CompoundIngredient(noIngredients)).toVanilla(); // de Morgan's Law
                    return DifferenceIngredient.of(yesIngredient, noIngredient);
                }
            }
        } else { // Not a compound
            Optional<Ingredient.Value> valueOp = getVanillaIngredientValue(modifier);
            if (valueOp.isPresent()) return Ingredient.fromValues(valueOp.stream());
        };
        throw new IngredientConversionException("todo");
    };

    protected Optional<Ingredient.Value> getVanillaIngredientValue(IIngredientModifier<? super ItemStack> modifier) {
        if (modifier instanceof ItemItemIngredientModifier itemIIM) return Optional.of(new Ingredient.ItemValue(new ItemStack(itemIIM.item())));
        if (modifier instanceof TagItemIngredientModifier tagIIM) return Optional.of(new Ingredient.TagValue(tagIIM.tag()));
        return Optional.empty();
    };

    @SuppressWarnings("unchecked")
    protected Optional<IIngredientModifier<? super ItemStack>> getNot(IIngredientModifier<? super ItemStack> potentialNotModifier) {
        if (potentialNotModifier instanceof TypeAttachedIngredientModifier typedModifier && typedModifier.untypedModifier() instanceof NotIngredientModifier untypedModifier) {
            try {
                return Optional.of(((NotIngredientModifier<ItemStack>)untypedModifier).modifier());
            } catch (ClassCastException e) {
                return Optional.empty();
            }
        };
        return Optional.empty();
    };

    /**
     * {@inheritDoc}
     * <p>This method is intentionally hardcoded. <b>There should never be any need to mixin into it.</b> To add compatibility with your mod's {@link ICustomIngredient}, extend and {@link PetrolparkRegistries.Keys#INGREDIENT_MODIFIER_TYPE register} an {@link ItemIngredientModifier} duplicating the Custom Ingredient's behaviour.</p>
     */
    @Override
    public IIngredientModifier<? super ItemStack> convertToModifier(Ingredient ingredient) throws IngredientConversionException {
        if (ingredient.isCustom()) {
            if (ingredient.getCustomIngredient() instanceof ModifiedIngredient modifiedIngredient) {
                return modifiedIngredient.modifier().simplify();
            } else if (ingredient.getCustomIngredient() instanceof CompoundIngredient compoundIngredient) {
                List<IIngredientModifier<? super ItemStack>> modifiers = new ArrayList<>(compoundIngredient.children().size());
                for (Ingredient child : compoundIngredient.children()) modifiers.add(convertToModifier(child));
                return CompoundIngredientModifier.or(modifiers).simplify();
            } else if (ingredient.getCustomIngredient() instanceof IntersectionIngredient intersectionIngredient) {
                List<IIngredientModifier<? super ItemStack>> modifiers = new ArrayList<>(intersectionIngredient.children().size());
                for (Ingredient child : intersectionIngredient.children()) modifiers.add(convertToModifier(child));
                return CompoundIngredientModifier.and(modifiers).simplify();
            } else if (ingredient.getCustomIngredient() instanceof DifferenceIngredient differenceIngredient) {
                List<IIngredientModifier<? super ItemStack>> modifiers = new ArrayList<>(2);
                modifiers.add(convertToModifier(differenceIngredient.base()));
                modifiers.add(NotIngredientModifier.of(convertToModifier(differenceIngredient.subtracted())));
                return CompoundIngredientModifier.and(modifiers).simplify();
            } else {
                throw new IngredientConversionException("Cannot convert Custom Ingredient to Petrolpark Modified Ingredient");
            }
            //TODO NeoForge DataComponentIngredients (maybe)
        } else if (ingredient.isSimple()) { // Should always be true at this point but double check in case of weird illegal mixins
            List<IIngredientModifier<? super ItemStack>> modifiers = new ArrayList<>(ingredient.getValues().length);
            for (Ingredient.Value value : ingredient.getValues()) {
                if (value instanceof Ingredient.ItemValue itemValue) modifiers.add(new ItemItemIngredientModifier(itemValue.item().getItem()));
                else if (value instanceof Ingredient.TagValue tagValue) modifiers.add(new TagItemIngredientModifier(tagValue.tag()));
                // Ignore additional weird (illegal) values
            };
            if (modifiers.size() == 1) return modifiers.get(0);
            return CompoundIngredientModifier.or(modifiers);
        };
        throw new IngredientConversionException("Unknown problem");
    };
    
};
