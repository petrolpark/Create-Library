package petrolpark.mc.library.core.world.item.decay.product;

import com.mojang.serialization.Codec;
import petrolpark.mc.library.registry.PetrolparkRegistries;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public interface IDecayProduct {

    /**
     * Use {@link IDecayProduct#CODEC} instead.
     */
    static final Codec<IDecayProduct> TYPED_CODEC = PetrolparkRegistries.DECAY_PRODUCT_TYPES
        .byNameCodec()
        .dispatch(IDecayProduct::getType, DecayProductType::codec);

    public static final Codec<IDecayProduct> CODEC = Codec.lazyInitialized(() -> TYPED_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, IDecayProduct> STREAM_CODEC = ByteBufCodecs.registry(PetrolparkRegistries.Keys.DECAY_PRODUCT_TYPE)
        .dispatch(IDecayProduct::getType, DecayProductType::streamCodec);

    public ItemStack get(ItemStack stack);
    
    public DecayProductType getType();
};
