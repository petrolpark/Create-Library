package com.petrolpark.recipe.advancedprocessing.firsttimelucky;

import java.util.List;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

public interface IFirstTimeLuckyRecipe<T extends ProcessingRecipe<?>> {
    
    /**
     * Give a way for {@link IFirstTimeLuckyRecipe} to convert to the proper class for this Recipe.
     * @return Should almost always be just {@code this}
     */
    T getAsRecipe();

    /**
     * Recipe-specific. Should this recipe in particular guarantee chance rewards the first time?
     */
    public boolean shouldBeLuckyFirstTime();

    /**
     * Recipe-specific. This is called by the recipe deserializer when it wants to mark this recipe as giving chance outputs the first time.
     */
    public void setLuckyFirstTime(boolean lucky);

    public default List<ItemStack> rollLuckyResults(Player player) {
        ProcessingRecipe<?> recipe = getAsRecipe();
        if (player == null) return recipe.rollResults();
        LazyOptional<FirstTimeLuckyRecipesCapability> plfrOp = player.getCapability(FirstTimeLuckyRecipesCapability.Provider.PLAYER_LUCKY_FIRST_RECIPES);
        if (!plfrOp.isPresent()) return recipe.rollResults();
        FirstTimeLuckyRecipesCapability plfr = plfrOp.resolve().get();
        if (plfr.contains(recipe.getId())) return recipe.rollResults(); // Only guarantee 100% success the first time
        plfr.add(recipe.getId()); // Record this recipe so we only get the bonus output once
        return recipe.getRollableResults().stream().map(ProcessingOutput::getStack).toList();
    };
};
