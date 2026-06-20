package com.petrolpark.core.world.entity.player.team;

import java.util.stream.Stream;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.petrolpark.core.world.entity.player.team.scoreboard.ScoreboardTeam;
import com.petrolpark.core.world.item.restaurant.Restaurant;
import com.petrolpark.registry.PetrolparkRegistries;
import com.petrolpark.registry.PetrolparkTeamProviderTypes;
import com.petrolpark.util.Lang;

import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.common.NeoForge;

/**
 * A collection of Players acting as one entity.
 * Teams are {@link MutableDataComponentHolder}s, but only {@link ITeam#isMember(Player) members} of a Team are guaranteed to have access to those Components on the client side.
 * @see <a href="https://github.com/petrolpark/Create-Library/wiki/Teams#accessing-teams">Wiki Article</a>
 */
@ParametersAreNonnullByDefault
public interface ITeam extends MutableDataComponentHolder {

    /**
     * Get every Team of which the given Player is a member.
     * Add custom {@link ITeam} implementations with {@link GatherTeamProvidersEvent}.
     * @param player
     */
    public static Stream<ITeam> streamAll(Player player) {
        final GatherTeamProvidersEvent event = new GatherTeamProvidersEvent(player);
        NeoForge.EVENT_BUS.post(event);
        return event.getTeamProvidersUnmodifiable().stream().map(provider -> provider.provideTeam(player.level()));
    };

    /**
     * The {@link ITeam.Provider} that provides <b>this exact</b> Team instance.
     */
    public ITeam.Provider getProvider();

    /**
     * Whether this Team can <b>never</b> have {@link ITeam#streamMembers any members} or {@link MutableDataComponentHolder#get(java.util.function.Supplier) Data Components}.
     * @see NoTeam#INSTANCE The Instance that should be used for this purpose
     */
    public default boolean isNone() {
        return getProvider().getProviderType() == PetrolparkTeamProviderTypes.NONE.get();
    };

    /**
     * Whether the given Player is in this Team, meaning they have access client-side to the Team's {@link MutableDataComponentHolder#get(java.util.function.Supplier) Data Components},
     * and have any non-{@link ITeam#isAdmin(Player) admin} abilities.
     * @param player
     */
    public boolean isMember(Player player);

    /**
     * The number of {@link ITeam#isMember(Player) members} of this Team.
     * @return Positive integer {@link NoTeam#INSTANCE almost always} greater than {@code 0}.
     */
    public int memberCount();
    
    /**
     * The unique {@link GameProfile#getName() username} of every {@link ITeam#isMember(Player) member} of this Team, in no particular order.
     * For {@link ScoreboardTeam}s in particular, this method is faster than {@link ITeam#streamMembers()} and should be used in preference to calling that method and <em>then</em> getting the usernames.
     */
    public Stream<String> streamMemberUsernames();

    /**
     * Every {@link ITeam#isMember(Player) member} of this Team, with no guarantee of order.
     * It is faster to use {@link ITeam#streamMemberUsernames()} unless having the Player itself is vital. On the client side, that is the only way to know the members of the Team.
     * @see ITeam#streamServerMembers() ServerPlayer implementation
     */
    @OnlyIn(Dist.DEDICATED_SERVER)
    public Stream<Player> streamMembers();

    /**
     * Every {@link ITeam#isMember(Player) member} of this Team as a {@link ServerPlayer} object, with no guarantee of order.
     * It is faster to use {@link ITeam#streamMemberUsernames()} unless having the Player itself is vital.
     * @see ITeam#streamMembers() Player (not necessarily ServerPlayer) but still server-side-only implementation
     * @see ITeam#sendToAllMembers(ClientboundPacketPayload) Shortcut if you would be using this list to send packets
     */
    @OnlyIn(Dist.DEDICATED_SERVER)
    public default Stream<ServerPlayer> streamServerMembers() {
        return streamMembers().map(p -> p instanceof ServerPlayer sp ? sp : null);
    };

    /**
     * What this means will be different for every system that uses Teams. It could mean the ability to change the name of a {@link Restaurant}, for example.
     * It has nothing to do with operator permissions.
     * If called, it is assumed that {@link ITeam#isMember(Player)} has already passed.
     * @param player
     */
    public boolean isAdmin(Player player);

    /**
     * The name of this Team, without necessarily referring to any {@link ITeam#isMember(Player) members}.
     * @see ITeam#getRenderedMemberList(int) Getting a formatted list of the names of some members
     */
    @OnlyIn(Dist.CLIENT)
    public Component getName();

    /**
     * Render an icon for this {@link ITeam}. The icon should occupy {@code (0, 0) -> (16, 16)} of the given PoseStack.
     * @param graphics
     */
    @OnlyIn(Dist.CLIENT)
    public void renderIcon(GuiGraphics graphics);

    /**
     * Get the {@link LivingEntity#getName() names} of some {@link ITeam#isMember(Player) members} of this Team, formatted nicely.
     * This does not need to list every member exhaustively.
     * This list in no particular order, but should be the same every time to prevent weird rendering.
     * @param maxTextWidth
     * @see ITeam#getName Getting the name of the Team
     */
    @OnlyIn(Dist.CLIENT)
    public default Component getRenderedMemberList(int maxTextWidth) {
        Minecraft mc = Minecraft.getInstance();
        return Lang.shortList(streamMemberUsernames().map(Component::literal).toList(), maxTextWidth, mc.font);
    };

    /**
     * Broadcast a packet to every {@link ITeam#isMember(Player) member} of this Team.
     * @param packet
     */
    public default void sendToAllMembers(ClientboundPacketPayload packet) {
        streamServerMembers().forEach(player -> CatnipServices.NETWORK.sendToClient(player, packet));
    };

    /**
     * {@link ITeam} objects have one instance for each team, and cannot be serialized. {@link ITeam.Provider} are references to Teams, not the Teams themselves,
     * and so can be {@link ITeam.ProviderType#codec() serialized}, and have multiple instances per Team. A {@link ITeam.Provider} should uniquely {@link ITeam.Provider#provideTeam(Level) identify} the same Team every single time.
     */
    public static interface Provider {

        /**
         * Use {@link ITeam.Provider#CODEC} instead.
         */
        @ApiStatus.Internal
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
