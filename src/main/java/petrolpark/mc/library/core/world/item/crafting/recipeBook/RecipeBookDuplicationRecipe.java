package petrolpark.mc.library.core.world.item.crafting.recipeBook;

import javax.annotation.Nonnull;

import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.registry.PetrolparkRecipeSerializers;
import petrolpark.mc.library.util.codec.CodecHelper;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class RecipeBookDuplicationRecipe extends CustomRecipe {

    public static final MapCodec<RecipeBookDuplicationRecipe> CODEC = CodecHelper.singleFieldMap(Ingredient.CODEC, "ingredient", RecipeBookDuplicationRecipe::duplicationIngredient, RecipeBookDuplicationRecipe::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, RecipeBookDuplicationRecipe> STREAM_CODEC = StreamCodec.composite(Ingredient.CONTENTS_STREAM_CODEC, RecipeBookDuplicationRecipe::duplicationIngredient, RecipeBookDuplicationRecipe::new);

    public final Ingredient duplicationIngredient;

    public RecipeBookDuplicationRecipe(Ingredient duplicationIngredient) {
        super(CraftingBookCategory.MISC);
        this.duplicationIngredient = duplicationIngredient;
    };

    public Ingredient duplicationIngredient() {
        return duplicationIngredient;
    };

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(duplicationIngredient);
    };

    @Override
    public boolean matches(@Nonnull CraftingInput input, @Nonnull Level level) {
        boolean recipeBook = false;
        boolean duplicationIngredient = false;
        for (ItemStack stack : input.items()) {
            if (stack.getItem() instanceof RecipeBookItem) {
                if (recipeBook) return false;
                if (duplicationIngredient) return true;
                recipeBook = true;
            } else if (this.duplicationIngredient.test(stack)) {
                if (duplicationIngredient) return false;
                if (recipeBook) return true;
                duplicationIngredient = true;
            };
        };
        return false;
    };

    @Override
    public ItemStack assemble(@Nonnull CraftingInput input, @Nonnull HolderLookup.Provider registries) {
        return input.items().stream().filter(stack -> stack.getItem() instanceof RecipeBookItem).findFirst().map(stack -> stack.copyWithCount(2)).orElse(ItemStack.EMPTY);
    };

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    };

    @Override
    public RecipeSerializer<RecipeBookDuplicationRecipe> getSerializer() {
        return PetrolparkRecipeSerializers.RECIPE_BOOK_DUPLICATION.get();
    };
    
};
