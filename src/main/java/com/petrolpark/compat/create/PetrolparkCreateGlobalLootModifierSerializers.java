package com.petrolpark.compat.create;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.mojang.serialization.MapCodec;
import com.petrolpark.compat.create.core.loot.FlaggedKineticBlockLootModifier;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class PetrolparkCreateGlobalLootModifierSerializers {
    
    public static final RegistryEntry<MapCodec<? extends IGlobalLootModifier>, MapCodec<? extends IGlobalLootModifier>> FLAGGED_KINETIC_BLOCK_LOOT_MODIFIER_SERIALIZER = REGISTRATE.simple("flagged_kinetic_blocks", NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, () -> FlaggedKineticBlockLootModifier.CODEC);

    public static final void register() {}; 
};
