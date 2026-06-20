package com.petrolpark.core.world.item.compression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.petrolpark.Petrolpark;
import com.petrolpark.core.world.item.compression.IItemCompressionSequence.EmptyItemCompressionSequence;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.common.util.ItemStackMap;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

@EventBusSubscriber
public class ItemCompressionManager {

    protected static final Map<ItemStack, IItemCompression> COMPRESSIONS = ItemStackMap.createTypeAndTagMap();
    protected static final Map<ItemStack, IItemCompressionSequence> COMPRESSION_SEQUENCES = ItemStackMap.createTypeAndTagMap();

    protected static boolean cacheInvalid = true;

    public static Optional<IItemCompression> get(RecipeManager recipeManager, ItemStack stack) {
        if (cacheInvalid) reload(recipeManager);
        return Optional.ofNullable(COMPRESSIONS.get(stack));
    };

    public static Optional<IItemCompressionSequence> getSequence(RecipeManager recipeManager, ItemStack stack) {
        if (cacheInvalid) reload(recipeManager);
        return Optional.ofNullable(COMPRESSION_SEQUENCES.get(stack));
    };

    private static final List<Recipe<?>> singleInputRecipes = new ArrayList<>();

    public static final void reload(RecipeManager recipeManager) {
        COMPRESSIONS.clear();
        singleInputRecipes.clear(); // Retains memory size from before
        for (CompressionRecipe compression : recipeManager.getRecipes().stream()
            .map(RecipeHolder::value)
            .filter(CraftingRecipe.class::isInstance)
            .map(r -> toCompressionRecipe(recipeManager.registries, r))
            .filter(Objects::nonNull)
            .toList()
        ) { // Close the Stream before checking for decompressions
            Iterator<Recipe<?>> iterator = singleInputRecipes.iterator();
            while (iterator.hasNext()) {
                Recipe<?> recipe = iterator.next();
                if (recipe.getIngredients().get(0).test(compression.result())) {
                    ItemStack decompressed = recipe.getResultItem(recipeManager.registries).copy();
                    if (compression.isDecompressedStacks(decompressed)) {
                        ItemStack decompressedSingle = decompressed.copyWithCount(1);
                        if (COMPRESSIONS.putIfAbsent(decompressedSingle, compression.compression()) != null)
                            COMPRESSIONS.put(decompressedSingle, IItemCompression.NONE); // One Item (Stack, not considering count) may not have more than one compression
                        iterator.remove(); // If the Recipe is a decompression, no other Compressions should map to it
                    };
                };
            };
        };
        COMPRESSIONS.replaceAll((stack, compression) -> compression == IItemCompression.NONE ? null : compression);
        rebuildCompressionSequences();
    };

    /**
     * If the given recipe is a potential compression, returns a {@link CompressionRecipe}.
     * If is is a potential decompression, it is added to {@link ItemCompressionManager#singleInputRecipes}.
     * @param recipe
     * @return A CompressionRecipe, or {@code null}
     */
    public static CompressionRecipe toCompressionRecipe(HolderLookup.Provider registries, Recipe<?> recipe) {
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        if (ingredients.size() == 0) return null;
        if (ingredients.size() == 1) {
            singleInputRecipes.add(recipe);
            return null; 
        };
        Ingredient ingredient = ingredients.get(0);
        int i;
        for (i = 1; i < ingredients.size(); i++) if (!areIngredientsEqual(ingredients.get(i), ingredient)) return null;
        return new CompressionRecipe(ingredient, i, recipe.getResultItem(registries).copy());
    };

    private static boolean areIngredientsEqual(Ingredient ingredient1, Ingredient ingredient2) {
        if (ingredient1 == ingredient2) return true;
        if (ingredient1.isCustom() != ingredient2.isCustom()) return false;
        if (ingredient1.isCustom()) return Objects.equals(ingredient1.getCustomIngredient(), ingredient2.getCustomIngredient());
        else return Arrays.equals(ingredient1.getValues(), ingredient2.getValues());
    };

    public static record CompressionRecipe(Ingredient ingredient, ItemCompression compression) implements IItemCompression {

        public CompressionRecipe(Ingredient ingredient, int count, ItemStack result) {
            this(ingredient, new ItemCompression(count, result));
        };

        /**
         * Whether the given Item Stack is the exact reversal of this Compression.
         * @param decompressed
         */
        public boolean isDecompressedStacks(ItemStack decompressed) {
            return decompressed.getCount() * result().getCount() == count() && ingredient().test(decompressed);
        };

        @Override
        public int count() {
            return compression().count();
        };

        @Override
        public ItemStack result() {
            return compression().result();
        };
    };

    public static final void rebuildCompressionSequences() {
        COMPRESSION_SEQUENCES.clear();
        COMPRESSIONS.forEach((stack, compression) -> {
            IItemCompressionSequence newSequence = putNewSequence(stack, () -> {
                FinishableMapItemCompressionSequence sequence = new FinishableMapItemCompressionSequence(stack.copy());
                IItemCompression nextCompression = compression;
                while (nextCompression != null) {
                    try {
                        if (!sequence.add(nextCompression)) return new EmptySharedItemCompressionSequence(sequence); // Remove all circular Compression sequences
                    } catch (ArithmeticException e) {
                        Petrolpark.LOGGER.warn("Item %s has too large of a compression factor to recognise compression sequence");
                        return new EmptySharedItemCompressionSequence(sequence);
                    };
                    nextCompression = COMPRESSIONS.get(nextCompression.result());
                };
                return sequence.finish();
            });
            if (newSequence != null) {
                newSequence.getAllItems().forEach(s -> COMPRESSION_SEQUENCES.merge(s, newSequence, (oldSequence, sequence) -> sequence.size() > oldSequence.size() ? sequence : oldSequence)); // Replace shorter Sequences
            };
        });
        COMPRESSION_SEQUENCES.replaceAll((stack, sequence) -> sequence.isEmpty() ? null : sequence);
    };

    /**
     * @param stack
     * @param sequenceSupplier
     * @return {@code null} if the key already had an associated value, or the new {@link IItemCompressionSequence} if it did not
     */
    private static IItemCompressionSequence putNewSequence(ItemStack stack, Supplier<IItemCompressionSequence> sequenceSupplier) {
        IItemCompressionSequence oldValue = COMPRESSION_SEQUENCES.get(stack);
        if (oldValue != null) return oldValue;
        IItemCompressionSequence sequence = sequenceSupplier.get();
        COMPRESSION_SEQUENCES.put(stack, sequence);
        return sequence;
    };

    static class EmptySharedItemCompressionSequence extends EmptyItemCompressionSequence {

        protected final List<ItemStack> items;

        public EmptySharedItemCompressionSequence(IItemCompressionSequence sequence) {
            this.items = sequence.getKnownItems();
        };

        @Override
        public List<ItemStack> getAllItems() {
            return items;
        };
    };

    @SubscribeEvent
    public static final void onRecipeReload(RecipesUpdatedEvent event) {
        cacheInvalid = true;
    };

    @SubscribeEvent
    public static final void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new ResourceManagerReloadListener() {
            @Override
            public void onResourceManagerReload(@Nonnull ResourceManager resourceManager) {
                cacheInvalid = true;
            };
        });
    };
};
