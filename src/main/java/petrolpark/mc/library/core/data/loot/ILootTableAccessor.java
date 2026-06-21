package petrolpark.mc.library.core.data.loot;

import java.util.function.Function;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;

public interface ILootTableAccessor {

    static <FINDER extends ILootTableAccessor> Products.P1<RecordCodecBuilder.Mu<FINDER>, Either<ResourceKey<LootTable>, LootTable>> lootTableField(RecordCodecBuilder.Instance<FINDER> instance) {
        return instance.group(Codec.either(ResourceKey.codec(Registries.LOOT_TABLE), LootTable.DIRECT_CODEC).fieldOf("loot_table").forGetter(ILootTableAccessor::lootTable));
    };
    
    Either<ResourceKey<LootTable>, LootTable> lootTable();

    default LootTable getLootTable(LootContext context) {
        return lootTable().map(
            key -> context.getResolver().get(Registries.LOOT_TABLE, key).map(Holder::value).orElse(LootTable.EMPTY),
            Function.identity()
        );
    };
};
