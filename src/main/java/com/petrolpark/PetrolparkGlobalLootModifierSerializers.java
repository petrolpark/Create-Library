package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.mojang.serialization.MapCodec;
import com.petrolpark.contamination.ContaminateGlobalLootModifier;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.neoforged.neoforge.common.loot.IGlobalLootModifier;

public class PetrolparkGlobalLootModifierSerializers {
    
    public static final RegistryEntry<MapCodec<? extends IGlobalLootModifier>, MapCodec<ContaminateGlobalLootModifier>> CONTAMINATE_GLOBAL_LOOT_MODIFIER_SERIALZIER = REGISTRATE.globalLootModifierSerializer("contaminate", ContaminateGlobalLootModifier.CODEC);

    public static final void register() {};
};
