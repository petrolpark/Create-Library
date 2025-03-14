package com.petrolpark.team;

import java.util.List;

import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkDataComponents;
import com.petrolpark.team.packet.BindTeamItemPacket;
import com.petrolpark.util.ScreenHelper;
import com.simibubi.create.foundation.utility.DistExecutor;

import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;

public interface ITeamBoundItem<I extends Item> {

    public static final String TEAM_TAG_KEY = "Team";

    public boolean isTeamRebindable(Level level, Player player, ItemStack stack);

    @OnlyIn(Dist.CLIENT)
    public Component getTeamSelectionScreenTitle(Level level, Player player, ItemStack stack);

    public default InteractionResult trySelectTeam(ItemStack stack, Player player, Level level) {
        if (!getTeam(stack, level).isNone() && !isTeamRebindable(level, player, stack)) return InteractionResult.PASS;
        GatherTeamProvidersEvent event = new GatherTeamProvidersEvent(player);
        NeoForge.EVENT_BUS.post(event);
        if (event.getTeamProvidersUnmodifiable().size() == 1) {
            bind(event.getTeamProvidersUnmodifiable().get(0), stack, player); // Don't open screen if only one Team is available
        } else if (level.isClientSide()) {
            Petrolpark.unsafeRunClient(() -> () -> openScreen(getTeamSelectionScreenTitle(level, player, stack), event.getTeamProvidersUnmodifiable()));
        };
        return InteractionResult.SUCCESS;
    };

    @OnlyIn(Dist.CLIENT)
    public static void openScreen(Component title, List<ITeam> teams) {
        ScreenHelper.openScreen(new SelectTeamScreen(title, teams, BindTeamItemPacket::new));
    };

    public static ITeam getTeam(ItemStack stack, Level level) {
        return stack.getOrDefault(PetrolparkDataComponents.TEAM_PROVIDER, NoTeam.INSTANCE).provideTeam(level);
    };
    
    @SuppressWarnings("unchecked")
    public default void bind(ITeam.Provider teamProvider, ItemStack stack, Player player) {
        if (stack.getItem() != this) return;
        if (!isTeamRebindable(player.level(), player, stack) && stack.has(PetrolparkDataComponents.TEAM_PROVIDER)) return;
        stack.set(PetrolparkDataComponents.TEAM_PROVIDER, teamProvider);
    };
};
