package com.petrolpark.core.data.loot.modifier;

import java.util.Objects;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.datafixers.Products.P1;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.util.codec.CodecHelper;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;

public interface ILootPoolModifier extends ILootTableModifier {

    static <T extends ILootPoolModifier> P1<RecordCodecBuilder.Mu<T>, Either<String, Integer>> poolField(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(Codec.either(Codec.STRING, CodecHelper.POS_INT).fieldOf("pool").forGetter(ILootPoolModifier::poolIdentifier));
    };

    public Either<String, Integer> poolIdentifier();

    @Override
    @ApiStatus.NonExtendable
    public default LootTable modify(HolderLookup.Provider registries, LootTable originalTable) {
        final LootTable.Builder builder = LootTable.lootTable()
            .setParamSet(originalTable.getParamSet());
        for (int i = 0; i < getPools(originalTable).size(); i++) {
            final LootPool pool = getPools(originalTable).get(i);
            final int j = i;
            if (poolIdentifier().map(name -> Objects.equals(name, pool.getName()), index -> index == j)) builder.withPool(modifyPool(registries, originalTable, pool));
            else withPool(builder, pool);
        };
        return builder.build();
    };

    public LootPool.Builder modifyPool(HolderLookup.Provider registries, LootTable lootTable, LootPool pool);

};
