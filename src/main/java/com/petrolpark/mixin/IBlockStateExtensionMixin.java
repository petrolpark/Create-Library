package com.petrolpark.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.petrolpark.PetrolparkAttributes;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import net.neoforged.neoforge.common.extensions.IBlockStateExtension;

@Mixin(IBlockStateExtension.class)
public interface IBlockStateExtensionMixin {
    
    @ModifyReturnValue(
        method = "Lnet/neoforged/neoforge/common/extensions/IBlockStateExtension;getFriction(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)F",
        at = @At("RETURN")
    )
    public default float petrolpark$scaleFrictionAttribute(float original, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        if (
            original == 1f
            || entity == null
            || !(entity instanceof LivingEntity livingEntity)
            || !livingEntity.getAttributes().hasAttribute(PetrolparkAttributes.SLIPPERINESS.getDelegate())
        ) {
            return original;
        } else {
            return Mth.clamp(original * (float)livingEntity.getAttributeValue(PetrolparkAttributes.SLIPPERINESS.getDelegate()), 1 / 256f / 256f, 1f);
        }
    };
};
