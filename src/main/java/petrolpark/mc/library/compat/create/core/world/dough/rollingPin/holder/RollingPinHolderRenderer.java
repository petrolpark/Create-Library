package petrolpark.mc.library.compat.create.core.world.dough.rollingPin.holder;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import petrolpark.mc.library.compat.create.shared.registry.SharedPartialModels;

public class RollingPinHolderRenderer extends KineticBlockEntityRenderer<RollingPinHolderBlockEntity> {

    final ItemRenderer itemRenderer;

    public RollingPinHolderRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    };

    @Override
    protected void renderSafe(RollingPinHolderBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        final BlockState state = be.getBlockState();
        final VertexConsumer vc = buffer.getBuffer(RenderType.solid());
        final Direction facing = Direction.get(AxisDirection.POSITIVE, state.getValue(RollingPinHolderBlock.HORIZONTAL_AXIS));
        
        final float armsOffset = -10 / 16f * be.armsExtension.getValue(partialTicks);

        CachedBuffers.partialFacing(SharedPartialModels.ROLLING_PIN_HOLDER_ARMS, state, facing)
            .translateY(armsOffset)
            .light(light)
            .renderInto(ms, vc);

        if (be.rollingPin.isEmpty()) return;

        final BakedModel bakedModel = itemRenderer.getModel(be.rollingPin, be.getLevel(), null, 0);

        ms.pushPose();
        TransformStack.of(ms)
            .center()
            .translateY(-12 / 16f + armsOffset)
            .rotateYDegrees(state.getValue(RollingPinHolderBlock.HORIZONTAL_AXIS) == Axis.X ? 180f : 90f)
            .rotateXDegrees(90f)
            .rotateY(getAngleForBe(be, be.getBlockPos(), facing.getAxis()))
            ;
        itemRenderer.render(be.rollingPin, ItemDisplayContext.NONE, false, ms, buffer, light, overlay, bakedModel);
        ms.popPose();
    };

    @Override
    protected SuperByteBuffer getRotatedModel(RollingPinHolderBlockEntity be, BlockState state) {
        return CachedBuffers.partialFacingVertical(AllPartialModels.SHAFTLESS_COGWHEEL, state, Direction.get(AxisDirection.POSITIVE, state.getValue(RollingPinHolderBlock.HORIZONTAL_AXIS)));
    };
    
};
