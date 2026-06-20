package com.petrolpark.registry;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.mojang.serialization.MapCodec;
import com.petrolpark.core.flags.FlagGlobalLootModifier;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.neoforged.neoforge.common.loot.IGlobalLootModifier;

public class PetrolparkGlobalLootModifierSerializers {
    
    public static final RegistryEntry<MapCodec<? extends IGlobalLootModifier>, MapCodec<FlagGlobalLootModifier>> FLAG_GLOBAL_LOOT_MODIFIER_SERIALZIER = REGISTRATE.globalLootModifierSerializer("flag", FlagGlobalLootModifier.CODEC);

    public static final void register() {};
};
