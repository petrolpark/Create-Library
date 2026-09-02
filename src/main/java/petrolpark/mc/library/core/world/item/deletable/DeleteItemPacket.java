package petrolpark.mc.library.core.world.item.deletable;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import petrolpark.mc.library.registry.PetrolparkPackets;

public record DeleteItemPacket(int slotIndex) implements ServerboundPacketPayload {

    public static final StreamCodec<ByteBuf, DeleteItemPacket> STREAM_CODEC = ByteBufCodecs.INT.map(DeleteItemPacket::new, DeleteItemPacket::slotIndex);

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PetrolparkPackets.DELETE_ITEM;
    };

    @Override
    public void handle(ServerPlayer player) {
        final AbstractContainerMenu menu = player.containerMenu;
        if (menu == null) return;
        if (slotIndex() < 0 || slotIndex() >= menu.slots.size()) return;
        final Slot slot = menu.slots.get(slotIndex());
        if (slot.allowModification(player) && slot.getItem().getItem() instanceof IDeletableItem deletableItem) {
            slot.tryRemove(player.isShiftKeyDown() ? Integer.MAX_VALUE : 1, Integer.MAX_VALUE, player).ifPresent(stack -> deletableItem.delete(player, stack));
        };
    };
    
};
