package com.petrolpark.core.world.block.ttPipe;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.petrolpark.core.world.block.ttPipe.valve.IValve;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;

public interface ITTPipeType<VALVE extends IValve<VALVE>, CAP extends ITTPipeConnectionHandler<VALVE, CAP>> {
    
    public void checkPipeCount(int pipes) throws TTPipeConnectionException;

    public VALVE getNoValve();

    public BlockCapability<CAP, @NotNull Direction> getCapability();

    public @Nullable CAP getMissingCapabilityFallback(Level level, BlockPos pos, BlockState state, Direction face);
};
