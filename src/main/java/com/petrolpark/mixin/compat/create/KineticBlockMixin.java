package com.petrolpark.mixin.compat.create;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.compat.create.core.block.entity.behaviour.FlagPoleBehaviour;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(KineticBlock.class)
public class KineticBlockMixin {
    
    @Inject(
        method = "Lcom/simibubi/create/content/kinetics/base/KineticBlock;setPlacedBy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V",
        at = @At("HEAD")
    )
    public void petrolpark$setFlagsFromItem(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack, CallbackInfo ci) {
        Optional.ofNullable(BlockEntityBehaviour.get(worldIn, pos, FlagPoleBehaviour.TYPE)).ifPresent(behaviour -> behaviour.setFromItem(stack));
    };
};
