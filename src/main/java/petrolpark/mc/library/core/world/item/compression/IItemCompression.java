package petrolpark.mc.library.core.world.item.compression;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public interface IItemCompression {

    public static final Codec<IItemCompression> CODEC = RecordCodecBuilder.<IItemCompression>create(instance -> instance.group(
        Codec.INT.fieldOf("count").forGetter(IItemCompression::count),
        ItemStack.CODEC.fieldOf("result").forGetter(IItemCompression::result)
    ).apply(instance, IItemCompression::create));

    public static final StreamCodec<RegistryFriendlyByteBuf, IItemCompression> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, IItemCompression::count,
        ItemStack.STREAM_CODEC, IItemCompression::result,
        IItemCompression::create
    );

    public static IItemCompression create(int count, ItemStack result) {
        if (count == 0 || result.isEmpty()) return IItemCompression.NONE;
        return new ItemCompression(count, result);
    };
    
    public int count();

    public ItemStack result();

    public default float ratio() {
        return (float)count() / (float)result().getCount();
    };

    public static final IItemCompression NONE = new IItemCompression() {

        @Override
        public int count() {
            return 0;
        };

        @Override
        public ItemStack result() {
            return ItemStack.EMPTY;
        };

        @Override
        public float ratio() {
            return 0f;
        };
        
    };
};
