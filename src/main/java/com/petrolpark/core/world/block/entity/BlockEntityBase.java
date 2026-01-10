package com.petrolpark.core.world.block.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Substitute for {@link SmartBlockEntity} if Create is not a dependency.
 */
public class BlockEntityBase extends BlockEntity {

    public BlockEntityBase(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    };

    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.loadAdditional(tag, registries);
    };

    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.saveAdditional(tag, registries);
    };

    public void sendData() {
		if (level instanceof ServerLevel serverLevel) serverLevel.getChunkSource().blockChanged(getBlockPos());
	};

	public void notifyUpdate() {
		setChanged();
		sendData();
	};

    @Override
    protected final void loadAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        read(tag, registries, false);
    };

    @Override
    protected final void saveAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        write(tag, registries, false);
    };

    @Override
    public final CompoundTag getUpdateTag(@Nonnull HolderLookup.Provider registries) {
        final CompoundTag tag = new CompoundTag();
        write(tag, registries, true);
        return tag;
    };

    @Override
    @Nullable
    public final Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    };

    @Override
    public final void handleUpdateTag(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider lookupProvider) {
        read(tag, lookupProvider, true);
    };

    @Override
    public void onDataPacket(@Nonnull Connection net, @Nonnull ClientboundBlockEntityDataPacket pkt, @Nonnull HolderLookup.Provider registries) {
        read(pkt.getTag() == null ? new CompoundTag() : pkt.getTag(), registries, true);
    };
    
};
