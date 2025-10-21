package com.petrolpark.compat.create;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.mojang.serialization.MapCodec;
import com.petrolpark.compat.create.core.loot.ContaminatedKineticBlockLootModifier;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class CreateGlobalLootModifierSerializers {
    
    public static final RegistryEntry<MapCodec<? extends IGlobalLootModifier>, MapCodec<? extends IGlobalLootModifier>> CONTAMINATED_KINETIC_BLOCK_LOOT_MODIFIER_SERIALIZER = REGISTRATE.simple("contaminated_kinetic_blocks", NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, () -> ContaminatedKineticBlockLootModifier.CODEC);

    public static final void register() {}; 
};
