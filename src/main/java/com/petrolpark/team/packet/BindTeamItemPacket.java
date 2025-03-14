package com.petrolpark.team.packet;

import com.petrolpark.team.ITeam;
import com.petrolpark.team.ITeamBoundItem;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class BindTeamItemPacket extends BindTeamPacket {

    @SuppressWarnings("unchecked")
    public BindTeamItemPacket(ITeam.Provider team) {
        super((T)team);
    };

    public BindTeamItemPacket(FriendlyByteBuf buffer) {
        super(buffer);
    };

    @Override
    public void handle(ITeam team, Context context) {
        ItemStack heldStack = context.getSender().getMainHandItem();
        if (heldStack.getItem() instanceof ITeamBoundItem<?> bindableItem) bindableItem.bind(team, heldStack, context.getSender());
    };
    
};
