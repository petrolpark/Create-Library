package com.petrolpark.team;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.team.scoreboard.ScoreboardTeam;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class TeamTypes {
  
    public static final RegistryEntry<NoTeam.Type> NONE = REGISTRATE.get().generic("none", PetrolparkRegistries.Keys.TEAM_TYPE, NoTeam.Type::new).register();
    public static final RegistryEntry<SinglePlayerTeam.Type> SINGLE_PLAYER = REGISTRATE.get().generic("single_player", PetrolparkRegistries.Keys.TEAM_TYPE, SinglePlayerTeam.Type::new).register();
    public static final RegistryEntry<ScoreboardTeam.Type> SCOREBOARD = REGISTRATE.get().generic("scoreboard", PetrolparkRegistries.Keys.TEAM_TYPE, ScoreboardTeam.Type::new).register();

    public static final void register() {};
};
