package com.petrolpark.core.data.loot.numberprovider.team;

import com.petrolpark.PetrolparkNumberProviderTypes;
import com.petrolpark.core.data.loot.numberprovider.NumberEstimate;
import com.petrolpark.core.team.ITeam;

import net.minecraft.world.level.storage.loot.LootContext;

/**
 * <p>{@code petrolpark:member_count}</p>
 * 
 * {@link ITeam#memberCount() Get the number of members} of the {@link ITeam}. No arguments.
 * 
 * @author petrolpark
 */
public class MemberCountTeamNumberProvider implements TeamNumberProvider {

    @Override
    public float getFloat(ITeam team, LootContext context) {
        return team.memberCount();
    };

    @Override
    public NumberEstimate getEstimate() {
        return NumberEstimate.UNKNOWN;
    };

    @Override
    public LootTeamNumberProviderType getTeamNumberProviderType() {
        return PetrolparkNumberProviderTypes.MEMBER_COUNT.get();
    };
    
};
