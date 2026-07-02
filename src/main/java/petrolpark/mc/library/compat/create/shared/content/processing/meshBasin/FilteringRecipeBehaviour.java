package petrolpark.mc.library.compat.create.shared.content.processing.meshBasin;

import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import petrolpark.mc.library.compat.create.core.world.block.entity.basin.AdvancedBasinOperatingBlockEntity;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateRecipeTypes;

public class FilteringRecipeBehaviour extends BlockEntityBehaviour {

    public static final BehaviourType<FilteringRecipeBehaviour> TYPE = new BehaviourType<>();

    protected final SpoutBlockEntity spout;
    protected final BasinOperator basinOperator;

    public FilteringRecipeBehaviour(SpoutBlockEntity be) {
        super(be);
        this.spout = be;
        this.basinOperator = new BasinOperator(null);
    };

    public BasinOperator getOperator() {
        return basinOperator;
    };

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    };

    public class BasinOperator extends AdvancedBasinOperatingBlockEntity {

        protected Object recipeCacheKey = new Object();

        public BasinOperator(BlockEntityType<?> typeIn) {
            super(typeIn, spout.getBlockPos(), spout.getBlockState());
        };

        @Override
        protected boolean isRunning() {
            return spout.processingTicks > 0;
        };

        @Override
        protected void onBasinRemoved() {
            spout.processingTicks = -1;
        };

        @Override
        protected <I extends RecipeInput> boolean matchBasinRecipe(Recipe<I> recipe) {
            if (!super.matchBasinRecipe(recipe)) return false;
            if (recipe instanceof FilteringRecipe filteringRecipe) return filteringRecipe.getInputFluid().test(spout.getBehaviour(SmartFluidTankBehaviour.TYPE).getPrimaryHandler().getFluidInTank(0));
            return true;
        };

        @Override
        protected boolean matchStaticFilters(RecipeHolder<? extends Recipe<?>> recipe) {
            return recipe.value().getType() == SharedCreateRecipeTypes.FILTERING.getType();
        };

        @Override
        protected Object getRecipeCacheKey() {
            return recipeCacheKey;
        };

        @Override
        public void updateRecipeCacheKey() {
            recipeCacheKey = new Object();
        };
        
    };
    
};
