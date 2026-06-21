package petrolpark.mc.library.core.client.texts;

import petrolpark.mc.library.PetrolparkClient;
import petrolpark.mc.library.registry.PetrolparkPackets;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record RequestTextPacket(ResourceLocation id) implements ClientboundPacketPayload {

    public static final StreamCodec<ByteBuf, RequestTextPacket> STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, RequestTextPacket::id, RequestTextPacket::new);

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PetrolparkPackets.REQUEST_TEXT;
    };

    @Override
    public void handle(LocalPlayer player) {
        CatnipServices.NETWORK.sendToServer(new ReplyTextPacket(PetrolparkClient.TEXTS.getText(id(), Minecraft.getInstance().getLanguageManager().getSelected(), player.getRandom())));
    };
    
};
