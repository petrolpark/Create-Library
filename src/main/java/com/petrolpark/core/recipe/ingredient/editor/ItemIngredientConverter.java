package com.petrolpark.core.recipe.ingredient.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.core.recipe.ingredient.AdvancedItemIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.CompoundAdvancedIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.IAdvancedIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.ItemAdvancedIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.ItemItemAdvancedIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.NotAdvancedIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.TagItemAdvancedIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.TypeAttachedAdvancedIngredient;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DifferenceIngredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IntersectionIngredient;

public class ItemIngredientConverter implements INeoForgeIngredientConverter<ItemStack, Ingredient> {

    /**
     * {@inheritDoc}
     * <p>This method is intentionally hardcoded. <b>There should never be any need to mixin into it.</b> To add compatibility with your mod's {@link ICustomIngredient}, extend and {@link PetrolparkRegistries.Keys#ADVANCED_ITEM_INGREDIENT_TYPE register} an {@link ItemAdvancedIngredient} duplicating the Custom Ingredient's behaviour.</p>
     */
    @Override
    @SuppressWarnings("unchecked")
    public Ingredient convertToNeoForgeIngredient(IAdvancedIngredient<? super ItemStack> advancedIngredient) throws IngredientConversionException {
        if (advancedIngredient instanceof TypeAttachedAdvancedIngredient typedAdvancedIngredient && typedAdvancedIngredient.untypedIngredient() instanceof CompoundAdvancedIngredient untypedAdvancedIngredient) {
            CompoundAdvancedIngredient<ItemStack> compoundAdvancedIngredient;
            try {
                compoundAdvancedIngredient = (CompoundAdvancedIngredient<ItemStack>)untypedAdvancedIngredient;
            } catch (ClassCastException e) {
                throw new IngredientConversionException("Could not cast the Compound Ingredient type");
            };

            if (!compoundAdvancedIngredient.isAnd() && !compoundAdvancedIngredient.isOr()) throw new IngredientConversionException("Compound Ingredient AdvancedIngredients may only require any or all child AdvancedIngredients");

            final List<IAdvancedIngredient<? super ItemStack>> childAdvancedIngredients = new ArrayList<>();
            final List<IAdvancedIngredient<? super ItemStack>> nottedChildAdvancedIngredients = new ArrayList<>();
            final List<Ingredient.Value> vanillaIngredientValues = new ArrayList<>();
            final List<Ingredient.Value> nottedVanillaIngredientValues = new ArrayList<>();

            for (IAdvancedIngredient<? super ItemStack> childAdvancedIngredient : compoundAdvancedIngredient.ingredients()) {
                Optional<IAdvancedIngredient<? super ItemStack>> notOp = getNot(childAdvancedIngredient);
                boolean not = notOp.isPresent();
                if (not) childAdvancedIngredient = notOp.get();
                Optional<Ingredient.Value> valueOp = getVanillaIngredientValue(childAdvancedIngredient);
                if (valueOp.isPresent()) {
                    (not ? nottedVanillaIngredientValues : vanillaIngredientValues).add(valueOp.get());
                } else {
                    (not ? nottedChildAdvancedIngredients : childAdvancedIngredients).add(childAdvancedIngredient);
                };
            };

            final Ingredient vanillaIngredient;
            if (nottedVanillaIngredientValues.isEmpty()) {
                vanillaIngredient = Ingredient.fromValues(vanillaIngredientValues.stream());
            } else {
                vanillaIngredient = DifferenceIngredient.of(Ingredient.fromValues(vanillaIngredientValues.stream()), 
                    compoundAdvancedIngredient.isAnd() // de Morgan's Law
                        ? Ingredient.fromValues(nottedVanillaIngredientValues.stream())
                        : IntersectionIngredient.of(nottedVanillaIngredientValues.stream()
                            .map(Stream::of)
                            .map(Ingredient::fromValues)
                            .toArray(i -> new Ingredient[i]))
                );
            };

            if (childAdvancedIngredients.isEmpty() && nottedChildAdvancedIngredients.isEmpty()) { // Pure vanilla Ingredient
                if (vanillaIngredientValues.isEmpty()) {
                    throw new IngredientConversionException("Cannot have only 'not' values in a Compound");
                } else {
                    return vanillaIngredient;
                }
            } else if (childAdvancedIngredients.isEmpty()) { // Not a pure vanilla Ingredient, and only 'nots'
                throw new IngredientConversionException("Cannot have only 'not' Ingredient AdvancedIngredients in a Compound");
            } else {  // Not a pure vanilla Ingredient
                List<Ingredient> yesIngredients = new ArrayList<>(childAdvancedIngredients.size() + (vanillaIngredient.isEmpty() ? 0 : 1));
                if (!vanillaIngredient.isEmpty()) yesIngredients.add(vanillaIngredient);
                for (IAdvancedIngredient<? super ItemStack> childAdvancedIngredient : childAdvancedIngredients) {
                    yesIngredients.add(convertToNeoForgeIngredient(childAdvancedIngredient));
                };
                Ingredient yesIngredient = (compoundAdvancedIngredient.isOr() ? new CompoundIngredient(yesIngredients) : new IntersectionIngredient(yesIngredients)).toVanilla(); // If its not an and compound, it should be an or

                if (nottedChildAdvancedIngredients.isEmpty()) {
                    return yesIngredient;
                } else {
                    List<Ingredient> noIngredients = new ArrayList<>(nottedChildAdvancedIngredients.size());
                    for (IAdvancedIngredient<? super ItemStack> nottedChildAdvancedIngredient : nottedChildAdvancedIngredients) {
                        noIngredients.add(convertToNeoForgeIngredient(nottedChildAdvancedIngredient));
                    };
                    Ingredient noIngredient = (compoundAdvancedIngredient.isOr() ? new IntersectionIngredient(noIngredients) : new CompoundIngredient(noIngredients)).toVanilla(); // de Morgan's Law
                    return DifferenceIngredient.of(yesIngredient, noIngredient);
                }
            }
        } else { // Not a compound
            Optional<Ingredient.Value> valueOp = getVanillaIngredientValue(advancedIngredient);
            if (valueOp.isPresent()) return Ingredient.fromValues(valueOp.stream());
        };
        throw new IngredientConversionException("todo");
    };

    protected Optional<Ingredient.Value> getVanillaIngredientValue(IAdvancedIngredient<? super ItemStack> advancedIngredient) {
        if (advancedIngredient instanceof ItemItemAdvancedIngredient itemIIM) return Optional.of(new Ingredient.ItemValue(new ItemStack(itemIIM.item())));
        if (advancedIngredient instanceof TagItemAdvancedIngredient tagIIM) return Optional.of(new Ingredient.TagValue(tagIIM.tag()));
        return Optional.empty();
    };

    @SuppressWarnings("unchecked")
    protected Optional<IAdvancedIngredient<? super ItemStack>> getNot(IAdvancedIngredient<? super ItemStack> potentialNotAdvancedIngredient) {
        if (potentialNotAdvancedIngredient instanceof TypeAttachedAdvancedIngredient typedAdvancedIngredient && typedAdvancedIngredient.untypedIngredient() instanceof NotAdvancedIngredient untypedAdvancedIngredient) {
            try {
                return Optional.of(((NotAdvancedIngredient<ItemStack>)untypedAdvancedIngredient).ingredient());
            } catch (ClassCastException e) {
                return Optional.empty();
            }
        };
        return Optional.empty();
    };

    /**
     * {@inheritDoc}
     * <p>This method is intentionally hardcoded. <b>There should never be any need to mixin into it.</b> To add compatibility with your mod's {@link ICustomIngredient}, extend and {@link PetrolparkRegistries.Keys#ADVANCED_ITEM_INGREDIENT_TYPE register} an {@link ItemAdvancedIngredient} duplicating the Custom Ingredient's behaviour.</p>
     */
    @Override
    public IAdvancedIngredient<? super ItemStack> convertToAdvancedIngredient(Ingredient ingredient) throws IngredientConversionException {
        if (ingredient.isCustom()) {
            if (ingredient.getCustomIngredient() instanceof AdvancedItemIngredient modifiedIngredient) {
                return modifiedIngredient.advacnedIngredient().simplify();
            } else if (ingredient.getCustomIngredient() instanceof CompoundIngredient compoundIngredient) {
                List<IAdvancedIngredient<? super ItemStack>> advancedIngredients = new ArrayList<>(compoundIngredient.children().size());
                for (Ingredient child : compoundIngredient.children()) advancedIngredients.add(convertToAdvancedIngredient(child));
                return CompoundAdvancedIngredient.or(advancedIngredients).simplify();
            } else if (ingredient.getCustomIngredient() instanceof IntersectionIngredient intersectionIngredient) {
                List<IAdvancedIngredient<? super ItemStack>> advancedIngredients = new ArrayList<>(intersectionIngredient.children().size());
                for (Ingredient child : intersectionIngredient.children()) advancedIngredients.add(convertToAdvancedIngredient(child));
                return CompoundAdvancedIngredient.and(advancedIngredients).simplify();
            } else if (ingredient.getCustomIngredient() instanceof DifferenceIngredient differenceIngredient) {
                List<IAdvancedIngredient<? super ItemStack>> advancedIngredients = new ArrayList<>(2);
                advancedIngredients.add(convertToAdvancedIngredient(differenceIngredient.base()));
                advancedIngredients.add(NotAdvancedIngredient.of(convertToAdvancedIngredient(differenceIngredient.subtracted())));
                return CompoundAdvancedIngredient.and(advancedIngredients).simplify();
            } else {
                throw new IngredientConversionException("Cannot convert Custom Ingredient to Petrolpark Modified Ingredient");
            }
            //TODO NeoForge DataComponentIngredients (maybe)
        } else if (ingredient.isSimple()) { // Should always be true at this point but double check in case of weird illegal mixins
            List<IAdvancedIngredient<? super ItemStack>> advancedIngredients = new ArrayList<>(ingredient.getValues().length);
            for (Ingredient.Value value : ingredient.getValues()) {
                if (value instanceof Ingredient.ItemValue itemValue) advancedIngredients.add(new ItemItemAdvancedIngredient(itemValue.item().getItem()));
                else if (value instanceof Ingredient.TagValue tagValue) advancedIngredients.add(new TagItemAdvancedIngredient(tagValue.tag()));
                // Ignore additional weird (illegal) values
            };
            if (advancedIngredients.size() == 1) return advancedIngredients.get(0);
            return CompoundAdvancedIngredient.or(advancedIngredients);
        };
        throw new IngredientConversionException("Unknown problem");
    };
    
};
