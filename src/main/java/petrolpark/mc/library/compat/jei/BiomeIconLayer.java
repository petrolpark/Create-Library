package petrolpark.mc.library.compat.jei;

import java.util.List;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.createmod.catnip.lang.Lang;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.Biome;
import petrolpark.mc.library.util.Neither;

public record BiomeIconLayer(ResourceLocation texture, Neither<Integer, BiomeIconLayer.ColorSource> color) {
  
    public static final Codec<BiomeIconLayer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("texture").forGetter(BiomeIconLayer::texture),
        Neither.fieldCodec(Codec.INT, StringRepresentable.fromEnum(BiomeIconLayer.ColorSource::values), "color").forGetter(BiomeIconLayer::color)
    ).apply(instance, BiomeIconLayer::new));

    public static final List<BiomeIconLayer> DEFAULT = List.of(
        new BiomeIconLayer(null, null),
        new BiomeIconLayer(null, null)
    );

    public enum ColorSource implements StringRepresentable {

        FOG(Biome::getFogColor),
        GRASS(biome -> biome.getGrassColor(0d, 0d)),
        FOLIAGE(Biome::getFoliageColor),
        WATER(Biome::getWaterColor),
        WATER_FOG(Biome::getWaterFogColor);

        private final String name;
        public final Function<Biome, Integer> colorGetter;

        ColorSource(Function<Biome, Integer> colorGetter) {
            name = Lang.asId(name());
            this.colorGetter = colorGetter;
        };

        @Override
        public String getSerializedName() {
            return name;
        };
    };
};
