package petrolpark.mc.library.core.world.entity.player.team.scoreboard;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.world.entity.player.team.AbstractTeam;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.core.world.entity.player.team.NoTeam;
import petrolpark.mc.library.registry.PetrolparkTeamProviderTypes;
import petrolpark.mc.library.util.codec.CodecHelper;

/**
 * {@link ITeam} wrapping vanilla's {@link PlayerTeam Scoreboard Teams}.
 */
public class ScoreboardTeam extends AbstractTeam {

    public final Level level;
    public final PlayerTeam team;

    public static final ITeam.Provider provider(PlayerTeam team) {
        return new ScoreboardTeam.Provider(team.getName());
    };

    public ScoreboardTeam(Level level, PlayerTeam team) {
        this(level, team, DataComponentPatch.EMPTY);
    };

    protected ScoreboardTeam(Level level, PlayerTeam team, DataComponentPatch components) {
        super(components);
        this.level = level;
        this.team = team;
    };

    @Override
    public ITeam.Provider getProvider() {
        return provider(team);
    };

    @Override
    public boolean isMember(@Nonnull Player player) {
        return player.getTeam() == team;
    };

    @Override
    public int memberCount() {
        return team.getPlayers().size();
    };

    @Override
    public Stream<String> streamMemberUsernames() {
        return team.getPlayers().stream();
    };

    @Override
    public Stream<ServerPlayer> streamOnlineMembers() {
        final MinecraftServer server = level.getServer();
        if (server != null) return streamMemberUsernames().map(server.getPlayerList()::getPlayerByName)
            .filter(Predicate.not(Objects::isNull));
        return Stream.empty();
    };

    @Override
    public boolean isAdmin(@Nonnull Player player) {
        return player.hasPermissions(2);
    };

    @Override
    public Component getName() {
        return team.getDisplayName();
    };

    @Override
    public void setChanged(DataComponentPatch patch) {
        Petrolpark.SCOREBOARD_TEAMS.dataComponentChanged(level, this, patch);
    };

    @Override
    public void renderIcon(@Nonnull GuiGraphics graphics) {
        Integer color = team.getColor().getColor();
        RenderSystem.disableBlend();
        PoseStack ms = graphics.pose();

        // Colored background
        graphics.fill(RenderType.guiOverlay(), 0, 0, 16, 16, color == null ? 0xFFFFFFFF : color | 0xFF000000);

        Minecraft mc = Minecraft.getInstance();
        ClientPacketListener connection = mc.getConnection();

        // Player faces
        if (connection != null) {
            List<String> playerNames = streamMemberUsernames().toList();
            ms.pushPose();
            ms.translate(1f, 1f, 0f);
            ms.scale(7 / 16f, 7 / 16f, 1f);
            int i = 0;
            for (String name : playerNames) {
                if (i >= 4) break;
                PlayerInfo playerInfo = connection.getPlayerInfo(name);
                if (playerInfo == null) continue;
                PlayerFaceRenderer.draw(graphics, playerInfo.getSkin(), 16 * (i % 2), 16 * (i / 2), 16);
                i++;
            };
            ms.popPose();
        };

    };

    public static record Provider(String teamName) implements ITeam.Provider {

        public static final MapCodec<Provider> CODEC = CodecHelper.singleFieldMap(Codec.STRING, "team", Provider::teamName, Provider::new);
        public static final StreamCodec<FriendlyByteBuf, Provider> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, Provider::teamName, Provider::new);

        @Override
        public ITeam provideTeam(@Nonnull Level level) {
            return Petrolpark.SCOREBOARD_TEAMS.get(level, teamName).orElse(NoTeam.INSTANCE);
        };

        @Override
        public ProviderType getProviderType() {
            return PetrolparkTeamProviderTypes.SCOREBOARD.get();
        };

    };
    
};
