package com.petrolpark.core.wish;

import com.petrolpark.PetrolparkPackets;
import com.petrolpark.core.recipe.ingredient.advanced.IAdvancedIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.ItemAdvancedIngredient;

import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record WishGrantedPacket(IAdvancedIngredient<? super ItemStack> wish, ItemStack stack) implements ClientboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, WishGrantedPacket> STREAM_CODEC = StreamCodec.composite(
        ItemAdvancedIngredient.STREAM_CODEC, WishGrantedPacket::wish,
        ItemStack.STREAM_CODEC, WishGrantedPacket::stack,
        WishGrantedPacket::new
    );

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PetrolparkPackets.GRANT_WISH;
    };

    @Override
    public void handle(LocalPlayer player) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getToasts().getToast(WishGrantedToast.class, wish()) == null) mc.getToasts().addToast(new WishGrantedToast(wish(), stack()));
    };
    
};
