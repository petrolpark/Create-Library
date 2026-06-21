package petrolpark.mc.library.core.data.loot.modifier;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ICondition;

public record LootTableModification(List<ICondition> conditions, ResourceLocation target, int priority, List<ILootTableModifier> modifiers) implements Comparable<LootTableModification> {

    public static final Codec<LootTableModification> DIRECT_CODEC = RecordCodecBuilder.<LootTableModification>create(instance -> instance.group(
        ICondition.LIST_CODEC.optionalFieldOf("conditions", Collections.emptyList()).forGetter(LootTableModification::conditions),
        ResourceLocation.CODEC.fieldOf("loot_table").forGetter(LootTableModification::target),
        Codec.INT.optionalFieldOf("priority", 0).forGetter(LootTableModification::priority),
        Codec.list(ILootTableModifier.CODEC).fieldOf("modifiers").forGetter(LootTableModification::modifiers)
    ).apply(instance, LootTableModification::new)).xmap(LootTableModificationManager::register, Function.identity());

    @Override
    public int compareTo(LootTableModification o) {
        return priority() - o.priority();
    };

};
