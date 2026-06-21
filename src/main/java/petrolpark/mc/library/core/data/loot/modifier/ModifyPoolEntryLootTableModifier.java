package petrolpark.mc.library.core.data.loot.modifier;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import petrolpark.mc.library.registry.PetrolparkLootModifierTypes;
import petrolpark.mc.library.util.JsonHelper;
import petrolpark.mc.library.util.codec.CodecHelper;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public record ModifyPoolEntryLootTableModifier(Either<String, Integer> poolIdentifier, JsonElement entryIdentifier, ILootPoolEntryModifier modifier) implements ILootPoolModifier {

    public static final MapCodec<ModifyPoolEntryLootTableModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ILootPoolModifier.poolField(instance).t1(),
        CodecHelper.JSON_ELEMENT_CODEC.fieldOf("entry").forGetter(ModifyPoolEntryLootTableModifier::entryIdentifier),
        ILootPoolEntryModifier.CODEC.fieldOf("modifier").forGetter(ModifyPoolEntryLootTableModifier::modifier)
    ).apply(instance, ModifyPoolEntryLootTableModifier::new));

    @Override
    public LootPool.Builder modifyPool(HolderLookup.Provider registries, LootTable lootTable, LootPool pool) {
        final RegistryOps<JsonElement> registryOps = RegistryOps.create(JsonOps.INSTANCE, registries);

        final LootPool.Builder builder = LootPool.lootPool()
            .setRolls(pool.getRolls())
            .setBonusRolls(pool.getBonusRolls());
        if (pool.getName() != null) builder.name(pool.getName());
        for (LootItemCondition condition : getConditions(pool)) when(builder, condition);
        for (LootItemFunction function : getFunctions(pool)) apply(builder, function);

        for (LootPoolEntryContainer entry : getEntries(pool)) {
            if (JsonHelper.fuzzyMatch(entryIdentifier(), LootPoolEntries.CODEC.encodeStart(registryOps, entry).getOrThrow())) {
                add(builder, modifier().modify(registries, lootTable, pool, entry));
            } else {
                add(builder, entry);
            };
        };

        return builder;
    };

    @Override
    public LootTableModifierType getType() {
        return PetrolparkLootModifierTypes.MODIFY_ENTRY.get();
    };
};
