package com.petrolpark.compat.create.core.recipe.firsttimelucky;

import java.util.List;
import java.util.Optional;

import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.create.PetrolparkCreateAttachmentTypes;
import com.petrolpark.core.data.ResourceLocationSet;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@RequiresCreate
public interface IFTLProcessingRecipe<T extends ProcessingRecipe<?, ?>> {
    
    /**
     * Give a way for {@link IFTLProcessingRecipe} to convert to the proper class for this Recipe.
     * @return Should almost always be just {@code this}
     */
    T getAsRecipe();

    public Optional<ResourceLocation> getFirstTimeLuckyKey();

    public default List<ItemStack> rollLuckyResults(Player player, RandomSource random) {
        ProcessingRecipe<?, ?> recipe = getAsRecipe();
        Optional<ResourceLocation> key = getFirstTimeLuckyKey();
        if (key.isEmpty() || player == null) return recipe.rollResults(random);
        ResourceLocationSet plfr = player.getData(PetrolparkCreateAttachmentTypes.FTL_RECIPES);
        if (plfr.add(key.get())) return recipe.getRollableResults().stream().map(ProcessingOutput::getStack).toList(); // Only guarantee 100% success the first time
        return recipe.rollResults(random);
    };

    public default List<ItemStack> rollLuckyResults(SmartBlockEntity blockEntity, RandomSource random) {
        FTLRecipesBehaviour behaviour = blockEntity.getBehaviour(FTLRecipesBehaviour.TYPE);
        if (behaviour != null) return rollLuckyResults(behaviour.getPlayer(), random);
        return getAsRecipe().rollResults(random);
    };
};
