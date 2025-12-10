package com.petrolpark.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.petrolpark.client.rendering.world.BlendedBlockColorEvent;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
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
    public int wrapGetColor(ColorResolver colorResolver, Biome biome, double x, double z, Operation<Integer> original, BlockPos pos, ColorResolver colorResolverAgain) {
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
    public int wrapGetColor(ColorResolver colorResolver, Biome biome, double x, double z, Operation<Integer> original, @Local BlockPos.MutableBlockPos pos) {
        return NeoForge.EVENT_BUS.post(new BlendedBlockColorEvent((ClientLevel)(Object)this, pos.immutable(), biome, colorResolver, original.call(colorResolver, biome, x, z))).getColor();
    };
};
