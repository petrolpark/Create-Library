package petrolpark.mc.library.core.world.item.crafting;

import java.util.function.Function;

import javax.annotation.Nonnull;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.util.codec.CodecHelper;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public abstract class WrappedCraftingRecipe implements CraftingRecipe {

    protected final CraftingRecipe wrappedRecipe;

    public CraftingRecipe getWrappedRecipe() {
        return wrappedRecipe;
    };

    public WrappedCraftingRecipe(CraftingRecipe wrappedRecipe) {
        this.wrappedRecipe = wrappedRecipe;
    };

    @Override
    public boolean matches(@Nonnull CraftingInput input, @Nonnull Level level) {
        return wrappedRecipe.matches(input, level);
    };

    @Override
    public ItemStack assemble(@Nonnull CraftingInput input, @Nonnull HolderLookup.Provider registries) {
        return wrappedRecipe.assemble(input, registries);
    };

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return wrappedRecipe.canCraftInDimensions(width, height);
    };

    @Override
    public ItemStack getResultItem(@Nonnull HolderLookup.Provider registries) {
        return wrappedRecipe.getResultItem(registries);
    };

    @Override
    public NonNullList<ItemStack> getRemainingItems(@Nonnull CraftingInput input) {
        return wrappedRecipe.getRemainingItems(input);
    };

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return wrappedRecipe.getIngredients();
    };

    @Override
    public boolean isSpecial() {
        return wrappedRecipe.isSpecial();
    };

    @Override
    public boolean showNotification() {
        return wrappedRecipe.showNotification();
    };

    @Override
    public String getGroup() {
        return wrappedRecipe.getGroup();
    };

    @Override
    public ItemStack getToastSymbol() {
        return wrappedRecipe.getToastSymbol();
    };

    @Override
    public CraftingBookCategory category() {
        return wrappedRecipe.category();
    };

    public static final <RECIPE extends WrappedCraftingRecipe> NonNullSupplier<RecipeSerializer<RECIPE>> serializer(Function<CraftingRecipe, RECIPE> factory) {

        final MapCodec<RECIPE> codec = CodecHelper.singleFieldMap(
            Recipe.CODEC.comapFlatMap(r -> r instanceof CraftingRecipe craftingRecipe ? DataResult.success(craftingRecipe) : DataResult.error(() -> String.format("{} is not a Crafting Recipe Type", r.getType())), Function.identity()),
            "recipe", WrappedCraftingRecipe::getWrappedRecipe,
            factory
        );

        final StreamCodec<RegistryFriendlyByteBuf, RECIPE> streamCodec = StreamCodec.composite(
            Recipe.STREAM_CODEC.map(recipe -> {
                if (recipe instanceof CraftingRecipe craftingRecipe) return craftingRecipe;
                throw new IllegalStateException(String.format("{} is not a Crafting Recipe Type", recipe.getType()));
            }, Function.identity()), WrappedCraftingRecipe::getWrappedRecipe,
            factory
        );

        return () -> new RecipeSerializer<RECIPE>() {
            
            @Override
            public MapCodec<RECIPE> codec() {
                return codec;
            };

            @Override
            public StreamCodec<RegistryFriendlyByteBuf,RECIPE> streamCodec() {
                return streamCodec;
            };
        };
    };
    
};

