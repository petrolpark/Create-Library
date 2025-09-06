package com.petrolpark.mixin;

import javax.annotation.Nonnull;

import org.spongepowered.asm.mixin.Mixin;

import com.petrolpark.config.PetrolparkConfigs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ChiseledBookShelfBlockEntity.class)
public class ChiseledBookshelfBlockEntityMixin extends BlockEntity {

    public ChiseledBookshelfBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        throw new AssertionError();
    };
    
    @Override
    public CompoundTag getUpdateTag(@Nonnull HolderLookup.Provider registries) {
        return PetrolparkConfigs.server().syncChiseledBookshelves.get() ? saveWithoutMetadata(registries) : super.getUpdateTag(registries);
    };

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return PetrolparkConfigs.server().syncChiseledBookshelves.get() ? ClientboundBlockEntityDataPacket.create(this) : super.getUpdatePacket();
    };
};
