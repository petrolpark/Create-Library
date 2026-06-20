package com.petrolpark.compat.create.core.world.block.multi;
// package com.petrolpark.compat.create.core.block.multi;

// import java.util.Optional;
// import java.util.function.Predicate;

// import com.simibubi.create.api.contraption.BlockMovementChecks.AttachedCheck;
// import com.simibubi.create.api.contraption.BlockMovementChecks.CheckResult;
// import com.simibubi.create.api.contraption.BlockMovementChecks.MovementAllowedCheck;
// import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

// import net.createmod.catnip.data.Iterate;
// import net.minecraft.core.BlockPos;
// import net.minecraft.core.Direction;
// import net.minecraft.world.level.Level;
// import net.minecraft.world.level.block.state.BlockState;

// public class MultiMovementChecks implements MovementAllowedCheck, AttachedCheck {

//     @SuppressWarnings("unchecked")
//     protected static final <M extends IMulti<? super M>> Optional<MultiBehaviour<M>> getMultiBehaviour(Level world, BlockPos pos) {
//         if (world.getBlockEntity(pos) instanceof SmartBlockEntity sbe) return sbe.getAllBehaviours().stream()
//             .filter(b -> b instanceof MultiBehaviour<?>)
//             .map(mb -> (MultiBehaviour<M>)mb)
//             .findAny();
//         return Optional.empty();
//     };

//     @Override
//     public CheckResult isMovementAllowed(BlockState state, Level world, BlockPos pos) {
//         return getMultiBehaviour(world, pos)
//             .flatMap(MultiBehaviour::getOptionalMulti)
//             .map(IMultiType.class::cast)
//             .map(IMultiType::isMoveable)
//             .map(CheckResult::of)
//             .orElse(CheckResult.PASS);
//     };

//     @Override
//     public CheckResult isBlockAttachedTowards(BlockState state, Level world, BlockPos pos, Direction direction) {
//         return isAttachedMulti(state, world, pos, direction, false);
//     };

//     protected static <M extends IMulti<? super M>> CheckResult isAttachedMulti(BlockState state, Level level, BlockPos pos, Direction attached, boolean cornersOnly) {

//         // Faces & inside Blocks
//         Optional<CheckResult> faceResultOp = getMultiBehaviour(level, pos).map(behaviour -> {
//             if (!behaviour.getOptionalMulti().isPresent()) return null;
//             if (behaviour instanceof IMultiSideBehaviour<?> side) return side.getMultiFace() != attached && !cornersOnly ? CheckResult.SUCCESS : CheckResult.PASS;
//             if (behaviour instanceof MultiInsidePartBehaviour<?>) return cornersOnly ? CheckResult.PASS : CheckResult.SUCCESS;
//             return null;
//         });
//         if (faceResultOp.isPresent()) return faceResultOp.get();

//         BlockPos adjacentPos = pos.relative(attached);

//         // Edges connecting to faces
//         Optional<CheckResult> edgeResultOp = getMultiBehaviour(level, adjacentPos).map(behaviour -> {
//             if (!behaviour.getOptionalMulti().isPresent()) return null;
//             if (behaviour instanceof IMultiSideBehaviour<?> side) return side.getMultiFace().getAxis() != attached.getAxis() && !cornersOnly ? CheckResult.SUCCESS : CheckResult.PASS;
//             return null;
//         });
//         if (edgeResultOp.isPresent()) return edgeResultOp.get();
        
//         // Edges connecting to corners
//         boolean sideOnAxis = false;
//         boolean sideOffAxis = false;
//         for (Direction face : Iterate.directions) {
//             if (getMultiBehaviour(level, pos.relative(face))
//                 .filter(MultiSidePartBehaviour.class::isInstance)
//                 .map(MultiSidePartBehaviour.class::cast)
//                 .map(MultiSidePartBehaviour::getMultiFace)
//                 .map(Direction::getAxis)
//                 .filter(Predicate.not(face.getAxis()::equals))
//                 .isPresent()
//             ) {
//                 if (face.getAxis() == attached.getAxis()) sideOnAxis = true; else sideOffAxis = true;
//             };
//         };
//         if (sideOffAxis && !sideOnAxis) return CheckResult.SUCCESS;

//         // Corners
//         if (!cornersOnly) return isAttachedMulti(level.getBlockState(adjacentPos), level, adjacentPos, attached.getOpposite(), true);

//         return CheckResult.PASS;
//     };
// };
