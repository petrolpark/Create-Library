package petrolpark.mc.library.core.world.entity.player.team.everybody;

import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import petrolpark.mc.library.core.world.entity.player.team.AbstractTeam;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.registry.PetrolparkTeamProviderTypes;

@ParametersAreNonnullByDefault
public class EverybodyTeam extends AbstractTeam {

    public final Level level;

    protected EverybodyTeam(Level level, DataComponentPatch components) {
        super(components);
        this.level = level;
    };

    @Override
    public boolean isMember(Player player) {
        return true;
    };

    @Override
    public EverybodyTeam.Provider getProvider() {
        return EverybodyTeam.Provider.INSTANCE;
    };

    @Override
    public int memberCount() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'memberCount'");
    };

    @Override
    public Stream<String> streamMemberUsernames() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'streamMemberUsernames'");
    };

    @Override
    public Stream<ServerPlayer> streamOnlineMembers() {
        final MinecraftServer server = level.getServer();
        if (server != null) return server.getPlayerList().getPlayers().stream();
        return Stream.empty();
    };

    @Override
    public boolean isAdmin(Player player) {
        return player.hasPermissions(2);
    };

    @Override
    public Component getName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getName'");
    };

    @Override
    public void renderIcon(GuiGraphics graphics) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'renderIcon'");
    };

    @Override
    public void setChanged(@Nullable DataComponentPatch patch) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setChanged'");
    };

    public static class Provider implements ITeam.Provider {

        public static final EverybodyTeam.Provider INSTANCE = new EverybodyTeam.Provider();

        private Provider() {};

        @Override
        public EverybodyTeam provideTeam(Level level) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'provideTeam'");
        };

        @Override
        public ProviderType getProviderType() {
            return PetrolparkTeamProviderTypes.EVERYBODY.get();
        };
        
    };
    
};
