package com.petrolpark.core.data.loot.numberprovider;

import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkLootContextParams;
import com.petrolpark.PetrolparkNumberProviderTypes;
import com.petrolpark.core.data.loot.numberprovider.team.TeamNumberProvider;
import com.petrolpark.core.team.ITeam;
import com.petrolpark.util.CodecHelper;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;

/**
 * <p>{@code petrolpark:team_property}</p>
 * 
 * Get a {@link TeamNumberProvider} value of the {@link PetrolparkLootContextParams#TEAM Team provided} in the {@link LootContext}.
 * 
 * Arguments:
 * <ul>
 * <li> {@code value} - A {@link TeamNumberProvider} to call on the {@link ITeam} 
 * </ul>
 * 
 * @author petrolpark
 */
public record ContextTeamNumberProvider(TeamNumberProvider value) implements IEstimableNumberProvider {

    public static final MapCodec<ContextTeamNumberProvider> CODEC = CodecHelper.singleFieldMap(TeamNumberProvider.CODEC, "value", ContextTeamNumberProvider::value, ContextTeamNumberProvider::new);
    
    @Override
    public float getFloat(@Nonnull LootContext context) {
        ITeam team = context.getParam(PetrolparkLootContextParams.TEAM);
        if (team != null) return value.getFloat(team, context);
        return 0f;
    };

    @Override
    public NumberEstimate getEstimate() {
        return value().getEstimate();
    };

    @Override
    public float getMaxFloat(LootContext context) {
        ITeam team = context.getParam(PetrolparkLootContextParams.TEAM);
        if (team != null) return value.getMaxFloat(team, context);
        return 0f;
    };

    @Override
    public LootNumberProviderType getType() {
        return PetrolparkNumberProviderTypes.CONTEXT_TEAM.get();
    };

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Sets.union(Set.of(PetrolparkLootContextParams.TEAM), value.getReferencedContextParams());
    };
    
};
