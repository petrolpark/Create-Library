package com.petrolpark.mixin.compat.create.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.PetrolparkItemDisplayContexts;
import com.simibubi.create.content.kinetics.belt.BeltRenderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

@Mixin(BeltRenderer.class)
public class BeltRendererMixin {
    
    /**
     * Use a bespoke ItemDisplayContext to render Items on Belts, Depots etc., with the base Create behaviour of using the Item Frame context as a fallback.
     * @param renderer
     * @param stack
     * @param itemDisplayContext
     * @param leftHand
     * @param poseStack
     * @param buffer
     * @param combinedLight
     * @param combinedOverlay
     * @param model
     * @param original
     */
    @WrapOperation(
        method = "renderItem(Lcom/simibubi/create/content/kinetics/belt/BeltBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/core/Direction;Lnet/minecraft/core/Vec3i;Lcom/simibubi/create/content/kinetics/belt/BeltSlope;IZZLcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;Lnet/minecraft/world/phys/Vec3;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"
        )
    )
    private void renderOnBelt(ItemRenderer renderer, ItemStack stack, ItemDisplayContext itemDisplayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay, BakedModel model, Operation<Void> original) {
        original.call(stack, PetrolparkItemDisplayContexts.BELT, leftHand, poseStack, buffer, combinedLight, combinedOverlay, model);
    };
};
