package com.petrolpark.core.data.loot.numberprovider.team;

import java.util.Collections;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkNumberProviderTypes;
import com.petrolpark.core.data.loot.numberprovider.FunctionNumberProvider;
import com.petrolpark.core.data.loot.numberprovider.NumberEstimate;
import com.petrolpark.core.data.loot.numberprovider.entity.EntityNumberProvider;
import com.petrolpark.core.team.ITeam;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;

/**
 * <p>{@code petrolpark:member_reduction}</p>
 * 
 * Get values from every {@link ITeam#streamMembers() Team member} and combine them.
 * 
 * Arguments:
 * <ul>
 * <li> {@code value} - {@link EntityNumberProvider} to get value from each member
 * <li> {@code function} - ID of {@link LootNumberProviderType} of {@link FunctionNumberProvider} to apply to all values
 * </ul>
 * 
 * @author petrolpark
 */
public record MemberReductionTeamNumberProvider(EntityNumberProvider value, LootNumberProviderType function) implements TeamNumberProvider {

    public static final MapCodec<MemberReductionTeamNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        EntityNumberProvider.CODEC.fieldOf("value").forGetter(MemberReductionTeamNumberProvider::value),
        BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE.byNameCodec().validate(FunctionNumberProvider::isFunction).fieldOf("function").forGetter(MemberReductionTeamNumberProvider::function)
    ).apply(instance, MemberReductionTeamNumberProvider::new));

    @Override
    public float getFloat(ITeam team, LootContext context) {
        return FunctionNumberProvider.get(function()).create(Collections.emptyList()).applyFloat(context, team.streamMembers().mapToDouble(player -> value.getFloat(player, context)));
    };

    @Override
    public NumberEstimate getEstimate() {
        return NumberEstimate.UNKNOWN;
    };

    @Override
    public LootTeamNumberProviderType getTeamNumberProviderType() {
        return PetrolparkNumberProviderTypes.MEMBER_REDUCTION.get();
    };
    
};
