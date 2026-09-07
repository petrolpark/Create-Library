package petrolpark.mc.library.core.world.item.crafting.pocket.crafter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import petrolpark.mc.library.core.world.item.crafting.pocket.IPocketCraftingContext;
import petrolpark.mc.library.core.world.item.crafting.pocket.PocketCrafting;
import petrolpark.mc.library.core.world.item.crafting.pocket.interpretedSlot.IInterpretedSlot;

@ParametersAreNonnullByDefault
public abstract class AbstractCookingPocketCrafter<R extends AbstractCookingRecipe> extends SingleInputPocketCrafter<R> {

    @Override
    public List<RecipeHolder<? extends R>> getRecipes(IPocketCraftingContext.Client context, List<IInterpretedSlot<?>> slots, PocketCrafting.SlotArrangement slotArrangement) {
        final List<RecipeHolder<? extends R>> recipes = super.getRecipes(context, slots, slotArrangement);
        if (recipes.size() <= 1) return recipes;
        return new ArrayList<>(recipes.stream()
            .collect(Collectors.toMap(
                recipe -> recipe.value().getResultItem(context.registries()), // If multiple Recipes give the same output...
                Function.identity(),
                (r1, r2) -> r1.value().getCookingTime() > r2.value().getCookingTime() ? r1 : r2 // ...pick the faster one
            )).values());
    };

    @Override
    public int getRecipeLength(IPocketCraftingContext context, RecipeHolder<? extends R> recipe) {
        return recipe.value().getCookingTime();
    };

    @Override
    public PocketCrafting.Result craft(IPocketCraftingContext context, boolean simulate, RecipeHolder<? extends R> recipe, List<IInterpretedSlot<?>> slots, @Nullable Slot outputSlot) {
        final PocketCrafting.Result result = super.craft(context, simulate, recipe, slots, outputSlot);

        // Spawn XP
        if (!simulate && result.successful() && context.level() instanceof ServerLevel level) {
            ExperienceOrb.award(level, context.player().position(),
                Mth.floor(recipe.value().getExperience()) +
                context.level().getRandom().nextFloat() < Mth.frac(recipe.value().getExperience()) ? 1 : 0
            );
        };

        return result;
    };
    
};
