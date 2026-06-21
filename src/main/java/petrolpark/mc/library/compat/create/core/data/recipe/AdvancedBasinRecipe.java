package petrolpark.mc.library.compat.create.core.data.recipe;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import petrolpark.mc.library.compat.create.core.data.recipe.firstTimeLucky.IFTLProcessingRecipe;
import petrolpark.mc.library.core.data.recipe.IBiomeSpecificRecipe;
import petrolpark.mc.library.core.data.recipe.INamedRecipe;
import petrolpark.mc.library.core.world.item.crafting.recipeBook.IBookRequiredRecipe;
import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderSet;
import net.minecraft.data.PackOutput;
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

    public static abstract class Gen<R extends AdvancedBasinRecipe> extends ProcessingRecipeGen<ProcessingRecipeParams, R, AdvancedProcessingRecipe.BasinBuilder<R>> {

        public Gen(PackOutput output, CompletableFuture<Provider> registries, String defaultNamespace) {
            super(output, registries, defaultNamespace);
        };

    };
};
