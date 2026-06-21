package petrolpark.mc.library.compat.create.core.world.dough.topping;

import java.util.Collections;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import petrolpark.mc.library.compat.create.core.world.dough.DoughData;
import petrolpark.mc.library.compat.create.core.world.dough.type.ICookableDough;
import petrolpark.mc.library.compat.create.registry.PetrolparkDoughToppingTypes;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public record CookableTopping(String translationKey, List<ResourceLocation> textures, List<Integer> tints) implements IDoughTopping {

    public static final MapCodec<CookableTopping> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.STRING.fieldOf("translation_keys").forGetter(CookableTopping::translationKey),
        ResourceLocation.CODEC.listOf(1, 64).fieldOf("textures").forGetter(CookableTopping::textures),
        Codec.INT.listOf(1, 64).optionalFieldOf("tints", Collections.singletonList(0xFFFFFFFF)).forGetter(CookableTopping::tints)
    ).apply(instance, CookableTopping::new));

    public static final StreamCodec<ByteBuf, CookableTopping> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, CookableTopping::translationKey,
        ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), CookableTopping::textures,
        ByteBufCodecs.INT.apply(ByteBufCodecs.list()), CookableTopping::tints,
        CookableTopping::new
    );

    @Override
    public Component name() {
        return Component.translatable(translationKey());
    };

    @Override
    public ResourceLocation textureLocation(DoughData doughData) {
        return get(textures(), doughData);
    };

    @Override
    public int getTint(DoughData doughData) {
        return get(tints(), doughData);
    };

    public <T> T get(List<T> list, DoughData data) {
        final int timesCooked = data.dough() instanceof ICookableDough cookableDough ? cookableDough.getTimesCooked() : 0;
        return list.get(Mth.clamp(timesCooked, 0, list.size() - 1));
    };

    @Override
    public DoughToppingType<CookableTopping> getType() {
        return PetrolparkDoughToppingTypes.COOKABLE.get();
    };
    
};
