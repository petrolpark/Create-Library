package petrolpark.mc.library.mixin.compat.create;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import petrolpark.mc.library.compat.create.core.world.block.entity.basin.AdvancedBasinOperatingBlockEntity;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateRecipeTypes;
import petrolpark.mc.library.core.world.item.crafting.recipeBook.IRecipeBookAcceptorBlockEntity;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(MechanicalPressBlockEntity.class)
public abstract class MechanicalPressBlockEntityMixin extends BasinOperatingBlockEntity implements IRecipeBookAcceptorBlockEntity {

    @Unique
    protected Object advancedRecipeCacheKey = new Object();

    @Shadow
    public abstract PressingBehaviour getPressingBehaviour();

    public MechanicalPressBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        throw new AssertionError();
    };

    @Override
    protected boolean updateBasin() {
        if (
            isSpeedRequirementFulfilled()
            && getSpeed() != 0f
            && !isRunning()
            && level != null && !level.isClientSide()
            && getBasin().filter(BasinBlockEntity::canContinueProcessing).isPresent()
        ) {
            if (advancedRecipeCacheKey == null) advancedRecipeCacheKey = new Object();
            final List<Recipe<?>> juicingRecipes = AdvancedBasinOperatingBlockEntity.getMatchingRecipes(getBasin().get(), advancedRecipeCacheKey, this::matchBasinRecipe, rh -> rh.value().getType() == SharedCreateRecipeTypes.JUICING.getType());
            if (!juicingRecipes.isEmpty()) {
                currentRecipe = juicingRecipes.get(0);
                startProcessingBasin();
                sendData();
            };
        };
        return super.updateBasin();
    };

    @ModifyArg(
        method = "Lcom/simibubi/create/content/kinetics/press/MechanicalPressBlockEntity;startProcessingBasin()V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/press/PressingBehaviour;start(Lcom/simibubi/create/content/kinetics/press/PressingBehaviour$Mode;)V"
        )
    )
    public PressingBehaviour.Mode petrolpark$useMeshBasinOffset(PressingBehaviour.Mode mode) {
        if (currentRecipe != null && currentRecipe.getType() == SharedCreateRecipeTypes.JUICING.getType()) return PressingBehaviour.Mode.valueOf("PETROLPARK_MESH_BASIN");
        return mode;
    };

    @ModifyReturnValue(
        method = "Lcom/simibubi/create/content/kinetics/press/MechanicalPressBlockEntity;matchStaticFilters(Lnet/minecraft/world/item/crafting/RecipeHolder;)Z",
        at = @At("RETURN")
    )
    protected boolean petrolpark$matchJuicingRecipes(boolean original, RecipeHolder<? extends Recipe<?>> recipe) {
        return original || recipe.value().getType() == SharedCreateRecipeTypes.JUICING.getType();
    };
    
    @Override
    public void onAvailableRecipesChanged() {
        advancedRecipeCacheKey = new Object();
        basinChecker.scheduleUpdate();
    };
};
