package petrolpark.mc.library.mixin.compat.create.client;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.depot.DepotRenderer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import petrolpark.mc.library.compat.create.core.world.item.transported.SpecialTransportedItemStack;
import petrolpark.mc.library.registry.PetrolparkItemDisplayContexts;

@Mixin(DepotRenderer.class)
public class DepotRendererMixin {

    @WrapOperation(
        method = "renderItemsOf",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/logistics/depot/DepotRenderer;renderItem(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/world/item/ItemStack;ILjava/util/Random;Lnet/minecraft/world/phys/Vec3;Z)V",
            ordinal = 0
        )
    )
    private static void petrolpark$renderSpecialTransportedItemStacks(PoseStack ms, MultiBufferSource buffer, int light, int overlay, ItemStack itemStack, int angle, Random r, Vec3 itemPosition, boolean alwaysUpright, Operation<Void> original, SmartBlockEntity be, float partialTicks, @Local(ordinal = 1) TransportedItemStack stack) {
        if (stack instanceof SpecialTransportedItemStack specialStack && specialStack.renderRotated(ms, buffer, partialTicks, light, overlay, angle)) return;
        original.call(ms, buffer, light, overlay, itemStack, angle, r, itemPosition, alwaysUpright);
    };

    /**
     * Use a bespoke ItemDisplayContext to render Items on Belts, Depots etc., with the base Create behaviour of using the Item Frame context as a fallback.
     * @param itemDisplayContext
     */
    @ModifyArg(
        method = "Lcom/simibubi/create/content/logistics/depot/DepotRenderer;renderItem(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/world/item/ItemStack;ILjava/util/Random;Lnet/minecraft/world/phys/Vec3;Z)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"
        ),
        index = 1
    )
    private static ItemDisplayContext petrolpark$useBeltDisplayContext(ItemDisplayContext itemDisplayContext) {
        return PetrolparkItemDisplayContexts.BELT;
    };

};
