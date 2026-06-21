package petrolpark.mc.library.util;

import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public record BlockStateAndEntity(BlockState state, @Nullable BlockEntity entity) {

    public static final BlockStateAndEntity of(BlockState state) {
        return new BlockStateAndEntity(state, null);
    };

    public static final BlockStateAndEntity at(BlockGetter level, BlockPos pos) {
        return new BlockStateAndEntity(level.getBlockState(pos), level.getBlockEntity(pos));
    };

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
    public BlockStateAndEntity withState(BlockState state) {
        return hasEntity() && entity().getType().isValid(state) ? new BlockStateAndEntity(state, entity) : new BlockStateAndEntity(state, null);
    };

    @SuppressWarnings("null")
    public Optional<BlockStateAndEntity> withStateOptional(BlockState state) {
        return hasEntity()
            ? (entity().getType().isValid(state) 
                ? Optional.of(new BlockStateAndEntity(state, entity()))
                : Optional.empty()
            )
            : Optional.of(new BlockStateAndEntity(state, null));
    };

    @Override
    @SuppressWarnings("null")
    public final boolean equals(Object arg0) {
        if (this == arg0) return true;
        if (!(arg0 instanceof BlockStateAndEntity other)) return false;
        return BlockHelper.equals(state(), other.state()) && (hasEntity()
            ? other.hasEntity() && entity().equals(other.entity())
            : !other.hasEntity()
        );
    };
};
