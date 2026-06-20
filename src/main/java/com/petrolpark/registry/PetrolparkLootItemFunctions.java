package com.petrolpark.registry;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.mojang.serialization.MapCodec;
import com.petrolpark.core.badge.BadgeAwardLootItemFunction;
import com.petrolpark.core.flags.FlagLootItemFunction;
import com.petrolpark.core.world.entity.player.team.SetTeamLootItemFunction;
import com.petrolpark.core.world.item.decay.StartDecayLootItemFunction;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

public class PetrolparkLootItemFunctions {
    
    public static final RegistryEntry<LootItemFunctionType<?>, LootItemFunctionType<BadgeAwardLootItemFunction>> BADGE_AWARD = REGISTRATE.lootItemFunctionType("badge_award", MapCodec.unit(BadgeAwardLootItemFunction::new));
    public static final RegistryEntry<LootItemFunctionType<?>, LootItemFunctionType<FlagLootItemFunction>> FLAG = REGISTRATE.lootItemFunctionType("flag", FlagLootItemFunction.CODEC);
    public static final RegistryEntry<LootItemFunctionType<?>, LootItemFunctionType<SetTeamLootItemFunction>> SET_TEAM = REGISTRATE.lootItemFunctionType("set_team", MapCodec.unit(new SetTeamLootItemFunction()));
    public static final RegistryEntry<LootItemFunctionType<?>, LootItemFunctionType<StartDecayLootItemFunction>> START_DECAY = REGISTRATE.lootItemFunctionType("start_decay", MapCodec.unit(new StartDecayLootItemFunction()));

    public static final void register() {};
};
