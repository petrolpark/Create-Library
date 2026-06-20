package com.petrolpark.compat.create.core.world.block.multi;

import java.util.EnumMap;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class MultiAssembler<M extends IMulti<? super M>> {

    /**
     * 
     * @param state
     * @param level
     * @param pos
     * @param face
     * @return The {@link Conversion} it is possible to do to this Block to turn it into a Multi Part Block.
     */
    public abstract Conversion canConvert(BlockState state, Level level, BlockPos pos, Direction face);

    /**
     * 
     * @param state
     * @param level
     * @param pos
     * @param conversion
     * @return The BlockState to convert the given BlockState into. This must have 
     */
    public abstract BlockState convert(BlockState state, Level level, BlockPos pos, Conversion conversion);
    
    public interface Conversion {
        public boolean cannot();
        public boolean isInside();
        public Optional<Direction> optionalSide();

        public static Conversion impossible() {
            return Results.FAILURE;
        };

        public static Conversion inside() {
            return Results.INSIDE;
        };

        public static Conversion side(Direction face) {
            return SideConversionResult.VALUES.computeIfAbsent(face, SideConversionResult::new);
        };
    };

    public static class SideConversionResult implements Conversion {

        protected static final EnumMap<Direction, SideConversionResult> VALUES = new EnumMap<>(Direction.class);

        public final Optional<Direction> face;

        protected SideConversionResult(Direction face) {
            this.face = Optional.of(face);
        };

        @Override
        public boolean cannot() {
            return false;
        };

        @Override
        public boolean isInside() {
            return false;
        };

        @Override
        public Optional<Direction> optionalSide() {
            return face;
        };
    };

    protected enum Results implements Conversion {
        FAILURE(true),
        INSIDE(false)
        ;

        public final boolean failure;

        Results(boolean failure) {
            this.failure = failure;
        };

        @Override
        public boolean cannot() {
            return failure;
        };

        @Override
        public boolean isInside() {
            return !failure;
        };

        @Override
        public Optional<Direction> optionalSide() {
            return Optional.empty();
        };
    };
};
