package com.petrolpark.compat.create.core.recipe;

import java.util.Optional;
import java.util.stream.Stream;

import com.petrolpark.compat.create.core.recipe.firsttimelucky.IFTLProcessingRecipe;
import com.petrolpark.core.recipe.IBiomeSpecificRecipe;
import com.petrolpark.core.recipe.INamedRecipe;
import com.petrolpark.core.recipe.book.IBookRequiredRecipe;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.fluids.FluidStack;

public class AdvancedBasinRecipe extends BasinRecipe implements IBiomeSpecificRecipe, IFTLProcessingRecipe<AdvancedBasinRecipe>, IBookRequiredRecipe {
    
    protected final Optional<HolderSet<Biome>> allowedBiomes;
    protected final Optional<ResourceLocation> firstTimeLuckyKey;
    protected final boolean bookRequired;

    protected Component name;

    protected AdvancedBasinRecipe(IRecipeTypeInfo typeInfo, ProcessingRecipeParams params) {
        super(typeInfo, params);
        if (params instanceof AdvancedProcessingRecipeParams properParams) {
            allowedBiomes = properParams.allowedBiomes();
            firstTimeLuckyKey = properParams.firstTimeLuckyKey();
            bookRequired = properParams.bookRequired;
        } else {
            throw new IllegalStateException("Not Advanced Recipe Params");
        };
    };

    public boolean isForMeshBasin() {
        return false;
    };

    @Override
    public AdvancedBasinRecipe getAsRecipe() {
        return this;
    };

    @Override
    public Optional<ResourceLocation> getFirstTimeLuckyKey() {
        return firstTimeLuckyKey;
    };

    @Override
    public Optional<HolderSet<Biome>> getAllowedBiomes() {
        return allowedBiomes;
    };

    protected void setName(Component name) {
        this.name = name;
    };

    @Override
    public Component getName(ResourceLocation recipeId) {
        return INamedRecipe.cacheDefaultName(name, this::setName, recipeId, Stream.concat(getRollableResults().stream().map(ProcessingOutput::getStack).map(ItemStack::getHoverName), getFluidResults().stream().map(FluidStack::getHoverName))::toList);
    };

    @Override
    public boolean isBookRequired(Level level) {
        return bookRequired;
    };
};
