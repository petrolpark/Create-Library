package petrolpark.mc.library.core.world.item.crafting.pocket.crafter;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import petrolpark.mc.library.core.world.item.crafting.pocket.IPocketCraftingContext;
import petrolpark.mc.library.core.world.item.crafting.pocket.PocketCrafting;
import petrolpark.mc.library.core.world.item.crafting.pocket.interpretedSlot.IInterpretedSlot;

@ParametersAreNonnullByDefault
public abstract class AbstractCookingPocketCrafter<R extends AbstractCookingRecipe> extends SimplePocketCrafter<SingleRecipeInput, R> {

    @Override
    public int getRecipeLength(IPocketCraftingContext context, RecipeHolder<? extends R> recipe) {
        return recipe.value().getCookingTime();
    };

    @Override
    public PocketCrafting.Result craft(IPocketCraftingContext context, boolean simulate, RecipeHolder<? extends R> recipe, List<IInterpretedSlot> slots, Slot outputSlot) {
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
