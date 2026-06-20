package com.petrolpark.mixin.compat.create.client;

import java.util.List;
import java.util.Map.Entry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.compat.create.shared.registry.SharedPartialModels;
import com.petrolpark.core.client.rendering.EmptySuperByteBuffer;
import com.petrolpark.core.world.item.decay.ItemDecay;
import com.petrolpark.registry.PetrolparkItemDisplayContexts;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage.ChainConveyorPackagePhysicsData;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorRenderer;
import com.simibubi.create.content.logistics.box.PackageItem;

import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

@Mixin(ChainConveyorRenderer.class)
public abstract class ChainConveyorRendererMixin {

    @Shadow
    abstract void renderBox(ChainConveyorBlockEntity be, PoseStack ms, MultiBufferSource buffer, int overlay, BlockPos pos, ChainConveyorPackage box, float partialTicks);

    @Inject(
        method = "Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorRenderer;renderSafe(Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
        at = @At("HEAD")
    )
    protected void inRenderSafe(ChainConveyorBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay, CallbackInfo ci) {
        if (VisualizationManager.supportsVisualization(be.getLevel())) { // Render non-Package Items without Flywheel Visuals no matter what
            for (ChainConveyorPackage box : be.getLoopingPackages())
                if (!PackageItem.isPackage(box.item))
                    renderBox(be, ms, buffer, overlay, be.getBlockPos(), box, partialTicks);
            for (Entry<BlockPos, List<ChainConveyorPackage>> entry : be.getTravellingPackages().entrySet())
                for (ChainConveyorPackage box : entry.getValue())
                    if (!PackageItem.isPackage(box.item))
                        renderBox(be, ms, buffer, overlay, be.getBlockPos(), box, partialTicks);
        };
    };
    
    @WrapOperation(
        method = "Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorRenderer;renderBox(Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorBlockEntity;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/core/BlockPos;Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorPackage;F)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/createmod/catnip/render/CachedBuffers;(Ldev/engine_room/flywheel/lib/model/baked/PartialModel;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/createmod/catnip/render/SuperByteBuffer;",
            ordinal = 0
        )
    )
    public SuperByteBuffer wrapGetRigBuffer(PartialModel model, BlockState referenceState, Operation<SuperByteBuffer> original, ChainConveyorBlockEntity be, PoseStack ms, MultiBufferSource buffer, int overlay, BlockPos pos, ChainConveyorPackage box, float partialTicks) {
        return PackageItem.isPackage(box.item) ? original.call(model, referenceState) : original.call(SharedPartialModels.CHAIN_CONVEYOR_HOOK, referenceState);
    };

    @WrapOperation(
        method = "Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorRenderer;renderBox(Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorBlockEntity;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/core/BlockPos;Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorPackage;F)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/createmod/catnip/render/CachedBuffers;(Ldev/engine_room/flywheel/lib/model/baked/PartialModel;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/createmod/catnip/render/SuperByteBuffer;",
            ordinal = 1
        )
    )
    public SuperByteBuffer wrapGetPackageBuffer(PartialModel model, BlockState referenceState, Operation<SuperByteBuffer> original, ChainConveyorBlockEntity be, PoseStack ms, MultiBufferSource buffer, int overlay, BlockPos pos, ChainConveyorPackage box, float partialTicks) {
        return PackageItem.isPackage(box.item) ? original.call(model, referenceState) : EmptySuperByteBuffer.EMPTY;
    };

    @Inject(
        method = "Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorRenderer;renderBox(Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorBlockEntity;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/core/BlockPos;Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorPackage;F)V",
        at = @At("TAIL")
    )
    public void inRenderBox(ChainConveyorBlockEntity be, PoseStack ms, MultiBufferSource buffer, int overlay, BlockPos pos, ChainConveyorPackage box, float partialTicks, CallbackInfo ci, @Local ChainConveyorPackagePhysicsData physicsData, @Local(ordinal = 1) float yaw, @Local(ordinal = 2) Vec3 offset, @Local(ordinal = 1) int light, @Local(ordinal = 2) float zRot, @Local(ordinal = 3) float xRot) {
        if (!PackageItem.isPackage(box.item)) {
            final Minecraft mc = Minecraft.getInstance();
            ms.pushPose();
            TransformStack.of(ms)
                .translate(offset)
			    .translate(0, 10 / 16f, 0)
			    .rotateYDegrees(yaw)
			    .rotateZDegrees(zRot)
			    .rotateXDegrees(xRot)
                .translate(0, -10 / 16f, 0)
                .scale(0.75f);
            mc.getItemRenderer().renderStatic(ItemDecay.checkDecay(box.item), PetrolparkItemDisplayContexts.DRYING_RACK, light, overlay, ms, buffer, be.getLevel(), 0);
            ms.popPose();
        };
    };
};
