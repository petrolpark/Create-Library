package petrolpark.mc.library.core.world.item.crafting.pocket.crafter;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import petrolpark.mc.library.core.world.item.crafting.pocket.IPocketCraftingContext;
import petrolpark.mc.library.core.world.item.crafting.pocket.ItemsPocketCraftingResult;
import petrolpark.mc.library.core.world.item.crafting.pocket.PocketCrafting;
import petrolpark.mc.library.core.world.item.crafting.pocket.interpretedSlot.IInterpretedSlot;

@ParametersAreNonnullByDefault
public abstract class SimplePocketCrafter<I extends RecipeInput, R extends Recipe<I>> implements IPocketCrafter<R> {

    public abstract Stream<RecipeType<? extends R>> streamRecipeTypes();
    
    @Nullable
    public abstract I createRecipeInput(IPocketCraftingContext context, List<IInterpretedSlot<?>> slots);

    @Override
    public boolean canCastRecipe(Recipe<?> recipe) {
        return streamRecipeTypes().anyMatch(recipe.getType()::equals);
    };

    @Override
    public List<RecipeHolder<? extends R>> getRecipes(IPocketCraftingContext.Client context, List<IInterpretedSlot<?>> slots, PocketCrafting.SlotArrangement slotArrangement) {
        final I input = createRecipeInput(context, slots);
        if (input == null) return Collections.emptyList();
        return streamRecipeTypes()
            .flatMap(type -> streamRecipesFor(context, type, input))
            .toList();
    };

    @Override
    public PocketCrafting.Result craft(IPocketCraftingContext context, boolean simulate, RecipeHolder<? extends R> recipeHolder, List<IInterpretedSlot<?>> inputSlots, @Nullable Slot outputSlot) {
        final I input = createRecipeInput(context, inputSlots);
        if (input == null) return PocketCrafting.Result.FAIL;
        final ItemStack result = getResult(context.registries(), input, recipeHolder);
        if (result.isEmpty()) return PocketCrafting.Result.FAIL;
        if (!simulate) {
            //TODO
        };
        return ItemsPocketCraftingResult.success(result);
    };

    protected final <R2 extends R> Stream<RecipeHolder<? extends R>> streamRecipesFor(IPocketCraftingContext context, RecipeType<R2> type, I input) {
        return context.recipeManager().getRecipesFor(type, input, context.level()).stream().map(Function.<RecipeHolder<? extends R>>identity());
    };

    protected final <R2 extends R> ItemStack getResult(HolderLookup.Provider registries, I input, RecipeHolder<R2> recipeHolder) {
        return recipeHolder.value().assemble(input, registries);
    };
};
