package petrolpark.mc.library.core.data.loot.wish;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.ItemAdvancedIngredient;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.createmod.catnip.codecs.CatnipCodecUtils;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

public class PlayerWishList extends AbstractWishList {

    public static final Codec<Map<IAdvancedIngredient<? super ItemStack>, Integer>> CODEC = Codec.unboundedMap(ItemAdvancedIngredient.CODEC, Codec.INT);
    public static final IAttachmentSerializer<Tag, PlayerWishList> SERIALIZER = new Serializer();

    protected final Player player;
    protected final Object2IntMap<IAdvancedIngredient<? super ItemStack>> wishes = new Object2IntOpenHashMap<>();

    public PlayerWishList(IAttachmentHolder attachmentHolder) {
        this(attachmentHolder, Collections.emptyMap());
    };

    public PlayerWishList(PlayerWishList oldList, IAttachmentHolder attachmentHolder, HolderLookup.Provider registries) {
        this(attachmentHolder, oldList.wishes);
    };

    public PlayerWishList(IAttachmentHolder attachmentHolder, Map<IAdvancedIngredient<? super ItemStack>, Integer> wishes) {
        if (!(attachmentHolder instanceof Player player)) throw new IllegalArgumentException("Only Players can have WishLists");
        this.player = player;
        this.wishes.putAll(wishes);
        this.wishes.defaultReturnValue(0);
    };

    @Override
    public Collection<IAdvancedIngredient<? super ItemStack>> getWishes() {
        return wishes.keySet();
    };

    @Override
    public int getWishInstanceCount(IAdvancedIngredient<? super ItemStack> wish, int maxFulfillments) {
        return Math.min(maxFulfillments, wishes.getInt(wish));
    };

    @Override
    public void fulfillWish(IAdvancedIngredient<? super ItemStack> wish, ItemStack stack) {
        if (wishes.containsKey(wish)) {
            if (player instanceof ServerPlayer serverPlayer) CatnipServices.NETWORK.sendToClient(serverPlayer, new WishGrantedPacket(wish, stack));
        };
        wishes.computeInt(wish, (w, c) -> c == 1 ? null : c - 1);
    };

    public static class Serializer implements IAttachmentSerializer<Tag, PlayerWishList> {

        @Override
        public PlayerWishList read(@Nonnull IAttachmentHolder holder, @Nonnull Tag tag, @Nonnull HolderLookup.Provider provider) {
            return CatnipCodecUtils.decode(CODEC, tag).map(map -> new PlayerWishList(holder, map)).orElse(new PlayerWishList(holder));
        };

        @Override
        public @Nullable Tag write(@Nonnull PlayerWishList attachment, @Nonnull HolderLookup.Provider provider) {
            return CatnipCodecUtils.encode(CODEC, attachment.wishes).orElse(null);
        };

    };
    
};
