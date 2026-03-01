package com.petrolpark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.petrolpark.core.world.entity.EntityFallOnEvent;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;

@Mixin(Entity.class)
public class EntityMixin {
    
    @Inject(
        method = "Lnet/minecraft/world/entity/Entity;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/Block;updateEntityAfterFallOn(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;)V"
        )
    )
    public void petrolpark$postEntityFallOnEvent(MoverType type, Vec3 pos, CallbackInfo ci, @Local BlockPos blockpos, @Local BlockState blockstate) {
        NeoForge.EVENT_BUS.post(new EntityFallOnEvent((Entity)(Object)this, blockpos, blockstate));
    };
};
