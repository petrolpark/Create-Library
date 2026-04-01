package com.petrolpark.util;

import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public record BlockAndEntity(BlockState state, @Nullable BlockEntity entity) {

    public Block block() {
        return state.getBlock();
    };

    public boolean is(TagKey<Block> tag) {
        return state().is(tag);
    };
    
    public boolean hasEntity() {
        return entity() != null;
    };

    public Optional<BlockEntity> entityOp() {
        return Optional.ofNullable(entity());
    };

    @SuppressWarnings("null")
    public BlockAndEntity withState(BlockState state) {
        return hasEntity() && entity().getType().isValid(state) ? new BlockAndEntity(state, entity) : new BlockAndEntity(state, null);
    };

    @SuppressWarnings("null")
    public Optional<BlockAndEntity> withStateOptional(BlockState state) {
        return hasEntity()
            ? (entity().getType().isValid(state) 
                ? Optional.of(new BlockAndEntity(state, entity()))
                : Optional.empty()
            )
            : Optional.of(new BlockAndEntity(state, null));
    };
};
