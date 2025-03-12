package com.petrolpark.data.loot.numberprovider;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.data.loot.PetrolparkLootNumberProviderTypes;
import com.petrolpark.data.loot.numberprovider.itemstack.ItemStackNumberProvider;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public record ToolNumberProvider(ItemStackNumberProvider value) implements NumberProvider {

    public static final MapCodec<ToolNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ItemStackNumberProvider.CODEC.fieldOf("value").forGetter(ToolNumberProvider::value)
    ).apply(instance, ToolNumberProvider::new));

    @Override
    public float getFloat(@Nonnull LootContext lootContext) {
        ItemStack tool = lootContext.getParamOrNull(LootContextParams.TOOL);
        if (tool != null) return value.getFloat(tool, lootContext);
        return 0f;
    };

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Collections.singleton(LootContextParams.TOOL);
    };

    @Override
    public LootNumberProviderType getType() {
        return PetrolparkLootNumberProviderTypes.TOOL.get();
    };
    
};
