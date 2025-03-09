package com.petrolpark.team;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.team.ITeam.ITeamType;
import com.petrolpark.team.scoreboard.ScoreboardTeam;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class TeamTypes {
  
    public static final RegistryEntry<ITeamType<?>, NoTeam.Type> NONE = REGISTRATE.generic("none", PetrolparkRegistries.Keys.TEAM_TYPE, NoTeam.Type::new).register();
    public static final RegistryEntry<ITeamType<?>, SinglePlayerTeam.Type> SINGLE_PLAYER = REGISTRATE.generic("single_player", PetrolparkRegistries.Keys.TEAM_TYPE, SinglePlayerTeam.Type::new).register();
    public static final RegistryEntry<ITeamType<?>, ScoreboardTeam.Type> SCOREBOARD = REGISTRATE.generic("scoreboard", PetrolparkRegistries.Keys.TEAM_TYPE, ScoreboardTeam.Type::new).register();

    public static final void register() {};
};
