package petrolpark.mc.library.mixin.compat.create.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import petrolpark.mc.library.registry.PetrolparkItemDisplayContexts;
import com.simibubi.create.content.logistics.depot.DepotRenderer;

import net.minecraft.world.item.ItemDisplayContext;

@Mixin(DepotRenderer.class)
public class DepotRendererMixin {

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
