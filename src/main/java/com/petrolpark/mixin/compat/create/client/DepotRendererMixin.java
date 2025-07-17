package com.petrolpark.mixin.compat.create.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.PetrolparkItemDisplayContexts;
import com.simibubi.create.content.logistics.depot.DepotRenderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

@Mixin(DepotRenderer.class)
public class DepotRendererMixin {
    
    @WrapOperation(
        method = "Lcom/simibubi/create/content/logistics/depot/DepotRenderer;renderItem(Lnet/minecraft/world/level/Level;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/world/item/ItemStack;ILjava/util/Random;Lnet/minecraft/world/phys/Vec3;Z)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"
        )
    )
    private static void renderOnBelt(ItemRenderer renderer, ItemStack stack, ItemDisplayContext pItemDisplayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay, BakedModel model, Operation<Void> original) {
        original.call(stack, PetrolparkItemDisplayContexts.BELT, leftHand, poseStack, buffer, combinedLight, combinedOverlay, model);
    };
};
