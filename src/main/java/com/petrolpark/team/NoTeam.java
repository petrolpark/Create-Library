package com.petrolpark.team;

import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import com.petrolpark.team.data.ITeamDataType;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class NoTeam implements ITeam, ITeam.Provider {

    public static final NoTeam INSTANCE = new NoTeam();

    @Override
    public ITeam.Provider getProvider() {
        return this;
    };

    @Override
    public ProviderType getProviderType() {
        return PetrolparkTeamProviderTypes.NONE.get();
    };

    @Override
    public ITeam provideTeam(Level level) {
        return this;
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
    public void setChanged(Level level, ITeamDataType<?> dataType) {};

    @Override
    public void renderIcon(GuiGraphics graphics) {
        //TODO
    };

    @Override
    public Component getRenderedMemberList(int maxTextWidth) {
        return Component.translatable("petrolpark.generic.list.none");
    };

    @Override
    public <T> @Nullable T set(@Nonnull DataComponentType<? super T> componentType, @Nonnull T value) {
        return null;
    };

    @Override
    public <T> @Nullable T remove(@Nonnull DataComponentType<? extends T> componentType) {
        return null;
    };

    @Override
    public void applyComponents(@Nonnull DataComponentPatch patch) {
        //NOOP
    };

    @Override
    public void applyComponents(@Nonnull DataComponentMap components) {
        //NOOP
    };

    @Override
    public DataComponentMap getComponents() {
        return DataComponentMap.EMPTY;
    };
    
};
