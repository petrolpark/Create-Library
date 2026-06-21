package petrolpark.mc.library.compat.create.core.world.dough.topping;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import petrolpark.mc.library.compat.create.core.world.dough.DoughData;
import petrolpark.mc.library.compat.create.registry.PetrolparkDoughToppingTypes;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record SimpleTopping(String translationKey, ResourceLocation textureLocation, int tint) implements IDoughTopping {

    public static final MapCodec<SimpleTopping> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.STRING.fieldOf("translation_key").forGetter(SimpleTopping::translationKey),
        ResourceLocation.CODEC.fieldOf("texture").forGetter(SimpleTopping::textureLocation),
        Codec.INT.optionalFieldOf("tint", 0xFFFFFFFF).forGetter(SimpleTopping::tint)
    ).apply(instance, SimpleTopping::new));

    public static final StreamCodec<ByteBuf, SimpleTopping> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, SimpleTopping::translationKey,
        ResourceLocation.STREAM_CODEC, SimpleTopping::textureLocation,
        ByteBufCodecs.INT, SimpleTopping::tint,
        SimpleTopping::new  
    );

    @Override
    public Component name() {
        return Component.translatable(translationKey());
    };

    @Override
    public ResourceLocation textureLocation(DoughData doughData) {
        return textureLocation();
    };

    @Override
    public int getTint(DoughData doughData) {
        return tint();
    };

    @Override
    public DoughToppingType<SimpleTopping> getType() {
        return PetrolparkDoughToppingTypes.SIMPLE.get();
    };
    
};
