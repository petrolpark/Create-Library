package com.petrolpark.core.world.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.EntityEvent;

/**
 * @see Block#updateEntityAfterFallOn(net.minecraft.world.level.BlockGetter, Entity)
 */
public class EntityFallOnEvent extends EntityEvent {
    
    protected final BlockPos pos;
    protected final BlockState state;

    public EntityFallOnEvent(Entity entity, BlockPos pos, BlockState state) {
        super(entity);
        this.pos = pos;
        this.state = state;
    };

    public Level getLevel() {
        return getEntity().level();
    };

    public BlockPos getPos() {
        return pos;
    };

    public BlockState getState() {
        return state;
    };
};
