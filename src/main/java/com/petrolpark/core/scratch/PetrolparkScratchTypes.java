package com.petrolpark.core.scratch;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.client.creativemodetab.CustomTab.ITabEntry.Item;
import com.petrolpark.core.scratch.type.IScratchType;
import com.petrolpark.core.scratch.type.SimpleScratchType;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

public class PetrolparkScratchTypes {
  
    public static final RegistryEntry<IScratchType<?>, SimpleScratchType<Boolean>> BOOLEAN = REGISTRATE.scratchType("boolean", Boolean.class);
    public static final RegistryEntry<IScratchType<?>, SimpleScratchType<Integer>> INTEGER = REGISTRATE.scratchType("integer", Integer.class);
    public static final RegistryEntry<IScratchType<?>, SimpleScratchType<Double>> REAL = REGISTRATE.scratchType("real", Double.class);
    public static final RegistryEntry<IScratchType<?>, SimpleScratchType<String>> STRING = REGISTRATE.scratchType("string", String.class);
    
    public static final RegistryEntry<IScratchType<?>, SimpleScratchType<Axis>> AXIS = REGISTRATE.scratchType("axis", Axis.class);
    public static final RegistryEntry<IScratchType<?>, SimpleScratchType<Direction>> DIRECTION = REGISTRATE.scratchType("direction", Direction.class);
    public static final RegistryEntry<IScratchType<?>, SimpleScratchType<Vec3i>> VEC3I = REGISTRATE.scratchType("vec3i", Vec3i.class);
    public static final RegistryEntry<IScratchType<?>, SimpleScratchType<Vec3>> VEC3 = REGISTRATE.scratchType("vec3", Vec3.class);

    public static final RegistryEntry<IScratchType<?>, SimpleScratchType<Block>> BLOCK = REGISTRATE.scratchType("block", Block.class);
    public static final RegistryEntry<IScratchType<?>, SimpleScratchType<Item>> ITEM = REGISTRATE.scratchType("item", Item.class);
    public static final RegistryEntry<IScratchType<?>, SimpleScratchType<Entity>> ENTITY = REGISTRATE.scratchType("entity", Entity.class);

    public static final void register() {};

};
