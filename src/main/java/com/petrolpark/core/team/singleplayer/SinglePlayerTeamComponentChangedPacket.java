package com.petrolpark.core.team.singleplayer;

import com.petrolpark.PetrolparkPackets;

import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record SinglePlayerTeamComponentChangedPacket(DataComponentPatch patch) implements ClientboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, SinglePlayerTeamComponentChangedPacket> STREAM_CODEC = DataComponentPatch.STREAM_CODEC.map(SinglePlayerTeamComponentChangedPacket::new, SinglePlayerTeamComponentChangedPacket::patch);

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PetrolparkPackets.SINGLE_PLAYER_TEAM_COMPONENT_CHANGED;
    };

    @Override
    public void handle(LocalPlayer player) {
        SinglePlayerTeam.get(player).applyComponents(patch);
    };
    
};
