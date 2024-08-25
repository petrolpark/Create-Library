package com.petrolpark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.petrolpark.block.entity.behaviour.AbstractRememberPlacerBehaviour;
import com.petrolpark.recipe.advancedprocessing.firsttimelucky.FirstTimeLuckyRecipesBehaviour;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelBlockEntity;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlock;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(CrushingWheelControllerBlock.class)
public class CrushingWheelControllerBlockMixin {
    
    @Inject(
        method = "lambda$updateSpeed$1",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/crusher/CrushingWheelBlockEntity;award(Lcom/simibubi/create/foundation/advancement/CreateAdvancement;)V",
            ordinal = 0
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT,
        remap = false
    )
    private static void inUpdateSpeed(BlockState state, LevelAccessor level, BlockPos pos, CrushingWheelControllerBlockEntity be, CallbackInfo ci, Direction var4[], int var5, int var6, Direction d, BlockState neighbour, BlockEntity adjBE, CrushingWheelBlockEntity cwbe) {
        FirstTimeLuckyRecipesBehaviour behaviour = cwbe.getBehaviour(FirstTimeLuckyRecipesBehaviour.TYPE);
        if (behaviour != null) AbstractRememberPlacerBehaviour.setPlacedBy(be, behaviour.getPlayer());
    };
};
