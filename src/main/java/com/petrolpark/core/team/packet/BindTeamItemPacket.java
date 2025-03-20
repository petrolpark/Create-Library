package com.petrolpark.core.team.packet;

import com.petrolpark.PetrolparkPackets;
import com.petrolpark.core.team.ITeam;
import com.petrolpark.core.team.ITeamBoundItem;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class BindTeamItemPacket extends BindTeamPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, BindTeamItemPacket> STREAM_CODEC = StreamCodec.composite(
        ITeam.Provider.STREAM_CODEC, BindTeamItemPacket::getTeamProvider,
        BindTeamItemPacket::new
    );

    public BindTeamItemPacket(ITeam.Provider teamProvider) {
        super(teamProvider);
    };

    @Override
    public void handle(ITeam.Provider teamProvider, ServerPlayer player) {
        ItemStack heldStack = player.getMainHandItem();
        if (heldStack.getItem() instanceof ITeamBoundItem<?> bindableItem) bindableItem.bind(teamProvider, heldStack, player);
    };

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PetrolparkPackets.BIND_TEAM_ITEM;
    };
    
};
