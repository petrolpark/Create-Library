package com.petrolpark.mixin.compat.create;

import java.util.Iterator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.petrolpark.compat.create.core.world.item.directional.DirectionalTransportedItemStack;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour.TransportedResult;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

@Mixin(Contraption.class)
public class ContraptionMixin {
    
    /**
     * Ensure directional Item Stacks on belts have the right orientation when a Contraption is converted back into blocks.
     */
    @Inject(
        method = "Lcom/simibubi/create/content/contraptions/Contraption;addBlocksToWorld(Lnet/minecraft/world/level/Level;Lcom/simibubi/create/content/contraptions/StructureTransform;)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/contraptions/StructureTransform;apply(Lnet/minecraft/world/level/block/entity/BlockEntity;)V"
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT,
        remap = false
    )
    public void petrolpark$rotateDirectionalStacks(Level world, StructureTransform transform, CallbackInfo ci, boolean var2, boolean var3[], int var4, int var5, boolean nonBrittles, Iterator<StructureBlockInfo> var7, StructureBlockInfo block, BlockPos targetPos, BlockState state, BlockState blockState, boolean verticalRotation, BlockEntity blockEntity) {
        if (blockEntity instanceof SmartBlockEntity sbe) {
            TransportedItemStackHandlerBehaviour behaviour = sbe.getBehaviour(TransportedItemStackHandlerBehaviour.TYPE);
            if (behaviour != null) behaviour.handleProcessingOnAllItems(stack -> {
                if (stack instanceof DirectionalTransportedItemStack directionalStack) {
                    if (transform.rotationAxis == Axis.Y) directionalStack.rotate(transform.rotation);
                    return TransportedResult.convertTo(directionalStack);
                } else {
                    return TransportedResult.doNothing();
                }
            });
        };
    };
};
