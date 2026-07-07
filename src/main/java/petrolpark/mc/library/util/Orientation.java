package petrolpark.mc.library.util;

import static petrolpark.mc.library.util.MathsHelper.VOXEL_BLOCK_CENTER;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang3.mutable.MutableObject;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Vec3i;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@ParametersAreNonnullByDefault
public enum Orientation implements StringRepresentable {

    DOWN_NORTH("down_north", Direction.DOWN, Direction.NORTH),
    DOWN_SOUTH("down_south", Direction.DOWN, Direction.SOUTH),
    DOWN_WEST("down_west", Direction.DOWN, Direction.WEST),
    DOWN_EAST("down_east", Direction.DOWN, Direction.EAST),
    UP_NORTH("up_north", Direction.UP, Direction.NORTH),
    UP_SOUTH("up_south", Direction.UP, Direction.SOUTH),
    UP_WEST("up_west", Direction.UP, Direction.WEST),
    UP_EAST("up_east", Direction.UP, Direction.EAST),
    NORTH_DOWN("north_down", Direction.NORTH, Direction.DOWN),
    NORTH_UP("north_up", Direction.NORTH, Direction.UP),
    NORTH_WEST("north_west", Direction.NORTH, Direction.WEST),
    NORTH_EAST("north_east", Direction.NORTH, Direction.EAST),
    SOUTH_DOWN("south_down", Direction.SOUTH, Direction.DOWN),
    SOUTH_UP("south_up", Direction.SOUTH, Direction.UP),
    SOUTH_WEST("south_west", Direction.SOUTH, Direction.WEST),
    SOUTH_EAST("south_east", Direction.SOUTH, Direction.EAST),
    WEST_DOWN("west_down", Direction.WEST, Direction.DOWN),
    WEST_UP("west_up", Direction.WEST, Direction.UP),
    WEST_NORTH("west_north", Direction.WEST, Direction.NORTH),
    WEST_SOUTH("west_south", Direction.WEST, Direction.SOUTH),
    EAST_DOWN("east_down", Direction.EAST, Direction.DOWN),
    EAST_UP("east_up", Direction.EAST, Direction.UP),
    EAST_NORTH("east_north", Direction.EAST, Direction.NORTH),
    EAST_SOUTH("east_south", Direction.EAST, Direction.SOUTH);
    
    public final String name;

    public final Direction top;
    public final Direction front;
    public final Direction right;

    public final Vec3 topVec;
    public final Vec3 frontVec;
    public final Vec3 rightVec;

    private static final Int2ObjectMap<Orientation> LOOKUP = Util.make(new Int2ObjectOpenHashMap<>(values().length), map -> {
        for (final Orientation orientation : values()) map.put(lookupKey(orientation.top, orientation.front), orientation);
    });

    public static final Orientation fromTopAndFront(Direction top, Direction front) {
        if (top.getAxis() == front.getAxis()) throw new IllegalArgumentException("Front and top of an orientation must be different axes");
        return LOOKUP.get(lookupKey(top, front));
    };

    private Orientation(String name, Direction top, Direction front) {
        this.name = name;
        this.top = top;
        this.front = front;

        final Vec3i rightVector = top.getNormal().cross(front.getNormal());
        right = Direction.fromDelta(rightVector.getX(), rightVector.getY(), rightVector.getZ());

        topVec = Vec3.atLowerCornerOf(top.getNormal());
        frontVec = Vec3.atLowerCornerOf(front.getNormal());
        rightVec = Vec3.atLowerCornerOf(right.getNormal());
    };

    @Override
    public String getSerializedName() {
        return name;
    };

    private static final int lookupKey(Direction top, Direction front) {
        return (top.ordinal() << 3) | front.ordinal();
    };

    public Orientation rotate(Axis axis, Rotation rotation) {
        Direction top = this.top;
        Direction front = this.front;
        for (int i = 0; i < rotation.ordinal(); i++) {
            top = top.getClockWise(axis);
            front = front.getClockWise(axis);
        };
        return fromTopAndFront(top, front);
    };

    public Orientation mirror(Mirror mirror) {
        return fromTopAndFront(mirror.mirror(top), mirror.mirror(front));
    };

    public Vec3 transform(Vec3 point) {
        return rightVec.scale(point.x()).add(topVec.scale(point.y())).add(frontVec.scale(point.z()));
    };

    public static class OrientedVoxelShaper {

        protected final Map<Orientation, VoxelShape> shapes;

        public OrientedVoxelShaper(VoxelShape shape) {
            shapes = new EnumMap<>(Orientation.class);
            for (final Orientation orientation : values()) {
                shapes.put(orientation, rotateShape(shape, orientation));
            };
        };

        public VoxelShape get(Orientation orientation) {
            return shapes.get(orientation);
        };

        public static VoxelShape rotateShape(VoxelShape shape, Orientation orientation) {
            if (orientation == UP_SOUTH) return shape;

            final MutableObject<VoxelShape> result = new MutableObject<>(Shapes.empty());

            shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> {
                final Vec3 v1 = orientation.transform(new Vec3(x1, y1, z1).scale(16d).subtract(VOXEL_BLOCK_CENTER)).add(VOXEL_BLOCK_CENTER);
                final Vec3 v2 = orientation.transform(new Vec3(x2, y2, z2).scale(16d).subtract(VOXEL_BLOCK_CENTER)).add(VOXEL_BLOCK_CENTER);

                final VoxelShape rotated = Block.box(
                    Math.min(v1.x(), v2.x()),
                    Math.min(v1.y(), v2.y()),
                    Math.min(v1.z(), v2.z()),
                    Math.max(v1.x(), v2.x()),
                    Math.max(v1.y(), v2.y()),
                    Math.max(v1.z(), v2.z())
                );
                result.setValue(Shapes.or(result.getValue(), rotated));
            });

            return result.getValue();
        };

    };
    
};

