package com.petrolpark.core.data.loot.numberprovider.team;

import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkNumberProviderTypes;
import com.petrolpark.core.data.loot.numberprovider.NumberEstimate;
import com.petrolpark.core.team.ITeam;
import com.petrolpark.util.CodecHelper;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

/**
 * <p>{@code petrolpark:flat}</p>
 * 
 * Get a simple {@link NumberProvider} value, without reference to the Team.
 * 
 * Arguments:
 * <ul>
 * <li> {@code value} - A {@link NumberProvider}
 * </ul>
 * 
 * Note that this has inline serialization. You can just refer to a {@link NumberProvider} directly when an {@link TeamNumberProvider} is expected and it will be converted.
 * 
 * @author petrolpark
 */
public record FlatTeamNumberProvider(NumberProvider numberProvider) implements TeamNumberProvider {

    public static final MapCodec<FlatTeamNumberProvider> CODEC = CodecHelper.singleFieldMap(NumberProviders.CODEC, "provider", FlatTeamNumberProvider::numberProvider, FlatTeamNumberProvider::new);

    @Override
    public float getFloat(ITeam Team, LootContext lootContext) {
        return numberProvider().getFloat(lootContext);
    };

    @Override
    public NumberEstimate getEstimate() {
        return NumberEstimate.get(numberProvider());
    };

    @Override
    public LootTeamNumberProviderType getTeamNumberProviderType() {
        return PetrolparkNumberProviderTypes.FLAT_TEAM.get();
    };
    
};
