package com.petrolpark.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.petrolpark.compat.create.shared.content.kinetics.horseMill.HorseMillContraptionEntity;
import com.petrolpark.core.client.rendering.world.BlendedBlockColorEvent;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.NeoForge;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {

    @WrapOperation(
        method = "calculateBlockTint",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/ColorResolver;getColor",
            ordinal = 0
        )
    )
    public int petrolpark$postBlendedBlockColorEvent(ColorResolver colorResolver, Biome biome, double x, double z, Operation<Integer> original, BlockPos pos, ColorResolver colorResolverAgain) {
        return NeoForge.EVENT_BUS.post(new BlendedBlockColorEvent((ClientLevel)(Object)this, pos.immutable(), biome, colorResolver, original.call(colorResolver, biome, x, z))).getColor();
    };
    
    @WrapOperation(
        method = "calculateBlockTint",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/ColorResolver;getColor",
            ordinal = 1
        )
    )
    public int petrolpark$postBlendedBlockColorEvent(ColorResolver colorResolver, Biome biome, double x, double z, Operation<Integer> original, @Local BlockPos.MutableBlockPos pos) {
        return NeoForge.EVENT_BUS.post(new BlendedBlockColorEvent((ClientLevel)(Object)this, pos.immutable(), biome, colorResolver, original.call(colorResolver, biome, x, z))).getColor();
    };

    @WrapWithCondition(
        method = "tickPassenger",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;setOldPosAndRot()V"
        )
    )
    public boolean petrolpark$swingLegsIfRidingHorseMillContraption(Entity entity) {
        return !(entity.getVehicle() instanceof HorseMillContraptionEntity);
    };
};
