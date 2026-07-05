package petrolpark.mc.library.mixin.compat.create.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltRenderer;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import petrolpark.mc.library.compat.create.core.world.item.transported.SpecialTransportedItemStack;
import petrolpark.mc.library.registry.PetrolparkItemDisplayContexts;

@Mixin(BeltRenderer.class)
public class BeltRendererMixin {

    @Inject(
        method = "renderItem(Lcom/simibubi/create/content/kinetics/belt/BeltBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/core/Direction;Lnet/minecraft/core/Vec3i;Lcom/simibubi/create/content/kinetics/belt/BeltSlope;IZZLcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;Lnet/minecraft/world/phys/Vec3;)V",
        at = @At(
            value = "INVOKE",
            target = "translate",
            ordinal = 4,
            shift = Shift.BY,
            by = 5
        ),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION,
        cancellable = true
    )
    private void petrolpark$renderSpecialTransportedStacks(BeltBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light,
		int overlay, Direction beltFacing, Vec3i directionVec, BeltSlope slope, int verticality, boolean slopeAlongX,
		boolean onContraption, TransportedItemStack transported, Vec3 beltStartOffset,
        CallbackInfo ci,
        Minecraft mc, ItemRenderer itemRenderer, MutableBlockPos mutablePos, float offset, float sideOffset, float verticalMovement, Vec3 offsetVec, boolean onSlope, boolean tiltForward, float slopeAngle, Vec3 itemPos, boolean alongX, int stackLight
        
    ) {
        if (transported instanceof SpecialTransportedItemStack specialStack && specialStack.renderRotated(ms, buffer, partialTicks, stackLight, overlay, transported.angle)) {
            ms.popPose();
            ci.cancel();
        };
    };

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
    private static ItemDisplayContext petrolpark$useBeltItemDisplayContext(ItemDisplayContext itemDisplayContext) {
        return PetrolparkItemDisplayContexts.BELT;
    };
};
