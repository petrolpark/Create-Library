package petrolpark.mc.library.mixin;

import java.util.Optional;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import petrolpark.mc.library.core.world.entity.player.extendedInventory.ExtendedInventory;

import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin implements ServerGamePacketListener {

    @Shadow
    public ServerPlayer player;

    @Shadow
    static Logger LOGGER;
    
    @Inject(
        method = "handleSetCarriedItem",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/server/level/ServerLevel;)V",
            shift = Shift.AFTER
        ),
        cancellable = true
    )
    public void petrolpark$setExtendedInventoryCarriedItem(ServerboundSetCarriedItemPacket packet, CallbackInfo ci) {
        final Optional<ExtendedInventory> invOp = ExtendedInventory.get(player);
        if (invOp.isEmpty()) return;
        final ExtendedInventory inv = invOp.get();
        if (inv.isFullHotbarSlot(packet.getSlot())) {
            if (player.getInventory().selected != packet.getSlot() && player.getUsedItemHand() == InteractionHand.MAIN_HAND) {
                player.stopUsingItem();
            };

            inv.selected = packet.getSlot();
            player.resetLastActionTime();

            ci.cancel();
        };
    };

    @Inject(
        method = "handleSetCreativeModeSlot",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT,
        cancellable = true
    )
    public void petrolpark$setExtendedInventoryCreativeModeSlot(ServerboundSetCreativeModeSlotPacket packet, CallbackInfo ci, boolean flag, ItemStack itemstack, CustomData customData) {
        if (packet.slotNum() >= 1 && packet.slotNum() < player.inventoryMenu.slots.size() && (itemstack.isEmpty() || itemstack.getDamageValue() >= 0 && !itemstack.isEmpty())) {
            player.inventoryMenu.getSlot(packet.slotNum()).setByPlayer(itemstack);
            player.inventoryMenu.broadcastChanges();
            ci.cancel();
        };
    };
};
