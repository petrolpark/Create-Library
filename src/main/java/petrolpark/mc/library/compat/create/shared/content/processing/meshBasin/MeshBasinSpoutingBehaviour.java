package petrolpark.mc.library.compat.create.shared.content.processing.meshBasin;

import com.simibubi.create.api.behaviour.spouting.BlockSpoutingBehaviour;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import petrolpark.mc.library.compat.create.core.world.block.entity.basin.AdvancedBasinOperatingBlockEntity;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateBlockEntityTypes;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateRecipeTypes;

public class MeshBasinSpoutingBehaviour implements BlockSpoutingBehaviour {

    public static final MeshBasinSpoutingBehaviour INSTANCE = new MeshBasinSpoutingBehaviour();

    @Override
    public int fillBlock(Level level, BlockPos pos, SpoutBlockEntity spout, FluidStack availableFluid, boolean simulate) {
        return level.getBlockEntity(pos, SharedCreateBlockEntityTypes.MESH_BASIN.get()).flatMap(basin -> 
            AdvancedBasinOperatingBlockEntity.getMatchingRecipes(
                basin, basin.filteringRecipeCacheKey,
                r -> BasinRecipe.match(basin, r) && (!(r instanceof FilteringRecipe filteringRecipe) || filteringRecipe.getInputFluid().test(availableFluid)),
                MeshBasinSpoutingBehaviour::isFilteringRecipe
            ).stream()
            .<Integer>mapMulti((recipe, consumer) -> {
                if (recipe instanceof FilteringRecipe filteringRecipe) {
                    if (!simulate) BasinRecipe.apply(basin, recipe);
                    consumer.accept(filteringRecipe.getInputFluid().amount());
                };
            }).findFirst()
        ).orElse(0);
    };

    public static final boolean isFilteringRecipe(RecipeHolder<?> recipeHolder) {
        return recipeHolder.value().getType() == SharedCreateRecipeTypes.FILTERING.getType();
    };
    
};
