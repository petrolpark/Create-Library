package petrolpark.mc.library.core.registrate.dataGen;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.function.TriConsumer;

import com.tterrag.registrate.providers.loot.RegistrateLootTables;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.neoforged.neoforge.common.conditions.ICondition;
import petrolpark.mc.library.core.data.loot.provider.ConditionalLootTableSubProvider;

public interface RegistrateConditionalLootTableSubProvider extends RegistrateLootTables, ConditionalLootTableSubProvider {
    
    @Override
    public default void generate(TriConsumer<ResourceLocation, Builder, List<ICondition>> output) {
        generate((key, lootTableBuilder) -> output.accept(key.location(), lootTableBuilder, Collections.emptyList()));
    };

    @Override
    @Deprecated
    void generate(@Nonnull BiConsumer<ResourceKey<LootTable>, Builder> output);
        
};
