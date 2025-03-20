package com.petrolpark.core.team;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.mojang.serialization.MapCodec;
import com.petrolpark.core.team.scoreboard.ScoreboardTeam;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.network.codec.StreamCodec;

public class PetrolparkTeamProviderTypes {
  
    public static final RegistryEntry<ITeam.ProviderType, ITeam.ProviderType>
    
    NONE = REGISTRATE.teamProviderType("none", MapCodec.unit(NoTeam.INSTANCE), StreamCodec.unit(NoTeam.INSTANCE)),
    SINGLE_PLAYER = REGISTRATE.teamProviderType("single_player", SinglePlayerTeam.Provider.CODEC, SinglePlayerTeam.Provider.STREAM_CODEC),
    SCOREBOARD = REGISTRATE.teamProviderType("scoreboard", ScoreboardTeam.Provider.CODEC, ScoreboardTeam.Provider.STREAM_CODEC);

    public static final void register() {};
};
