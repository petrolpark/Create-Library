package com.petrolpark.network.packet;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;

public abstract class C2SPacket {
    
    public abstract void toBytes(FriendlyByteBuf buffer);

    public abstract boolean handle(Supplier<NetworkEvent.Context> supplier);
};
