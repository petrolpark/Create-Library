package petrolpark.mc.library.compat.create.core.world.item.valueSettings;

import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour.ValueSettings;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;

import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import petrolpark.mc.library.Petrolpark;

@ParametersAreNonnullByDefault
public interface IValueSettingsItem {

    public default InteractionResultHolder<ItemStack> useValueSettingsItem(Level level, Player player, InteractionHand usedHand) {
        final ItemStack stack = player.getItemInHand(usedHand);
        Petrolpark.unsafeRunClient(() -> () -> ScreenOpener.open(new ItemValueSettingsScreen(usedHand, createValueSettingsBoard(player, usedHand, stack), getValueSettings(stack))));
        return InteractionResultHolder.success(stack);
    };
    
    public ValueSettingsBoard createValueSettingsBoard(Player player, InteractionHand hand, ItemStack stack);

    public ValueSettings getValueSettings(ItemStack stack);

    public void setValueSettings(ItemStack stack, ValueSettings valueSettings, boolean ctrlDown);
};
