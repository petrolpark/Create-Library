package petrolpark.mc.library.core.world.item.compression;

import java.util.HashMap;

import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public class SyncClientItemCompressionsPacket extends HashMap<ItemStack, IItemCompression> implements ClientboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncClientItemCompressionsPacket> COMPRESSIONS_CODEC = ByteBufCodecs.map(SyncClientItemCompressionsPacket::new, ItemStack.STREAM_CODEC, IItemCompression.STREAM_CODEC);

    public SyncClientItemCompressionsPacket(int size) {
        super(size);
    };

    @Override
    public PacketTypeProvider getTypeProvider() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTypeProvider'");
    };

    @Override
    public void handle(LocalPlayer player) {
        ItemCompressionManager.COMPRESSIONS.clear();
        ItemCompressionManager.COMPRESSIONS.putAll(this);
        ItemCompressionManager.rebuildCompressionSequences();
    };
    
};
