package com.petrolpark.data.loot;

import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.ForgeRegistries;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.mojang.serialization.Codec;
import com.petrolpark.contamination.ContaminateGlobalLootModifier;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class PetrolparkGlobalLootModifierSerializers {
    
    public static final RegistryEntry<Codec<? extends IGlobalLootModifier>> CONTAMINATE_GLOBAL_LOOT_MODIFIER_SERIALZIER = REGISTRATE.get().simple("contaminate", ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, () -> ContaminateGlobalLootModifier.CODEC);

    public static final void register() {};
};
