package com.petrolpark.compat.create.common.processing.meshbasin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.processing.basin.BasinBlock;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRenderer;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.data.IntAttached;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;

public class MeshBasinRenderer extends BasinRenderer {

    public MeshBasinRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    };

    @Override
    protected void renderSafe(BasinBlockEntity basin, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        final MeshBasinBlockEntity meshBasin = (MeshBasinBlockEntity)basin;
        FilteringRenderer.renderOnBlockEntity(meshBasin, partialTicks, ms, buffer, light, overlay);
        renderFluids(meshBasin, partialTicks, ms, buffer, light, overlay);

        final BlockPos pos = meshBasin.getBlockPos();
        final BlockState state = meshBasin.getBlockState();

        ms.pushPose();
		ms.translate(0.5f, 0.2f, 0.5f);

		final RandomSource r = RandomSource.create(pos.hashCode());
		Vec3 baseVector = new Vec3(0.125f, 0.5f, 0);

		IItemHandlerModifiable inv = meshBasin.getItemCapability(null);
		if (inv == null) inv = new ItemStackHandler();

		int itemCount = 0;
		for (int slot = 0; slot < inv.getSlots(); slot++) if (!inv.getStackInSlot(slot).isEmpty()) itemCount++;

		if (itemCount == 1) baseVector = new Vec3(0, 0.5f, 0);

		float anglePartition = 360f / itemCount;
		for (int slot = 0; slot < inv.getSlots(); slot++) {
			final ItemStack stack = inv.getStackInSlot(slot);
			if (stack.isEmpty()) continue;

			ms.pushPose();

			final Vec3 itemPosition = VecHelper.rotate(baseVector, anglePartition * itemCount, Axis.Y);
			ms.translate(itemPosition.x, itemPosition.y, itemPosition.z);
            TransformStack.of(ms)
				.rotateYDegrees(anglePartition * itemCount + 35)
				.rotateXDegrees(65);

			for (int i = 0; i <= stack.getCount() / 8; i++) {
				ms.pushPose();

				Vec3 vec = VecHelper.offsetRandomly(Vec3.ZERO, r, 1 / 16f);

				ms.translate(vec.x, vec.y, vec.z);
				renderItem(ms, buffer, light, overlay, stack);
				ms.popPose();
			}
			ms.popPose();

			itemCount--;
		};
		ms.popPose();

		if (!(state.getBlock() instanceof MeshBasinBlock)) return;
		final Direction direction = state.getValue(BasinBlock.FACING);
		if (direction == Direction.DOWN) return;
		final Vec3 directionVec = Vec3.atLowerCornerOf(direction.getNormal());
		final Vec3 outVec = VecHelper.getCenterOf(BlockPos.ZERO).add(directionVec.scale(0.55f).subtract(0, 0.5f, 0));

        final Level level = meshBasin.getLevel();
        if (level == null) return;
		final boolean outToBasin = level.getBlockState(meshBasin.getBlockPos().relative(direction)).getBlock() instanceof BasinBlock;

		for (IntAttached<ItemStack> intAttached : meshBasin.getVisualizedOutputItems()) {
			final float progress = 1 - (intAttached.getFirst() - partialTicks) / BasinBlockEntity.OUTPUT_ANIMATION_TIME;

			if (!outToBasin && progress > 0.35f) continue;

			ms.pushPose();
            TransformStack.of(ms)
				.translate(outVec)
				.translate(new Vec3(0, Math.max(-.55f, -(progress * progress * 2)), 0))
				.translate(directionVec.scale(progress * .5f))
				.rotateYDegrees(AngleHelper.horizontalAngle(direction))
				.rotateXDegrees(progress * 180);
			renderItem(ms, buffer, light, overlay, intAttached.getValue());
			ms.popPose();
		};
    };
    
};
