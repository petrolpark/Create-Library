package petrolpark.mc.library.core.world.entity.player.team;

import java.util.List;

import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.world.entity.player.team.packet.BindTeamItemPacket;
import petrolpark.mc.library.core.world.entity.player.team.singleplayer.SinglePlayerTeam;
import petrolpark.mc.library.registry.PetrolparkDataComponentTypes;
import petrolpark.mc.library.util.ScreenHelper;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;

/**
 * An Item which can be linked to a {@link ITeam}.
 * @see <a href="https://github.com/petrolpark/Create-Library/wiki/Teams#accessing-teams">Usage</a>
 */
public interface ITeamBoundItem {

    /**
     * 
     * @param level
     * @param player
     * @param stack
     */
    public boolean isTeamRebindable(Level level, Player player, ItemStack stack);

    @OnlyIn(Dist.CLIENT)
    public Component getTeamSelectionScreenTitle(Level level, Player player, ItemStack stack);

    /**
     * Attempt to Bind a {@link ITeam} to the given Item Stack.
     * If the Player is a member of only one Team (typically, their {@link SinglePlayerTeam}), this is bound instantly.
     * If the Player is a member of multiple Teams, a menu is opened, allowing them to select one. Once a selection has been made, the chosen Team will be bound to the Item Stack in the Players main hand (hopefully still the same one).
     * @param stack
     * @param player
     * @param level
     */
    public default InteractionResult trySelectTeam(ItemStack stack, Player player, Level level) {
        if (!getTeam(stack, level).isNone() && !isTeamRebindable(level, player, stack)) return InteractionResult.PASS;
        GatherTeamProvidersEvent event = new GatherTeamProvidersEvent(player);
        NeoForge.EVENT_BUS.post(event);
        if (event.getTeamProvidersUnmodifiable().size() == 1) {
            bind(event.getTeamProvidersUnmodifiable().get(0), stack, player); // Don't open screen if only one Team is available
        } else if (level.isClientSide()) {
            Petrolpark.unsafeRunClient(() -> () -> openScreen(getTeamSelectionScreenTitle(level, player, stack), event.getTeamsUnmodifiable(level)));
        };
        return InteractionResult.SUCCESS;
    };

    @OnlyIn(Dist.CLIENT)
    public static void openScreen(Component title, List<ITeam> teams) {
        ScreenHelper.openScreen(new SelectTeamScreen(title, teams, BindTeamItemPacket::new));
    };

    public static ITeam getTeam(ItemStack stack, Level level) {
        return stack.getOrDefault(PetrolparkDataComponentTypes.TEAM_PROVIDER, NoTeam.INSTANCE).provideTeam(level);
    };
    
    public default void bind(ITeam.Provider teamProvider, ItemStack stack, Player player) {
        if (stack.getItem() != this) return;
        if (!isTeamRebindable(player.level(), player, stack) && stack.has(PetrolparkDataComponentTypes.TEAM_PROVIDER)) return;
        stack.set(PetrolparkDataComponentTypes.TEAM_PROVIDER, teamProvider);
    };
};
