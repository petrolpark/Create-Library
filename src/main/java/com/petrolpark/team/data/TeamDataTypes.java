package com.petrolpark.team.data;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.shop.ShopsData;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class TeamDataTypes {

    public static final RegistryEntry<ITeamDataType<?>, ITeamDataType<ShopsData>>

    SHOPS = REGISTRATE.teamDataType("shops", TeamShops.ShopsData::new);
    
    public static final void register() {};
};
