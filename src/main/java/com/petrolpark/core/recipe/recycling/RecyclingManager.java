package com.petrolpark.core.recipe.recycling;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import com.petrolpark.PetrolparkRecipeTypes;
import com.petrolpark.PetrolparkTags;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;

@EventBusSubscriber
public class RecyclingManager {
    
    protected static final Map<Ingredient, RecyclingOutputs> INGREDIENT_INVERSES = new HashMap<>();
    protected static final Map<Item, RecyclingOutputs> ITEM_RECYCLINGS = new HashMap<>();
    protected static final Set<RecyclingOutputsModifier> OUTPUT_MODIFIERS = new TreeSet<>(RecyclingOutputsModifier::compare);

    public static final void registerModifier(RecyclingOutputsModifier recyclingOutputsModifier) {
        OUTPUT_MODIFIERS.add(recyclingOutputsModifier);
    };

    static {
        registerModifier(RecyclingOutputsModifier.DURABILITY);
        registerModifier(RecyclingOutputsModifier.CONTAMINANTS);
        registerModifier(RecyclingOutputsModifier.DECOMPRESSION);
    };

    public static final void loadIngredientInverses(RecipeManager recipeManager) {
        recipeManager.getAllRecipesFor(PetrolparkRecipeTypes.INGREDIENT_RECYCLING.getType()).stream()
            .map(RecipeHolder::value)
            .map(IRecyclingRecipe::cast)
            .forEach(recipe -> INGREDIENT_INVERSES.put(recipe.ingredient(), recipe.outputs()));
    };
     
    public static final RecyclingOutputs getInverse(Ingredient ingredient) {
        return INGREDIENT_INVERSES.computeIfAbsent(ingredient, i -> {
            if (i.isCustom() && i.getCustomIngredient() instanceof IRecyclableCustomIngredient recyclable) return recyclable.getRecyclingOutputs();
            if (!i.isSimple()) return RecyclingOutputs.empty(); // Can't be compared just based on satisfying Item Stacks, so can't be reduced to a simple Item Stack
            ItemStack[] values = i.getItems();
            if (values.length == 1) return new RecyclingOutputs(values[0]); // If there is only a single satisfying Item Stack (e.g. Tag with a single value), that is the inverse
            return RecyclingOutputs.empty();
        });
    };

    /**
     * Get the {@link RecyclingOutputs} of the Item, based on the Recipe used to craft it.
     * @param level
     * @param item
     * @return A {@link RecyclingOutputs}, or {@link RecyclingOutputs#empty()} if there are multiple different Recipes for crafting this Item
     */
    public static final RecyclingOutputs getInverseRecipeRecyclingOutputs(Level level, Item item) {
        List<RecyclingOutputs> possibleOutputs = level.getRecipeManager().getRecipes().stream()
            .map(RecipeHolder::value)
            .filter(PetrolparkTags.RecipeTypes.RECYCLABLE::matches)
            .filter(recipe -> recipe.getResultItem(level.registryAccess()).is(item))
            .map(recipe -> recipe.getIngredients().stream()
                .map(RecyclingManager::getInverse)
                .reduce(RecyclingOutputs::addOther)
                .map(outputs -> outputs.multiplyAll(1f / (float)recipe.getResultItem(level.registryAccess()).getCount()))
            ).dropWhile(Optional::isEmpty)
            .map(Optional::get)
            .distinct() // If there are multiple Recipes but they have the same Ingredients (therefore the same Recycling Outputs), then the Item can still be recycled
            .toList();
        if (possibleOutputs.size() == 1) return possibleOutputs.get(0);
        return RecyclingOutputs.empty(); // If there are multiple non-equal outputs in the list
    };

    /**
     * Get the {@link RecyclingOutput}s from recycling this Stack, before adjusting for the size of the Stack and before applying any {@link RecyclingOutputsModifier}s.
     * @param level
     * @param stack {@link ItemStack#getCount() Count} is ignored
     * @return Copy of a {@link RecyclingOutput}s, safe to modify
     * @see RecyclingManager#getRecyclingOutputs(Level, ItemStack)
     */
    public static final RecyclingOutputs getRawRecyclingOutputs(Level level, ItemStack stack) {

        Optional<RecyclingOutputs> optional = level.getRecipeManager().getRecipeFor(PetrolparkRecipeTypes.RECYCLING.getType(), new SingleRecipeInput(stack), level)
            .map(RecipeHolder::value)
            .map(IRecyclingRecipe::cast)
            .map(IRecyclingRecipe::outputs);
        if (optional.isPresent()) return optional.get().copy();

        return Optional.ofNullable(ITEM_RECYCLINGS.computeIfAbsent(stack.getItem(), item -> getInverseRecipeRecyclingOutputs(level, item)).copy()).orElse(RecyclingOutputs.empty());
    };

    public static final RecyclingOutputs getRecyclingOutputs(Level level, ItemStack stack) {
        ItemStack copy = stack.copyWithCount(1);
        RecyclingOutputs outputs = getRawRecyclingOutputs(level, copy).multiplyAll(stack.getCount());
        for (RecyclingOutputsModifier modifier : OUTPUT_MODIFIERS) modifier.modify(level, copy, outputs); // Go through Modifiers in priority order
        return outputs;
    };

    @SubscribeEvent
    public static final void onRecipesUpdated(RecipesUpdatedEvent event) {
        INGREDIENT_INVERSES.clear();
        ITEM_RECYCLINGS.clear();
        loadIngredientInverses(event.getRecipeManager());
    };
};
