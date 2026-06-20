package com.petrolpark.core.world.entity.player.team;

import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.Nullable;

import com.petrolpark.Petrolpark;
import com.petrolpark.registry.PetrolparkTeamProviderTypes;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@ParametersAreNonnullByDefault
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
    public Stream<String> streamMemberUsernames() {
        return Stream.empty();
    };

    @Override
    @OnlyIn(Dist.DEDICATED_SERVER)
    public Stream<Player> streamMembers() {
        return Stream.empty();
    };

    @Override
    public boolean isAdmin(Player player) {
        return false;
    };

    @Override
    public Component getName() {
        return Component.translatable("team.petrolpark.team.none");
    };

    @Override
    public void renderIcon(GuiGraphics graphics) {
        //TODO
    };

    @Override
    public Component getRenderedMemberList(int maxTextWidth) {
        return Component.translatable(Petrolpark.translationKey("generic.list.none"));
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
