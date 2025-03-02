package com.petrolpark.data.loot;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.data.loot.numberprovider.team.LootTeamNumberProviderType;
import com.petrolpark.data.loot.numberprovider.team.MembersTeamNumberProvider;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class PetrolparkLootTeamNumberProviders {

    public static final RegistryEntry<LootTeamNumberProviderType>

    MEMBERS = REGISTRATE.get().lootTeamNumberProviderType("members", MembersTeamNumberProvider::new);
  
    public static final void register() {};
};
