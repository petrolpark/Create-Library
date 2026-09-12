package petrolpark.mc.library.core.world.entity.ai.behavior;

import java.util.Optional;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.ImmutableMap;
import com.simibubi.create.content.contraptions.actors.seat.SeatBlock;
import com.simibubi.create.content.contraptions.actors.seat.SeatEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import petrolpark.mc.library.compat.Mods;
import petrolpark.mc.library.compat.create.RequiresCreate;
import petrolpark.mc.library.shared.registry.SharedMemoryModuleTypes;

@ParametersAreNonnullByDefault
@RequiresCreate
public class SitBehavior extends Behavior<LivingEntity> {

    public SitBehavior() {
        super(ImmutableMap.of(
            SharedMemoryModuleTypes.SEAT_POS.get(), MemoryStatus.VALUE_PRESENT
        ));
    };

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, LivingEntity owner) {
        if (!Mods.CREATE.isLoaded() || owner.isPassenger()) return false;
        final GlobalPos pos = owner.getBrain().getMemory(SharedMemoryModuleTypes.SEAT_POS.get()).get();
        return (pos.dimension() == owner.level().dimension() && pos.pos().closerToCenterThan(owner.position(), 1.73d));
    };

    @Override
    protected void start(ServerLevel level, LivingEntity entity, long gameTime) {
        final GlobalPos pos = entity.getBrain().getMemory(SharedMemoryModuleTypes.SEAT_POS.get()).get();
        final Level seatLevel = level.getServer().getLevel(pos.dimension());
        if (seatLevel == null) return;
        final BlockState seatState = seatLevel.getBlockState(pos.pos());
        if (!(seatState.getBlock() instanceof SeatBlock)) return;
        if (SeatBlock.isSeatOccupied(seatLevel, pos.pos())) return;
        SeatBlock.sitDown(seatLevel, pos.pos(), entity);
    };

    @Override
    protected boolean canStillUse(ServerLevel level, LivingEntity entity, long gameTime) {
        if (!entity.isPassenger()) return false;
        return isSatIfNeeded(level, entity).orElse(false);
    };

    @Override
    protected boolean timedOut(long gameTime) {
        return false;
    };

    @Override
    protected void stop(ServerLevel level, LivingEntity entity, long gameTime) {
        if (entity.getVehicle() instanceof SeatEntity seat)
            seat.ejectPassengers();
    };

    public static Optional<Boolean> isSatIfNeeded(ServerLevel level, LivingEntity entity) {
        return entity.getBrain().getMemory(SharedMemoryModuleTypes.SEAT_POS.get())
            .map(pos -> level.getServer().getLevel(pos.dimension()) == entity.level() && entity.getVehicle() instanceof SeatEntity seat && BlockPos.containing(seat.position()).equals(pos.pos()));
    };
    
};
