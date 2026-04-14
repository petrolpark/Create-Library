package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.mojang.serialization.MapCodec;
import com.petrolpark.core.badge.BadgeAwardLootItemFunction;
import com.petrolpark.core.contamination.ContaminationLootItemFunction;
import com.petrolpark.core.item.decay.StartDecayLootItemFunction;
import com.petrolpark.core.team.SetTeamLootItemFunction;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

public class PetrolparkLootItemFunctions {
    
    public static final RegistryEntry<LootItemFunctionType<?>, LootItemFunctionType<BadgeAwardLootItemFunction>> BADGE_AWARD = REGISTRATE.lootItemFunctionType("badge_award", MapCodec.unit(BadgeAwardLootItemFunction::new));
    public static final RegistryEntry<LootItemFunctionType<?>, LootItemFunctionType<ContaminationLootItemFunction>> CONTAMINATION = REGISTRATE.lootItemFunctionType("contaminate", ContaminationLootItemFunction.CODEC);
    public static final RegistryEntry<LootItemFunctionType<?>, LootItemFunctionType<SetTeamLootItemFunction>> SET_TEAM = REGISTRATE.lootItemFunctionType("set_team", MapCodec.unit(new SetTeamLootItemFunction()));
    public static final RegistryEntry<LootItemFunctionType<?>, LootItemFunctionType<StartDecayLootItemFunction>> START_DECAY = REGISTRATE.lootItemFunctionType("start_decay", MapCodec.unit(new StartDecayLootItemFunction()));

    public static final void register() {};
};
