package com.petrolpark.core.recipe.compression;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
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
import net.neoforged.neoforge.common.util.ItemStackMap;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

@EventBusSubscriber
public class ItemCompressionManager implements ResourceManagerReloadListener {

    protected static final Map<ItemStack, IItemCompression> COMPRESSIONS = ItemStackMap.createTypeAndTagMap();
    protected static final Map<ItemStack, IItemCompressionSequence> COMPRESSION_SEQUENCES = ItemStackMap.createTypeAndTagMap();

    public static Optional<IItemCompression> get(ItemStack stack) {
        return Optional.ofNullable(COMPRESSIONS.get(stack));
    };

    public static Optional<IItemCompressionSequence> getSequence(ItemStack stack) {
        return Optional.ofNullable(COMPRESSION_SEQUENCES.get(stack));
    };

    public final RegistryAccess registryAccess;
    public final RecipeManager recipeManager;

    public final List<Recipe<?>> singleInputRecipes = new ArrayList<>();

    public ItemCompressionManager(RegistryAccess registryAccess, RecipeManager recipeManager) {
        this.registryAccess = registryAccess;
        this.recipeManager = recipeManager;
    };

    @Override
    public void onResourceManagerReload(@Nonnull ResourceManager resourceManager) {
        COMPRESSIONS.clear();
        singleInputRecipes.clear(); // Retains memory size from before
        for (CompressionRecipe compression : recipeManager.getRecipes().stream()
            .map(RecipeHolder::value)
            .filter(CraftingRecipe.class::isInstance)
            .map(this::toCompressionRecipe)
            .toList()
        ) { // Close the Stream before checking for decompressions
            Iterator<Recipe<?>> iterator = singleInputRecipes.iterator();
            while (iterator.hasNext()) {
                Recipe<?> recipe = iterator.next();
                if (recipe.getIngredients().get(0).test(compression.result())) {
                    ItemStack decompressed = recipe.getResultItem(registryAccess);
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
    public CompressionRecipe toCompressionRecipe(Recipe<?> recipe) {
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        if (ingredients.size() == 0) return null;
        if (ingredients.size() == 1) {
            singleInputRecipes.add(recipe);
            return null; 
        };
        Ingredient ingredient = ingredients.get(0);
        int i;
        for (i = 1; i < ingredients.size(); i++) if (!ingredients.get(0).equals(ingredient)) return null;
        return new CompressionRecipe(ingredient, i + 1, recipe.getResultItem(registryAccess));
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
            if (
                putNewSequence(stack, () -> {
                    FinishableMapItemCompressionSequence sequence = new FinishableMapItemCompressionSequence(stack);
                    IItemCompression nextCompression = compression;
                    while (nextCompression != null) {
                        if (!sequence.add(nextCompression)) return IItemCompressionSequence.EMPTY; // Remove all circular Compression sequences
                        nextCompression = COMPRESSIONS.get(nextCompression.result());
                    };
                    return sequence.finish();
                }) instanceof ISharedItemCompressionSequence sics
            ) {
                sics.getAllItems().forEach(s -> COMPRESSION_SEQUENCES.putIfAbsent(s, sics));
            };
        });
        COMPRESSION_SEQUENCES.replaceAll((stack, sequence) -> sequence == IItemCompressionSequence.EMPTY ? null : sequence);
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

    public static interface ISharedItemCompressionSequence extends IItemCompressionSequence {};

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new ItemCompressionManager(event.getRegistryAccess(), event.getServerResources().getRecipeManager()));
    };
};
