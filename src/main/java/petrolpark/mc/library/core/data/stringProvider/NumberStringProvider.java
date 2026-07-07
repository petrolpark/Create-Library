package petrolpark.mc.library.core.data.stringProvider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.createmod.catnip.lang.Lang;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import petrolpark.mc.library.registry.PetrolparkStringProviderTypes;

public record NumberStringProvider(NumberProvider numberProvider, NumberStringProvider.Format format) implements StringProvider {

    public static final MapCodec<NumberStringProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        NumberProviders.CODEC.fieldOf("number").forGetter(NumberStringProvider::numberProvider),
        NumberStringProvider.Format.CODEC.optionalFieldOf("format", NumberStringProvider.Format.INTEGER).forGetter(NumberStringProvider::format)
    ).apply(instance, NumberStringProvider::new));

    @Override
    public Component getString(LootContext context) {
        return format().format(numberProvider().getFloat(context), numberProvider().getInt(context));
    };

    @Override
    public StringProviderType getStringProviderType() {
        return PetrolparkStringProviderTypes.NUMBER.get();
    };

    public enum Format implements StringRepresentable {

        INTEGER {
            @Override
            public Component format(float asFloat, int asInt) {
                return Component.literal("" + asInt);
            };
        },
        TIME {
            @Override
            public Component format(float asFloat, int asInt) {
                final int seconds = (asInt / 20) % 60;
                final int minutes = asInt / 1200;
                //todo "a long time" if too many minutes
                return Component.literal(String.format("%02d:%02d", minutes, seconds));
            };
        },
        ENCHANTMENT_LEVEL {
            @Override
            public Component format(float asFloat, int asInt) {
                return Component.translatable("enchantment.level." + asInt);
            };
        },
        ;

        public static final Codec<NumberStringProvider.Format> CODEC = StringRepresentable.fromEnum(Format::values);

        private final String name;

        Format() {
            this.name = Lang.asId(name());
        };

        public abstract Component format(float asFloat, int asInt);

        @Override
        public String getSerializedName() {
            return name;
        };
    };
    
};
