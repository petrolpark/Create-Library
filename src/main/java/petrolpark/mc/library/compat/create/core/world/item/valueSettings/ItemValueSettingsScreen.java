package petrolpark.mc.library.compat.create.core.world.item.valueSettings;

import com.simibubi.create.AllKeys;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour.ValueSettings;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsScreen;

import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;

public class ItemValueSettingsScreen extends ValueSettingsScreen {

    protected final InteractionHand hand;

    public ItemValueSettingsScreen(InteractionHand hand, ValueSettingsBoard board, ValueSettings valueSettings) {
        super(BlockPos.ZERO, board, valueSettings, $ -> {}, 0);
        this.hand = hand;
    };

    @Override
    protected void saveAndClose(double pMouseX, double pMouseY) {
        final ValueSettings closest = getClosestCoordinate((int) pMouseX, (int) pMouseY);
        CatnipServices.NETWORK.sendToServer(new ItemValueSettingsPacket(closest.row(), closest.value(), hand, AllKeys.ctrlDown()));
        onClose();
    };
    
};
