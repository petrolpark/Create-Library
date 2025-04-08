package com.petrolpark.core.team.packet;

import com.petrolpark.PetrolparkPackets;
import com.petrolpark.core.team.ITeam;
import com.petrolpark.core.team.ITeamBoundBlockEntity;

import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

public class BindTeamBlockPacket extends BindTeamPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, BindTeamBlockPacket> STREAM_CODEC = StreamCodec.composite(
        ITeam.Provider.STREAM_CODEC, BindTeamBlockPacket::getTeamProvider,
        CatnipStreamCodecs.BLOCK_HIT_RESULT, BindTeamBlockPacket::getHit,
        BindTeamBlockPacket::new
    );

    public final BlockHitResult hit;

    public BindTeamPacket.Factory forHit(BlockHitResult hit) {
        return new Factory(hit);
    };

    public BindTeamBlockPacket(ITeam.Provider teamProvider, BlockHitResult hit) {
        super(teamProvider);
        this.hit = hit;
    };

    public BlockHitResult getHit() {
        return hit;
    };

    @Override
    public void handle(ITeam.Provider teamProvider, ServerPlayer player) {
        BlockEntity be = player.level().getBlockEntity(hit.getBlockPos());
        if (be instanceof ITeamBoundBlockEntity tbbe) tbbe.bind(teamProvider, player, hit);
    };

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PetrolparkPackets.BIND_TEAM_BLOCK;
    };

    private static class Factory implements BindTeamPacket.Factory {

        public final BlockHitResult hit;

        public Factory(BlockHitResult hit) {
            this.hit = hit;
        };

        @Override
        public BindTeamPacket create(ITeam.Provider teamProvider) {
            return new BindTeamBlockPacket(teamProvider, hit);
        };
    };

    @Override
    public Component getDescription(ServerLevel level) {
        return translate(teamProvider.provideTeam(level).getName(), hit.getBlockPos().toShortString());
    };
    
};
