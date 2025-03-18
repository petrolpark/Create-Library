package com.petrolpark.recipe.advancedprocessing.firsttimelucky;

import java.util.List;

import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.create.CreateAttachmentTypes;
import com.petrolpark.recipe.ResourceLocationSet;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@RequiresCreate
public interface IFTLProcessingRecipe<T extends ProcessingRecipe<?>> {

    public final <R extends ProcessingRecipe<?> & IFTLProcessingRecipe<R>> Codec<R> = 
    
    /**
     * Give a way for {@link IFTLProcessingRecipe} to convert to the proper class for this Recipe.
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
    void setLuckyFirstTime(boolean lucky);

    public default List<ItemStack> rollLuckyResults(Player player) {
        ProcessingRecipe<?> recipe = getAsRecipe();
        if (player == null) return recipe.rollResults();
        ResourceLocationSet plfr = player.getData(CreateAttachmentTypes.FTL_RECIPES);
        if (plfr.add(recipe.id)) return recipe.getRollableResults().stream().map(ProcessingOutput::getStack).toList(); // Only guarantee 100% success the first time
        return recipe.rollResults();
    };
};
