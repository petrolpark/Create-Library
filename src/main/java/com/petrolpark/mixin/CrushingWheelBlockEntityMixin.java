package com.petrolpark.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.recipe.advancedprocessing.firsttimelucky.FirstTimeLuckyRecipesBehaviour;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.crusher.AbstractCrushingRecipe;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(CrushingWheelBlockEntity.class)
public abstract class CrushingWheelBlockEntityMixin extends KineticBlockEntity {
    
    public CrushingWheelBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        throw new AssertionError(); // Should never be called
    };

    @Inject(
        method = "Lcom/simibubi/create/content/kinetics/crusher/CrushingWheelBlockEntity;addBehaviours(Ljava/util/List;)V",
        at = @At("RETURN"),
        remap = false
    )
    public void inAddBehaviours(List<BlockEntityBehaviour> behaviours, CallbackInfo ci) {
        behaviours.add(new FirstTimeLuckyRecipesBehaviour(this, r -> r instanceof AbstractCrushingRecipe));
    };
};
