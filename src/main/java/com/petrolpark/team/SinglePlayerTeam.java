package com.petrolpark.team;

import java.util.UUID;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.petrolpark.team.data.ITeamDataType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.INBTSerializable;

@AutoRegisterCapability
public class SinglePlayerTeam extends AbstractTeam<SinglePlayerTeam> implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static final Capability<SinglePlayerTeam> CAPABILITY = CapabilityManager.get(new CapabilityToken<SinglePlayerTeam>() {});

    public final Player player;

    public SinglePlayerTeam(Player player) {
        this.player = player;
    };

    @Override
    public ITeamType<SinglePlayerTeam> getType() {
        return TeamTypes.SINGLE_PLAYER.get();
    };

    @Override
    public boolean isMember(Player player) {
        return player.equals(this.player);
    };

    @Override
    public int memberCount() {
        return 1;
    };

    @Override
    public Stream<String> streamMemberUsernames(Level level) {
        return Stream.of(player.getGameProfile().getName());
    };

    @Override
    public Stream<Player> streamMembers(Level level) {
        return Stream.of(player);
    };

    @Override
    public boolean isAdmin(Player player) {
        return true;
    };

    @Override
    public Component getName(Level level) {
        return player.getDisplayName();
    };

    @Override
    public void setChanged(Level level, ITeamDataType<?> dataType) {
        // Doesn't need to be changed, capabilities are always saved
    };

    @Override
    public void renderIcon(GuiGraphics graphics) {
        Minecraft mc = Minecraft.getInstance();
        if (player == null) return;
        PlayerFaceRenderer.draw(graphics, mc.getSkinManager().getInsecureSkinLocation(player.getGameProfile()), 0, 0, 16);
    };

    @Override
    public Component getRenderedMemberList(int maxTextWidth) {
        return player.getDisplayName();
    };

    // CAPABILITY

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CAPABILITY) return LazyOptional.of(() -> this).cast();
        return LazyOptional.empty();
    };

    @Override
    public CompoundTag serializeNBT() {
        return saveTeamData(player.level());
    };

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        loadTeamData(player.level(), nbt);
    };

    // TYPE

    public static class Type implements ITeamType<SinglePlayerTeam> {

        @Override
        public SinglePlayerTeam read(CompoundTag tag, Level level) {
            UUID uuid = tag.getUUID("Player");
            Player player = level.getPlayerByUUID(uuid);
            if (player != null) return player.getCapability(CAPABILITY).resolve().get();
            return null;
        };

        @Override
        public void write(SinglePlayerTeam team, CompoundTag tag) {
            tag.putUUID("Player", team.player.getUUID());
        };

    };
    
};
