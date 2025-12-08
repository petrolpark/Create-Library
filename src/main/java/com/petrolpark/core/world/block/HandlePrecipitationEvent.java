package com.petrolpark.core.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

/**
 * Called on a random Block in a chunk exposed to the sky every tick, for things like filling Cauldrons.
 * <p> {@link Level#isRaining()} will always be {@code true}; there is no need to check this. </p>
 * <p> If {@link ICancellableEvent#setCanceled(boolean) cancelled}, the {@link Block#handlePrecipitation(BlockState, Level, BlockPos, net.minecraft.world.level.biome.Biome.Precipitation) usual Block handling} will not be called. </p>
 * <p> This event is fired for non-motion-blocking Blocks e.g. plants, whereas the usual handling is not. </p>
 */
public class HandlePrecipitationEvent extends BlockEvent implements ICancellableEvent {

    protected final Level level;
    protected final Biome.Precipitation precipitation;

    public HandlePrecipitationEvent(Level level, BlockPos pos, BlockState state, Biome.Precipitation precipitation) {
        super(level, pos, state);
        this.level = level;
        this.precipitation = precipitation;
    };

    @Override
    public Level getLevel() {
        return level;
    };

    public Biome.Precipitation getPrecipitation() {
        return precipitation;
    };
    
};
