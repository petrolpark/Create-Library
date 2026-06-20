package com.petrolpark.compat.create.core.world.item.directional;

import java.util.function.Function;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.compat.create.RequiresCreate;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@RequiresCreate
public class DirectionalTransportedItemStack extends TransportedItemStack {

    @Nullable
    protected Rotation rotation; // Rotation from South

    public DirectionalTransportedItemStack(ItemStack stack) {
        super(stack);
        rotation = stack.getItem() instanceof IDirectionalOnBelt item ? item.rotationForPlacement(stack) : null;
        if (rotation == null) rotation = Rotation.NONE;
        refreshAngle();
    };

    @Override
    public float getTargetSideOffset() {
        return 0f;
    };

    public Rotation getRotation() {
        return rotation;
    };

    public void rotate(Rotation appliedRotation) {
        if (rotation != null) rotation = appliedRotation.getRotated(rotation);
        refreshAngle();
    };

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
        refreshAngle();
    };

    public void refreshAngle() {
        if (rotation != null) angle = getTargetAngle();
    };

    public final int getTargetAngle() {
        switch (rotation) {
            case NONE: return 180;
            case CLOCKWISE_90: return 90;
            case CLOCKWISE_180: return 0;
            case COUNTERCLOCKWISE_90: return 270;
            case null:
            default: return 0;
        }
    };

    @Override
    public TransportedItemStack getSimilar() {
		return copy(this);
	};

    @Override
	public TransportedItemStack copy() {
		return copy(this);
	};

    /**
     * Return {@code true} to not call the usual rendering code for TransportedItemStack.
     * @param itemRenderer
     * @param ms
     * @param buffer
     * @param light
     * @param overlay
     */
    @OnlyIn(Dist.CLIENT)
    public boolean overrideRender(ItemRenderer itemRenderer, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        return false;
    };

    public static DirectionalTransportedItemStack copy(TransportedItemStack transportedItemStack) {
        return copy(transportedItemStack, DirectionalTransportedItemStack::new);
    };

    public static <DTIS extends DirectionalTransportedItemStack> DTIS copy(TransportedItemStack transportedItemStack, Function<ItemStack, DTIS> dtisFactory) {
        DTIS copy = dtisFactory.apply(transportedItemStack.stack.copy());
        // Copied from Create
		copy.beltPosition = transportedItemStack.beltPosition;
		copy.insertedAt = transportedItemStack.insertedAt;
		copy.insertedFrom = transportedItemStack.insertedFrom;
		copy.prevBeltPosition = transportedItemStack.prevBeltPosition;
		copy.prevSideOffset = transportedItemStack.prevSideOffset;
		copy.processedBy = transportedItemStack.processedBy;
		copy.processingTime = transportedItemStack.processingTime;
        //
        if (transportedItemStack instanceof DirectionalTransportedItemStack directionalStack) {
            if (directionalStack.rotation != null) copy.rotation = directionalStack.rotation;
            copy.refreshAngle();
        };
        return copy;
    };

    public static DirectionalTransportedItemStack copyFully(TransportedItemStack transportedItemStack) {
        return copyFully(transportedItemStack, DirectionalTransportedItemStack::new);
    };

    public static <DTIS extends DirectionalTransportedItemStack> DTIS copyFully(TransportedItemStack transportedItemStack, Function<ItemStack, DTIS> dtisFactory) {
        DTIS copy = copy(transportedItemStack, dtisFactory);
        copy.locked = transportedItemStack.locked;
        copy.lockedExternally = transportedItemStack.lockedExternally;
        return copy;
    };

    @Override
	public CompoundTag serializeNBT(HolderLookup.Provider registries) {
		CompoundTag nbt = super.serializeNBT(registries);
        if (rotation != null) nbt.putInt("Rotation", rotation.ordinal());
        return nbt;
	};
    
};
