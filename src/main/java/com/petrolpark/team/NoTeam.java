package com.petrolpark.team;

import java.util.stream.Stream;

import com.petrolpark.team.data.ITeamDataType;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class NoTeam implements ITeam<NoTeam> {

    public static final NoTeam INSTANCE = new NoTeam();

    @Override
    public ITeamType<NoTeam> getType() {
        return TeamTypes.NONE.get();
    };

    @Override
    public boolean isNone() {
        return true;
    };

    @Override
    public boolean isMember(Player player) {
        return false;
    };

    @Override
    public int memberCount() {
        return 0;
    };

    @Override
    public Stream<String> streamMemberUsernames(Level level) {
        return Stream.empty();
    };

    @Override
    public Stream<Player> streamMembers(Level level) {
        return Stream.empty();
    };

    @Override
    public boolean isAdmin(Player player) {
        return false;
    };

    @Override
    public Component getName(Level level) {
        return Component.translatable("team.petrolpark.team.none");
    };

    @Override
    @SuppressWarnings("unchecked")
    public <DATA> DATA getTeamData(ITeamDataType<? super DATA> dataType) {
        return (DATA)dataType.getBlankInstance();
    };

    @Override
    public void setChanged(Level level, ITeamDataType<?> dataType) {};

    @Override
    public void renderIcon(GuiGraphics graphics) {
        //TODO
    };

    @Override
    public Component getRenderedMemberList(int maxTextWidth) {
        return Component.translatable("petrolpark.generic.list.none");
    };

    public static class Type implements ITeamType<NoTeam> {

        @Override
        public NoTeam read(CompoundTag tag, Level level) {
            return INSTANCE;
        };

        @Override
        public void write(NoTeam team, CompoundTag tag) {};

    };
    
};
