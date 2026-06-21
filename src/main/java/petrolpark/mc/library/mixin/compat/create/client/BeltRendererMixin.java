package petrolpark.mc.library.mixin.compat.create.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import petrolpark.mc.library.registry.PetrolparkItemDisplayContexts;
import com.simibubi.create.content.kinetics.belt.BeltRenderer;

import net.minecraft.world.item.ItemDisplayContext;

@Mixin(BeltRenderer.class)
public class BeltRendererMixin {

    /**
     * Use a bespoke ItemDisplayContext to render Items on Belts, Depots etc., with the base Create behaviour of using the Item Frame context as a fallback.
     * @param itemDisplayContext
     */
    @ModifyArg(
        method = "renderItem(Lcom/simibubi/create/content/kinetics/belt/BeltBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/core/Direction;Lnet/minecraft/core/Vec3i;Lcom/simibubi/create/content/kinetics/belt/BeltSlope;IZZLcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;Lnet/minecraft/world/phys/Vec3;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"
        ),
        index = 1
    )
    private static ItemDisplayContext modifyRenderOnBelt(ItemDisplayContext itemDisplayContext) {
        return PetrolparkItemDisplayContexts.BELT;
    };
};
