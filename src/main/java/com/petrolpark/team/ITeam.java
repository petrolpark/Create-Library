package com.petrolpark.team;

import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.team.data.ITeamDataType;
import com.petrolpark.util.Lang;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.MutableDataComponentHolder;

public interface ITeam extends MutableDataComponentHolder {

    public ITeam.Provider getProvider();

    public default boolean isNone() {
        return getProvider().getProviderType() == PetrolparkTeamProviderTypes.NONE.get();
    };

    public boolean isMember(Player player);

    public int memberCount();
    
    public Stream<String> streamMemberUsernames(Level level);

    /**
     * Use {@link ITeam#streamMemberUsernames(Level)} unless having the Player itself is vital.
     * @param level
     * @return Stream of Players in this Team.
     */
    public default Stream<Player> streamMembers(Level level) {
        MinecraftServer server = level.getServer();
        if (server == null) return Stream.empty();
        return streamMemberUsernames(level).map(server.getPlayerList()::getPlayerByName);
    };

    /**
     * If called, it is assumed that {@link ITeam#isMember(Player)} has already passed.
     * @param player
     * @return Whether this Player can manage this Team
     */
    public boolean isAdmin(Player player);

    public Component getName(Level level);

    public void setChanged(Level level, ITeamDataType<?> dataType);

    /**
     * Render an icon for this {@link ITeam}. The icon should occupy {@code (0, 0) -> (16, 16)} of the given PoseStack.
     * @param graphics
     */
    @OnlyIn(Dist.CLIENT)
    public void renderIcon(GuiGraphics graphics);

    @OnlyIn(Dist.CLIENT)
    public default Component getRenderedMemberList(int maxTextWidth) {
        Minecraft mc = Minecraft.getInstance();
        return Lang.shortList(streamMemberUsernames(mc.level).map(Component::literal).toList(), maxTextWidth, mc.font);
    };

    public static interface Provider {

        /**
         * Use {@link ITeam.Provider#CODEC} instead.
         */
        static Codec<Provider> TYPED_CODEC = PetrolparkRegistries.TEAM_PROVIDER_TYPES
            .byNameCodec()
            .dispatch(ITeam.Provider::getProviderType, ITeam.ProviderType::codec);

        public static final Codec<ITeam.Provider> CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, Codec.unit(NoTeam.INSTANCE)));

        public static final StreamCodec<RegistryFriendlyByteBuf, ITeam.Provider> STREAM_CODEC = ByteBufCodecs.registry(PetrolparkRegistries.Keys.TEAM_PROVIDER_TYPE)
            .dispatch(ITeam.Provider::getProviderType, ITeam.ProviderType::streamCodec);

        public ITeam provideTeam(Level level);

        public ITeam.ProviderType getProviderType();
    };

    public static record ProviderType(MapCodec<? extends ITeam.Provider> codec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends ITeam.Provider> streamCodec) {

    };
};
