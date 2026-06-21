package petrolpark.mc.library.compat.create.core.data.recipe.firstTimeLucky;

import java.util.Optional;

import petrolpark.mc.library.compat.create.RequiresCreate;
import petrolpark.mc.library.compat.create.core.data.recipe.AdvancedProcessingRecipeParams;
import com.simibubi.create.content.kinetics.millstone.MillingRecipe;

import net.minecraft.resources.ResourceLocation;

/**
 * Milling recipes which guarantee all chance outputs the first time they are done.
 */
@RequiresCreate
public class FTLMillingRecipe extends MillingRecipe implements IFTLProcessingRecipe<MillingRecipe> {

    public final Optional<ResourceLocation> firstTimeLuckyKey;

    public FTLMillingRecipe(AdvancedProcessingRecipeParams params) {
        super(params);
        firstTimeLuckyKey = params instanceof AdvancedProcessingRecipeParams advancedParams ? advancedParams.firstTimeLuckyKey() : Optional.empty();
    };

    @Override
    public Optional<ResourceLocation> getFirstTimeLuckyKey() {
        return firstTimeLuckyKey;
    };

    @Override
    public MillingRecipe getAsRecipe() {
        return this;
    };
};
