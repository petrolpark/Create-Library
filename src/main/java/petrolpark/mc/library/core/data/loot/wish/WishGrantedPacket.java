package petrolpark.mc.library.core.data.loot.wish;

import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.ItemAdvancedIngredient;
import petrolpark.mc.library.registry.PetrolparkPackets;

public record WishGrantedPacket(IAdvancedIngredient<ItemStack> wish, ItemStack stack) implements ClientboundPacketPayload {

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
