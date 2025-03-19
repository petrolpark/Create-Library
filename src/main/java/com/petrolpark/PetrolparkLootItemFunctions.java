package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.mojang.serialization.MapCodec;
import com.petrolpark.badge.BadgeAwardLootItemFunction;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

public class PetrolparkLootItemFunctions {
    
    public static final RegistryEntry<LootItemFunctionType<?>, LootItemFunctionType<BadgeAwardLootItemFunction>> BADGE_AWARD = REGISTRATE.lootItemFunctionType("badge_award", MapCodec.unit(BadgeAwardLootItemFunction::new));

    public static final void register() {};
};
