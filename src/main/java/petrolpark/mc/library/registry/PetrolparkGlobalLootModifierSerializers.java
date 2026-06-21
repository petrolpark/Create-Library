package petrolpark.mc.library.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.core.flags.FlagGlobalLootModifier;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.neoforged.neoforge.common.loot.IGlobalLootModifier;

public class PetrolparkGlobalLootModifierSerializers {
    
    public static final RegistryEntry<MapCodec<? extends IGlobalLootModifier>, MapCodec<FlagGlobalLootModifier>> FLAG_GLOBAL_LOOT_MODIFIER_SERIALZIER = REGISTRATE.globalLootModifierSerializer("flag", FlagGlobalLootModifier.CODEC);

    public static final void register() {};
};
