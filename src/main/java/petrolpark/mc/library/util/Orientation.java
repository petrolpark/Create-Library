package petrolpark.mc.library.util;

import static petrolpark.mc.library.util.MathsHelper.VOXEL_BLOCK_CENTER;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang3.mutable.MutableObject;
import org.joml.Matrix3f;
import org.joml.Quaternionf;

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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@ParametersAreNonnullByDefault
public enum Orientation implements StringRepresentable {

    DOWN_NORTH("down_north", Direction.DOWN, Direction.NORTH, 180, 0),
    DOWN_SOUTH("down_south", Direction.DOWN, Direction.SOUTH, 180, 180),
    DOWN_WEST("down_west", Direction.DOWN, Direction.WEST, 180, 270),
    DOWN_EAST("down_east", Direction.DOWN, Direction.EAST, 180, 90),
    UP_NORTH("up_north", Direction.UP, Direction.NORTH, 0, 180),
    UP_SOUTH("up_south", Direction.UP, Direction.SOUTH, 0, 0),
    UP_WEST("up_west", Direction.UP, Direction.WEST, 0, 90),
    UP_EAST("up_east", Direction.UP, Direction.EAST, 0, 270),
    NORTH_DOWN("north_down", Direction.NORTH, Direction.DOWN, 270, 270),
    NORTH_UP("north_up", Direction.NORTH, Direction.UP, 90, 270),
    NORTH_WEST("north_west", Direction.NORTH, Direction.WEST, 180, 270),
    NORTH_EAST("north_east", Direction.NORTH, Direction.EAST, 0, 270),
    SOUTH_DOWN("south_down", Direction.SOUTH, Direction.DOWN, 270, 90),
    SOUTH_UP("south_up", Direction.SOUTH, Direction.UP, 90, 90),
    SOUTH_WEST("south_west", Direction.SOUTH, Direction.WEST, 0, 90),
    SOUTH_EAST("south_east", Direction.SOUTH, Direction.EAST, 180, 90),
    WEST_DOWN("west_down", Direction.WEST, Direction.DOWN, 270, 180),
    WEST_UP("west_up", Direction.WEST, Direction.UP, 90, 180),
    WEST_NORTH("west_north", Direction.WEST, Direction.NORTH, 0, 180),
    WEST_SOUTH("west_south", Direction.WEST, Direction.SOUTH, 180, 180),
    EAST_DOWN("east_down", Direction.EAST, Direction.DOWN, 270, 0),
    EAST_UP("east_up", Direction.EAST, Direction.UP, 90, 0),
    EAST_NORTH("east_north", Direction.EAST, Direction.NORTH, 180, 0),
    EAST_SOUTH("east_south", Direction.EAST, Direction.SOUTH, 0, 0);

    public final String name;

    public final Direction top;
    public final Direction front;
    public final Direction right;

    /**
     * The blockstate {@code x} then {@code y} rotation (each a multiple of 90) rendering this orientation from a
     * model authored as {@link #UP_SOUTH} (if {@link #top} is vertical) or {@link #EAST_SOUTH} (otherwise)
     */
    public final int blockStateXRotation;
    public final int blockStateYRotation;

    public final Vec3 topVec;
    public final Vec3 frontVec;
    public final Vec3 rightVec;

    /** Rotation from the default {@code UP_SOUTH} orientation to this orientation. */
    public final Quaternionf rotationFromUpSouth;

    private static final Int2ObjectMap<Orientation> LOOKUP = Util.make(new Int2ObjectOpenHashMap<>(values().length), map -> {
        for (final Orientation orientation : values()) map.put(lookupKey(orientation.top, orientation.front), orientation);
    });

    private static final Int2ObjectMap<Orientation> EDGE_ORIENTATION_LOOKUP = Util.make(new Int2ObjectOpenHashMap<>(values().length), map -> {
        for (final Orientation orientation : values())
            map.put(lookupKey(orientation.top, orientation.front), orientation.top.getAxis().ordinal() < orientation.front.getAxis().ordinal()
                ? orientation
                : fromTopAndFront(orientation.front, orientation.top)
            );
    });

    public static final Orientation[] EDGE_ORIENTATIONS = Stream.of(values()).filter(o -> o.top.getAxis().ordinal() < o.front.getAxis().ordinal()).toArray(Orientation[]::new);

    public static final EnumProperty<Orientation> ORIENTATION_PROPERTY = EnumProperty.create("orientation", Orientation.class);
    public static final EnumProperty<Orientation> EDGE_ORIENTATION_PROPERTY = EnumProperty.create("orientation", Orientation.class, orientation -> orientation.top.getAxis().ordinal() < orientation.front.getAxis().ordinal());

    public static final Orientation fromTopAndFront(Direction top, Direction front) {
        if (top.getAxis() == front.getAxis()) throw new IllegalArgumentException("Front and top of an orientation must be different axes");
        return LOOKUP.get(lookupKey(top, front));
    };

    private Orientation(String name, Direction top, Direction front, int x, int y) {
        this.name = name;
        this.top = top;
        this.front = front;
        this.blockStateXRotation = x;
        this.blockStateYRotation = y;

        final Vec3i rightVector = top.getNormal().cross(front.getNormal());
        right = Direction.fromDelta(rightVector.getX(), rightVector.getY(), rightVector.getZ());

        topVec = Vec3.atLowerCornerOf(top.getNormal());
        frontVec = Vec3.atLowerCornerOf(front.getNormal());
        rightVec = Vec3.atLowerCornerOf(right.getNormal());

        rotationFromUpSouth = new Quaternionf().setFromNormalized(new Matrix3f(
            (float) rightVec.x(), (float) rightVec.y(), (float) rightVec.z(),
            (float) topVec.x(), (float) topVec.y(), (float) topVec.z(),
            (float) frontVec.x(), (float) frontVec.y(), (float) frontVec.z()
        ));
    };

    @Override
    public String getSerializedName() {
        return name;
    };

    private static final int lookupKey(Direction top, Direction front) {
        return (top.ordinal() << 3) | front.ordinal();
    };

    public Direction[] topAndFront() {
        return new Direction[]{top, front};
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

    public Orientation asEdge() {
        return EDGE_ORIENTATION_LOOKUP.get(lookupKey(top, front));
    };

    /**
     * The {@link Orientation} whose {@link #blockStateXRotation}/{@link #blockStateYRotation} to use when rendering
     * this {@link Orientation} from a single model authored as {@link #EAST_SOUTH}. For most orientations this is
     * just {@code this}, but {@code UP_SOUTH}, {@code UP_NORTH}, {@code DOWN_SOUTH} and {@code DOWN_NORTH} aren't
     * reachable from {@code EAST_SOUTH} at all, so their front/top-swapped counterpart is used instead - which
     * renders the correct edge, but with "front" and "top" swapped relative to this orientation's true pose.
     */
    public Orientation asEdgeBlockStateRotation() {
        return switch (this) {
            case UP_SOUTH -> SOUTH_UP;
            case UP_NORTH -> NORTH_UP;
            case DOWN_SOUTH -> SOUTH_DOWN;
            case DOWN_NORTH -> NORTH_DOWN;
            default -> this;
        };
    };

    public Vec3 transform(Vec3 point) {
        return rightVec.scale(point.x()).add(topVec.scale(point.y())).add(frontVec.scale(point.z()));
    };

    public boolean isTopVertical() {
        return top.getAxis() == Axis.Y;
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

