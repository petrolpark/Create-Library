package com.petrolpark.core.data.loot.wish;

import com.petrolpark.core.data.recipe.ingredient.advanced.IAdvancedIngredient;
import com.petrolpark.core.data.recipe.ingredient.advanced.ItemAdvancedIngredient;
import com.petrolpark.registry.PetrolparkPackets;

import net.createmod.catnip.net.base.ClientboundPacketPayload;
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
        ClientWishToastHelper.tryShowToast(wish(), stack());
    };

};
