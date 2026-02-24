package com.petrolpark.compat.create.core.recipe;

import java.util.Optional;

import com.petrolpark.compat.create.core.recipe.firsttimelucky.IFTLProcessingRecipe;
import com.petrolpark.core.recipe.IBiomeSpecificRecipe;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public class AdvancedBasinRecipe extends BasinRecipe implements IBiomeSpecificRecipe, IFTLProcessingRecipe<AdvancedBasinRecipe> {
    
    protected final Optional<HolderSet<Biome>> allowedBiomes;
    protected final Optional<ResourceLocation> firstTimeLuckyKey;

    protected AdvancedBasinRecipe(IRecipeTypeInfo typeInfo, ProcessingRecipeParams params) {
        super(typeInfo, params);
        if (params instanceof AdvancedProcessingRecipeParams properParams) {
            allowedBiomes = properParams.allowedBiomes();
            firstTimeLuckyKey = properParams.firstTimeLuckyKey();
        } else {
            throw new IllegalStateException("Not Advanced Recipe Params");
        };
    }

    @Override
    public AdvancedBasinRecipe getAsRecipe() {
        return this;
    };

    @Override
    public Optional<ResourceLocation> getFirstTimeLuckyKey() {
        return getFirstTimeLuckyKey();
    };

    @Override
    public Optional<HolderSet<Biome>> getAllowedBiomes() {
        return getAllowedBiomes();
    };
};
