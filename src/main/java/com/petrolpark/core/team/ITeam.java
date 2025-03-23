package com.petrolpark.core.team;

import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.util.Lang;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.MutableDataComponentHolder;

/**
 * A collection of Players acting as one Entity.
 * Teams are {@link MutableDataComponentHolder}s, but only {@link ITeam#isMember(Player) members} of a Team are guaranteed to have access to those Components on the client side.
 */
public interface ITeam extends MutableDataComponentHolder {

    public ITeam.Provider getProvider();

    public default boolean isNone() {
        return getProvider().getProviderType() == PetrolparkTeamProviderTypes.NONE.get();
    };

    public boolean isMember(Player player);

    public int memberCount();
    
    public Stream<String> streamMemberUsernames();

    /**
     * Use {@link ITeam#streamMemberUsernames()} unless having the Player itself is vital.
     * @return Stream of Players in this Team.
     */
    @OnlyIn(Dist.DEDICATED_SERVER)
    public Stream<Player> streamMembers();

    /**
     * If called, it is assumed that {@link ITeam#isMember(Player)} has already passed.
     * @param player
     * @return Whether this Player can manage this Team
     */
    public boolean isAdmin(Player player);

    public Component getName();

    /**
     * Render an icon for this {@link ITeam}. The icon should occupy {@code (0, 0) -> (16, 16)} of the given PoseStack.
     * @param graphics
     */
    @OnlyIn(Dist.CLIENT)
    public void renderIcon(GuiGraphics graphics);

    @OnlyIn(Dist.CLIENT)
    public default Component getRenderedMemberList(int maxTextWidth) {
        Minecraft mc = Minecraft.getInstance();
        return Lang.shortList(streamMemberUsernames().map(Component::literal).toList(), maxTextWidth, mc.font);
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
