package com.petrolpark.team;

import java.util.List;

import com.petrolpark.team.packet.BindTeamItemPacket;
import com.petrolpark.util.ScreenHelper;

import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;

public interface ITeamBoundItem<I extends Item> {

    public static final String TEAM_TAG_KEY = "Team";

    public boolean isTeamRebindable(Level level, Player player, ItemStack stack);

    @OnlyIn(Dist.CLIENT)
    public Component getTeamSelectionScreenTitle(Level level, Player player, ItemStack stack);

    public default InteractionResult trySelectTeam(ItemStack stack, Player player, Level level) {
        if (!getTeam(stack, level).isNone() && !isTeamRebindable(level, player, stack)) return InteractionResult.PASS;
        GatherTeamsEvent event = new GatherTeamsEvent(player);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.getTeamsUnmodifiable().size() == 1) {
            bind(event.getTeamsUnmodifiable().get(0), stack, player); // Don't open screen if only one Team is available
        } else if (level.isClientSide()) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> openScreen(getTeamSelectionScreenTitle(level, player, stack), event.getTeamsUnmodifiable()));
        };
        return InteractionResult.SUCCESS;
    };

    @OnlyIn(Dist.CLIENT)
    public static void openScreen(Component title, List<ITeam<?>> teams) {
        ScreenHelper.openScreen(new SelectTeamScreen(title, teams, BindTeamItemPacket::new));
    };

    public static ITeam<?> getTeam(ItemStack stack, Level level) {
        if (!(stack.getItem() instanceof ITeamBoundItem) || !stack.hasTag() || !stack.getTag().contains(TEAM_TAG_KEY, Tag.TAG_COMPOUND)) return NoTeam.INSTANCE;
        return ITeam.read(stack.getOrCreateTagElement(TEAM_TAG_KEY), level);
    };
    
    @SuppressWarnings("unchecked")
    public default <T extends ITeam<? super T>> void bind(ITeam<?> team, ItemStack stack, Player player) {
        if (stack.getItem() != this) return;
        if (!isTeamRebindable(player.level(), player, stack) && stack.hasTag() && stack.getTag().contains(TEAM_TAG_KEY, Tag.TAG_COMPOUND)) return;
        stack.getOrCreateTag().put(TEAM_TAG_KEY, ITeam.write((T)team));
    };
};
