package com.petrolpark.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.petrolpark.recipe.advancedprocessing.firsttimelucky.IFirstTimeLuckyRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class RecipeHelper {

    private static final Random r = new Random();

    public static List<ItemStack> rollResults(ProcessingRecipe<?> recipe, int multiplier) {
        return rollResults(recipe, null, multiplier);
    };
    
    @SuppressWarnings("unchecked")
    public static List<ItemStack> rollResults(ProcessingRecipe<?> recipe, Player player, int multiplier) {
        List<ItemStack> results = new ArrayList<>();
        if (recipe == null) return results;
        if (recipe instanceof IFirstTimeLuckyRecipe luckyRecipe && player != null && multiplier > 0) {
            results.addAll(luckyRecipe.rollLuckyResults(player));
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
};
