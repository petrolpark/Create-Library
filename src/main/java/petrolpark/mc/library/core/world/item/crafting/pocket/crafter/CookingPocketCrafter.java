package petrolpark.mc.library.core.world.item.crafting.pocket.crafter;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;

public class CookingPocketCrafter<R extends AbstractCookingRecipe> extends AbstractCookingPocketCrafter<R> {

    protected final String translationKey;
    protected final List<RecipeType<? extends R>> recipeTypes;
    protected final Supplier<ItemStack> defaultToolStack;

    public CookingPocketCrafter(String translationKey, List<RecipeType<? extends R>> recipeTypes, Supplier<ItemStack> defaultToolStack) {
        this.translationKey = translationKey;
        this.recipeTypes = recipeTypes;
        this.defaultToolStack = defaultToolStack;
    };

    @Override
    public MutableComponent getName() {
        return Component.translatable(translationKey);
    };

    @Override
    public ItemStack getDefaultToolStack() {
        return defaultToolStack.get();
    };

    @Override
    public Stream<RecipeType<? extends R>> streamRecipeTypes() {
        return recipeTypes.stream();
    };
    
};
