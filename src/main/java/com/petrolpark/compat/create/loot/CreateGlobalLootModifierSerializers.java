package com.petrolpark.compat.create.loot;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.mojang.serialization.Codec;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.ForgeRegistries;

public class CreateGlobalLootModifierSerializers {
    
    public static final RegistryEntry<Codec<? extends IGlobalLootModifier>> CONTAMINATED_KINETIC_BLOCK_LOOT_MODIFIER_SERIALIZER = REGISTRATE.get().simple("contaminated_kinetic_blocks", ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, () -> ContaminatedKineticBlockLootModifier.CODEC);

    public static final void register() {}; 
};
