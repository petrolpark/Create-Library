package com.petrolpark.core.team.scoreboard;

import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkPackets;

import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record ScoreboardTeamComponentChangedPacket(String teamName, DataComponentPatch patch) implements ClientboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ScoreboardTeamComponentChangedPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, ScoreboardTeamComponentChangedPacket::teamName,
        DataComponentPatch.STREAM_CODEC, ScoreboardTeamComponentChangedPacket::patch,
        ScoreboardTeamComponentChangedPacket::new
    );

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PetrolparkPackets.SCOREBOARD_TEAM_COMPONENT_CHANGED;
    };

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(LocalPlayer player) {
        Petrolpark.SCOREBOARD_TEAMS.applyPatch(player.level(), teamName, patch);
    };
    
};
