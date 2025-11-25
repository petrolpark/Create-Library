package com.petrolpark.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;

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

    private static final Int2ObjectMap<Orientation> LOOKUP = Util.make(new Int2ObjectOpenHashMap<>(values().length), map -> {
        for (Orientation orientation : values()) map.put(lookupKey(orientation.top, orientation.front), orientation);
    });

    public static final Orientation fromTopAndFront(Direction top, Direction front) {
        if (top.getAxis() == front.getAxis()) throw new IllegalArgumentException("Front and top of an orientation must be different axes");
        return LOOKUP.get(lookupKey(top, front));
    };

    private Orientation(String name, Direction top, Direction front) {
        this.name = name;
        this.top = top;
        this.front = front;
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
};

