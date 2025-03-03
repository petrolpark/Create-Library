package com.petrolpark.tube;

import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.List;

import java.util.function.Consumer;

import com.petrolpark.RequiresCreate;
import com.petrolpark.util.BlockFace;
import com.petrolpark.util.ClampedCubicSpline;
import com.petrolpark.util.MathsHelper;

import net.createmod.catnip.lang.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * A Clamped Cubic Spline with each end in the middle of a Block Face
 */
@RequiresCreate
public class TubeSpline extends ClampedCubicSpline {

    public static final double MAX_LENGTH = 32d;
    public static final double MAX_VOLUME = 256d;
    public static final int MAX_CONTROL_POINTS = 16;

    // Inputs
    public final BlockFace start;
    public final BlockFace end;

    // Input parameters
    public final double maxAngle; // Maximum angle between two segments (in radians)
    public final double segmentRadius; // Distance from a segment to the center of a block to "block" that block
    
    protected boolean tooSharp;
    protected Set<BlockPos> blockedPositions;
    protected TubePlacementResult result;

    public TubeSpline(BlockFace start, BlockFace end, double maxAngle, double segmentLength, double segmentRadius) {
        this(start, end, Collections.emptyList(), maxAngle, segmentLength, segmentRadius);
    };

    public TubeSpline(BlockFace start, BlockFace end, List<Vec3> middleControlPoints, double maxAngle, double segmentLength, double segmentRadius) {
        super(start.getCenter(), end.getCenter(), new Vec3(start.getFace().step()), new Vec3(end.getFace().getOpposite().step()), segmentLength);
        this.start = start;
        this.end = end;
        this.maxAngle = maxAngle;
        this.segmentRadius = segmentRadius;
        controlPoints.addAll(1, middleControlPoints);
        recalculate();
    };

    @Override
    public void recalculate() {
        blockedPositions = new HashSet<>();
        super.recalculate();

        // Check its not too sharp
        tooSharp = false; // Start by assuming not
        for (int i = 0; i < tangents.size() - 1; i++) {
            Vec3 t1 = tangents.get(i);
            Vec3 t2 = tangents.get(i+1);
            double dot = t1.dot(t2) / (t1.length() * t2.length());
            double angle = Math.acos(Mth.clamp(dot, -1d, 1d));
            if (angle > maxAngle) {
                tooSharp = true;
                break;
            };
        };
    };

    @Override
    protected void forEachSegment(int index, Vec3 point, Vec3 tangent) {
        BlockPos containing = BlockPos.containing(point);
        if (point.subtract(Vec3.atCenterOf(containing)).lengthSqr() < segmentRadius && !containing.equals(start.getPos()) && !containing.equals(end.getPos())) blockedPositions.add(containing);
    };

    public List<Vec3> getMiddleControlPoints() {
        return controlPoints.subList(1, controlPoints.size() - 1);
    };

    public Set<BlockPos> getBlockedPositions() {
        return blockedPositions;
    };

    @Override
    public AABB getOccupiedVolume() {
        return super.getOccupiedVolume().inflate(segmentRadius);
    };

    public TubePlacementResult getResult() {
        return result;
    };

    public void validate(Level level, Player player, Item requiredItem, ITubeBlock block) {
        result = TubePlacementResult.SUCCESS; // Start by assuming success
        BlockState startState = level.getBlockState(start.getPos());
        BlockState endState = level.getBlockState(end.getPos());
        if (startState.getBlock() != block || endState.getBlock() != block) {
            result = TubePlacementResult.WRONG_BLOCK;
            return;
        } else if (block.getTubeConnectingFace(level, start.getPos(), startState) != start.getFace() || block.getTubeConnectingFace(level, end.getPos(), endState) != end.getFace()) {
            result = TubePlacementResult.WRONG_FACE;
        } else if (controlPoints.size() > MAX_CONTROL_POINTS) {
            result = TubePlacementResult.TOO_MANY_POINTS;
        } else if (totalLength >= MAX_LENGTH) {
            result = TubePlacementResult.TOO_LONG;
        } else if (totalLength <= 1d || start.equals(end)) {
            result = TubePlacementResult.TOO_SHORT;
        } else if (MathsHelper.volume(occupiedVolume) > MAX_VOLUME) {
            result = TubePlacementResult.TOO_BIG;
        } else if (tooSharp) {
            result = TubePlacementResult.TOO_SHARP;
        } else if (!checkCanAfford(player, requiredItem, block)) {
            result = TubePlacementResult.TOO_POOR;
        } else {
            int i = 1;
            for (Vec3 controlPoint : controlPoints) {
                for (int j = i; j < controlPoints.size(); j++) {
                    if (controlPoint.distanceToSqr(controlPoints.get(j)) < segmentLength * segmentLength) {
                        result = TubePlacementResult.POINTS_TOO_CLOSE;
                        return;
                    };
                };
                i++;
            };
            checkBlocked(level, p -> {});
        };
    };

    public boolean checkBlocked(Level level, Consumer<BlockPos> forEachObstructingBlock) {
        boolean blocked = false;
        for (BlockPos pos : blockedPositions) {
            if (pos.equals(start.getPos()) || pos.equals(end.getPos())) continue;
            BlockState state = level.getBlockState(pos);
            if (state.canBeReplaced()) continue;
            //TODO maybe a tag for pass throughable blocks, and check that theres not already a pipe in the way
            forEachObstructingBlock.accept(pos);
            blocked = true;
        };
        if (blocked && (result == null || result.ordinal() > TubePlacementResult.BLOCKED.ordinal())) result = TubePlacementResult.BLOCKED;
        return blocked;
    };

    public boolean checkCanAfford(Player player, Item requiredItem, ITubeBlock block) {
        boolean canAfford = player.getAbilities().instabuild || player.getInventory().countItem(requiredItem) >= block.getItemsForTubeLength(getLength());
        if (!canAfford && (result == null || result.ordinal() > TubePlacementResult.TOO_POOR.ordinal())) result = TubePlacementResult.TOO_POOR;
        return canAfford;
    };

    public static enum TubePlacementResult {
        // In order of decreasing priority
        WRONG_BLOCK, WRONG_FACE, TOO_MANY_POINTS, TOO_LONG, TOO_SHORT, TOO_BIG, TOO_SHARP, TOO_POOR, POINTS_TOO_CLOSE, BLOCKED, SUCCESS(true),
        ;

        public final boolean success;

        TubePlacementResult() {
            this(false);
        };

        TubePlacementResult(boolean success) {
            this.success = success;
        };

        public Component translate(ItemStack stack) {
            return Component.translatable("petrolpark.tube.result."+ Lang.asId(name()), stack.getHoverName()).withStyle(success ? ChatFormatting.GREEN : ChatFormatting.RED);
        };
    };
    
};
