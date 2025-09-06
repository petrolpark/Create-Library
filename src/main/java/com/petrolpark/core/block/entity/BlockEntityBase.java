package com.petrolpark.core.block.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
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

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    };

    @Override
    public CompoundTag getUpdateTag(@Nonnull HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    };
    
};
