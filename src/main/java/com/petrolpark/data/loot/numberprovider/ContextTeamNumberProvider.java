package com.petrolpark.data.loot.numberprovider;

import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.data.loot.PetrolparkLootContextParams;
import com.petrolpark.data.loot.PetrolparkLootNumberProviderTypes;
import com.petrolpark.data.loot.numberprovider.team.TeamNumberProvider;
import com.petrolpark.team.ITeam;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public record ContextTeamNumberProvider(TeamNumberProvider value) implements NumberProvider {

    public static final MapCodec<ContextTeamNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        TeamNumberProvider.CODEC.fieldOf("value").forGetter(ContextTeamNumberProvider::value)
    ).apply(instance, ContextTeamNumberProvider::new));

    @Override
    public float getFloat(@Nonnull LootContext context) {
        ITeam<?> team = context.getParam(PetrolparkLootContextParams.TEAM);
        if (team != null) return value.getFloat(team, context);
        return 0f;
    };

    @Override
    public LootNumberProviderType getType() {
        return PetrolparkLootNumberProviderTypes.CONTEXT_TEAM.get();
    };

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Sets.union(Set.of(PetrolparkLootContextParams.TEAM), value.getReferencedContextParams());
    };
    
};
