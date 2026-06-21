package petrolpark.mc.library.core.data.loot.modifier;

import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.registry.PetrolparkLootModifierTypes;
import petrolpark.mc.library.util.codec.CodecHelper;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;

public record SetQualityLootPoolEntryModifier(int quality) implements ILootPoolSingletonEntryModifier {

    public static final MapCodec<SetQualityLootPoolEntryModifier> CODEC = CodecHelper.singleFieldMap(CodecHelper.POS_INT, "quality", SetQualityLootPoolEntryModifier::quality, SetQualityLootPoolEntryModifier::new);

    @Override
    public LootPoolSingletonContainer modifySingleton(HolderLookup.Provider registries, LootTable lootTable, LootPool pool, LootPoolSingletonContainer entry) {
        entry.quality = quality();
        return entry;
    };

    @Override
    public LootPoolEntryModifierType getType() {
        return PetrolparkLootModifierTypes.SET_QUALITY.get();
    };
    
};
