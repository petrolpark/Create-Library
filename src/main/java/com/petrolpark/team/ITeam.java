package com.petrolpark.team;

import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.team.data.ITeamDataType;
import com.petrolpark.util.Lang;
import com.petrolpark.util.NBTHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface ITeam<T extends ITeam<? super T>> {

    public static ITeam<?> read(CompoundTag tag, Level level) {
        ITeamType<?> type = NBTHelper.readRegistryObject(tag, "Type", PetrolparkRegistries.Keys.TEAM_TYPE);
        if (type == null) {
            Petrolpark.LOGGER.warn("Unknown Team Type: "+tag.getString("Type"));
            return NoTeam.INSTANCE;
        };
        return type.read(tag, level);
    };

    public static <T extends ITeam<? super T>> CompoundTag write(T team) {
        CompoundTag tag = new CompoundTag();
        NBTHelper.writeRegistryObject(tag, "Type", PetrolparkRegistries.Keys.TEAM_TYPE, team.getType());
        team.getType().write(team, tag);
        return tag;
    };

    public ITeamType<T> getType();

    public default boolean isNone() {
        return getType() == TeamTypes.NONE;
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

    /**
     * Returns the Team Data associated with the given {@link ITeamDataType}.
     * Implementations must not return {@code null} for missing Data, but a {@link ITeamDataType#getBlankInstance() blank instance}.
     * @param <DATA> Class of the Team Data
     * @param dataType
     * @return Non-{@code null} instance of the Team Data
     */
    @Nonnull
    public <DATA> DATA getTeamData(ITeamDataType<? super DATA> dataType);

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

    public static interface ITeamType<T extends ITeam<? super T>> {

        public T read(CompoundTag tag, Level level);

        public void write(T team, CompoundTag tag);
    };
};
