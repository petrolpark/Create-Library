package petrolpark.mc.library.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.core.badge.BadgeAwardLootItemFunction;
import petrolpark.mc.library.core.flags.FlagLootItemFunction;
import petrolpark.mc.library.core.world.entity.player.team.SetTeamLootItemFunction;
import petrolpark.mc.library.core.world.item.decay.StartDecayLootItemFunction;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

public class PetrolparkLootItemFunctions {
    
    public static final RegistryEntry<LootItemFunctionType<?>, LootItemFunctionType<BadgeAwardLootItemFunction>> BADGE_AWARD = REGISTRATE.lootItemFunctionType("badge_award", MapCodec.unit(BadgeAwardLootItemFunction::new));
    public static final RegistryEntry<LootItemFunctionType<?>, LootItemFunctionType<FlagLootItemFunction>> FLAG = REGISTRATE.lootItemFunctionType("flag", FlagLootItemFunction.CODEC);
    public static final RegistryEntry<LootItemFunctionType<?>, LootItemFunctionType<SetTeamLootItemFunction>> SET_TEAM = REGISTRATE.lootItemFunctionType("set_team", MapCodec.unit(new SetTeamLootItemFunction()));
    public static final RegistryEntry<LootItemFunctionType<?>, LootItemFunctionType<StartDecayLootItemFunction>> START_DECAY = REGISTRATE.lootItemFunctionType("start_decay", MapCodec.unit(new StartDecayLootItemFunction()));

    public static final void register() {};
};
