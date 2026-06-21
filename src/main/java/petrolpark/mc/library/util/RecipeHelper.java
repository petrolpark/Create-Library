package petrolpark.mc.library.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import petrolpark.mc.library.compat.create.core.data.recipe.firstTimeLucky.IFTLProcessingRecipe;
import petrolpark.mc.library.core.data.recipe.IBiomeSpecificRecipe;
import petrolpark.mc.library.core.world.item.crafting.recipeBook.IBookRequiredRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

public class RecipeHelper {

    private static final Random r = new Random();

    public static List<ItemStack> rollResults(RandomSource random, ProcessingRecipe<?, ?> recipe, int multiplier) {
        return rollResults(random, recipe, null, multiplier);
    };
    
    @SuppressWarnings("unchecked")
    public static List<ItemStack> rollResults(RandomSource random, ProcessingRecipe<?, ?> recipe, Player player, int multiplier) {
        List<ItemStack> results = new ArrayList<>();
        if (recipe == null) return results;
        if (recipe instanceof IFTLProcessingRecipe luckyRecipe && player != null && multiplier > 0) {
            results.addAll(luckyRecipe.rollLuckyResults(player, random));
            multiplier--;
        };
        if (multiplier <= 0) return results;
        for (ProcessingOutput output : recipe.getRollableResults()) {
            float expectedCount = (float)multiplier * output.getChance() * output.getStack().getCount();
            int count = (int)expectedCount;
            if (r.nextFloat() < expectedCount - count) count++;
            int stackSize = output.getStack().getMaxStackSize();
            for (int i = 0; i < count / stackSize; i++) results.add(output.getStack().copyWithCount(stackSize));
            results.add(output.getStack().copyWithCount(count % stackSize));
        };
        return results;
    };
    
    /**
     * Check if a Recipe has a {@link IBiomeSpecificRecipe Biome requirement} that it is fulfilled,
     * and that if it {@link IBookRequiredRecipe requires a Recipe Book}, that one is present.
     * @param recipeHolder
     * @param level
     * @param pos
     * @return Whether the checks listed above pass
     */
    public static boolean isValidAt(RecipeHolder<?> recipeHolder, Level level, BlockPos pos) {
        if (recipeHolder.value() instanceof IBiomeSpecificRecipe biomeSpecificRecipe && !biomeSpecificRecipe.isValidAt(level, pos)) return false;
        if (recipeHolder.value() instanceof IBookRequiredRecipe bookRequiredRecipe && bookRequiredRecipe.isBookRequired(level) && !IBookRequiredRecipe.hasRequiredBook(level, pos, recipeHolder)) return false;
        return true;
    };
};
