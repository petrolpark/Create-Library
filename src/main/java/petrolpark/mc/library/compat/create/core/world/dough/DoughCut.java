package petrolpark.mc.library.compat.create.core.world.dough;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreateRegistries;
import petrolpark.mc.library.util.Mask;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;

/**
 * @param shape
 * @param pattern
 * @param area
 */
@ApiStatus.Experimental
public record DoughCut(Mask shape, Mask pattern, float area) {

    public DoughCut(Mask shape, float area) {
        this(shape, shape.downsample(4), area);
    };

    public static final Codec<DoughCut> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Mask.friendlyCodecSized(16, 16).fieldOf("pattern").forGetter(DoughCut::pattern),
        Codec.floatRange(0f, 1f).fieldOf("area").forGetter(DoughCut::area)
    ).apply(instance, DoughCut::new));

    public static final Codec<Holder<DoughCut>> CODEC = RegistryFileCodec.create(PetrolparkCreateRegistries.Keys.DOUGH_CUT, DIRECT_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<DoughCut>> STREAM_CODEC = ByteBufCodecs.holderRegistry(PetrolparkCreateRegistries.Keys.DOUGH_CUT);
};
