package com.petrolpark.team.data;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.shop.TeamShopsData;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class TeamDataTypes {

    public static final RegistryEntry<ITeamDataType<TeamShopsData>>

    SHOPS = REGISTRATE.get().teamDataType("shops", TeamShopsData.Type::new);
    
    public static final void register() {};
};
