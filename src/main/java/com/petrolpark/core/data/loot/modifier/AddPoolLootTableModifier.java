package com.petrolpark.core.data.loot.modifier;

import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkLootModifierTypes;
import com.petrolpark.util.CodecHelper;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;

public record AddPoolLootTableModifier(LootPool pool) implements ILootTableModifier {

    public static final MapCodec<AddPoolLootTableModifier> CODEC = CodecHelper.singleFieldMap(LootPool.CODEC, "pool", AddPoolLootTableModifier::pool, AddPoolLootTableModifier::new);

    @Override
    public LootTable modify(HolderLookup.Provider registries, LootTable originalTable) {
        originalTable.addPool(pool());
        return originalTable;
    };

    @Override
    public LootTableModifierType getType() {
        return PetrolparkLootModifierTypes.ADD_POOL.get();
    };
    
};
