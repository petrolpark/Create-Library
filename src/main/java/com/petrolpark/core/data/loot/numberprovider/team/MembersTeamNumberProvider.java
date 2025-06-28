package com.petrolpark.core.data.loot.numberprovider.team;

import com.petrolpark.PetrolparkNumberProviderTypes;
import com.petrolpark.core.data.loot.numberprovider.NumberEstimate;
import com.petrolpark.core.team.ITeam;

import net.minecraft.world.level.storage.loot.LootContext;

public class MembersTeamNumberProvider implements TeamNumberProvider {

    @Override
    public float getFloat(ITeam team, LootContext context) {
        return team.streamMemberUsernames().count();
    };

    @Override
    public NumberEstimate getEstimate() {
        return NumberEstimate.UNKNOWN;
    };

    @Override
    public LootTeamNumberProviderType getType() {
        return PetrolparkNumberProviderTypes.MEMBER_COUNT.get();
    };
    
};
