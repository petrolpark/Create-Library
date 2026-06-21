package petrolpark.mc.library.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.core.world.entity.player.team.NoTeam;
import petrolpark.mc.library.core.world.entity.player.team.scoreboard.ScoreboardTeam;
import petrolpark.mc.library.core.world.entity.player.team.singleplayer.SinglePlayerTeam;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.network.codec.StreamCodec;

public class PetrolparkTeamProviderTypes {
  
    public static final RegistryEntry<ITeam.ProviderType, ITeam.ProviderType>
    
    NONE = REGISTRATE.teamProviderType("none", MapCodec.unit(NoTeam.INSTANCE), StreamCodec.unit(NoTeam.INSTANCE)),
    SINGLE_PLAYER = REGISTRATE.teamProviderType("single_player", SinglePlayerTeam.Provider.CODEC, SinglePlayerTeam.Provider.STREAM_CODEC),
    SCOREBOARD = REGISTRATE.teamProviderType("scoreboard", ScoreboardTeam.Provider.CODEC, ScoreboardTeam.Provider.STREAM_CODEC);

    public static final void register() {};
};
