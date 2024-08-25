package com.petrolpark.mixin;

import java.util.List;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.petrolpark.recipe.advancedprocessing.firsttimelucky.FirstTimeLuckyRecipesBehaviour;
import com.petrolpark.recipe.advancedprocessing.firsttimelucky.IFirstTimeLuckyRecipe;
import com.simibubi.create.content.kinetics.crusher.AbstractCrushingRecipe;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.wrapper.RecipeWrapper;

@Mixin(CrushingWheelControllerBlockEntity.class)
public abstract class CrushingWheelControllerBlockEntityMixin extends SmartBlockEntity {


    //TODO get the player in the controller block entity

    public CrushingWheelControllerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        throw new AssertionError();
    };

    @Inject(
        method = "Lcom/simibubi/create/content/kinetics/crusher/CrushingWheelControllerBlockEntity;addBehaviours(Ljava/util/List;)V",
        at = @At("RETURN"),
        remap = false
    )
    public void inAddBehaviours(List<BlockEntityBehaviour> behaviours, CallbackInfo ci) {
        behaviours.add(new FirstTimeLuckyRecipesBehaviour(this, r -> r instanceof AbstractCrushingRecipe));
    };

    @Inject(
        method = "Lcom/simibubi/create/content/kinetics/crusher/CrushingWheelControllerBlockEntity;applyRecipe()V",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;size()I",
            ordinal = 0
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT,
        remap = false
    )
    @SuppressWarnings("unchecked")
    public void inApplyRecipe(CallbackInfo ci, Optional<ProcessingRecipe<RecipeWrapper>> recipe, List<ItemStack> list, int rolls, int roll, List<ItemStack> rolledResults, int i) {
        if (i == 0) {
            FirstTimeLuckyRecipesBehaviour behaviour = getBehaviour(FirstTimeLuckyRecipesBehaviour.TYPE);
            if (behaviour != null && recipe.get() instanceof IFirstTimeLuckyRecipe ftlr) {
                List<ItemStack> results = ftlr.rollLuckyResults(behaviour.getPlayer());
                rolledResults.clear();
                rolledResults.addAll(results);
            };
        };
    };
    
    
};
