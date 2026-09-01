package petrolpark.mc.library.compat.create.core.world.item.valueSettings;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour.ValueSettings;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;

import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreatePackets;

public record ItemValueSettingsPacket(int row, int value, InteractionHand hand, boolean ctrlDown) implements ServerboundPacketPayload {

    public static final StreamCodec<FriendlyByteBuf, ItemValueSettingsPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, ItemValueSettingsPacket::row,
        ByteBufCodecs.INT, ItemValueSettingsPacket::value,
        CatnipStreamCodecs.HAND, ItemValueSettingsPacket::hand,
        ByteBufCodecs.BOOL, ItemValueSettingsPacket::ctrlDown,
        ItemValueSettingsPacket::new
    );

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PetrolparkCreatePackets.ITEM_VALUE_SETTINGS;
    };

    @Override
    public void handle(ServerPlayer player) {
        final ItemStack stack = player.getItemInHand(hand());
        if (stack.getItem() instanceof IValueSettingsItem item) {
            final ValueSettingsBoard board = item.createValueSettingsBoard(player, hand(), stack);
            if (row() < 0 || row() >= board.rows().size() || value() < 0 || value() > board.maxValue()) return;
            item.setValueSettings(stack, new ValueSettings(row(), value()), ctrlDown());
        };
    };
    
};
