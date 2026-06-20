package com.petrolpark.core.world.entity.player.team.packet;

import com.petrolpark.core.world.entity.player.team.ITeam;
import com.petrolpark.core.world.entity.player.team.ITeamBoundItem;
import com.petrolpark.registry.PetrolparkPackets;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
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
        if (heldStack.getItem() instanceof ITeamBoundItem bindableItem) bindableItem.bind(teamProvider, heldStack, player);
    };

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PetrolparkPackets.BIND_TEAM_ITEM;
    };

    @Override
    public Component getDescription(ServerLevel level) {
        return translate(teamProvider.provideTeam(level).getName());
    };
    
};
