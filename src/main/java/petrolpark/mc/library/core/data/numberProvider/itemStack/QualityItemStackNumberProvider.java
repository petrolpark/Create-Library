package petrolpark.mc.library.core.data.numberProvider.itemStack;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import petrolpark.mc.library.compat.pquality.OptionalQuality;
import petrolpark.mc.library.core.data.numberProvider.NumberEstimate;
import petrolpark.mc.library.registry.PetrolparkNumberProviderTypes;
import petrolpark.mc.library.util.Lang;

/**
 * <p>{@code petrolpark:quality}</p>
 * 
 * @author petrolpark
 */
@ParametersAreNonnullByDefault
public record QualityItemStackNumberProvider(QualityValue value) implements ItemStackNumberProvider {

    public static final MapCodec<QualityItemStackNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        StringRepresentable.fromEnum(QualityValue::values).optionalFieldOf("value", QualityValue.MULTIPLIER).forGetter(QualityItemStackNumberProvider::value)
    ).apply(instance, QualityItemStackNumberProvider::new));

    @Override
    public float getFloat(ItemStack stack, LootContext lootContext) {
        return value().getFloat(stack);
    };

    @Override
    public int getInt(ItemStack stack, LootContext lootContext) {
        return value().getInt(stack);
    };

    @Override
    public NumberEstimate getEstimate() {
        return NumberEstimate.POSITIVE;
    };

    @Override
    public LootItemStackNumberProviderType getItemStackNumberProviderType() {
        return PetrolparkNumberProviderTypes.ITEM_QUALITY.get();
    };

    public static enum QualityValue implements StringRepresentable {

        MULTIPLIER() {
            @Override
            public float getFloat(ItemStack stack) {
                return OptionalQuality.multiply(stack, 1f);
            };
            @Override
            public int getInt(ItemStack stack) {
                return OptionalQuality.multiply(stack, 1);
            };
        },
        BIG_MULTIPLIER() {
            @Override
            public float getFloat(ItemStack stack) {
                return OptionalQuality.bigMultiply(stack, 1f);
            };
            @Override
            public int getInt(ItemStack stack) {
                return OptionalQuality.bigMultiply(stack, 1);
            };
        },
        REDUCER() {
            @Override
            public float getFloat(ItemStack stack) {
                return OptionalQuality.reduce(stack, 1f);
            };
            @Override
            public int getInt(ItemStack stack) {
                return OptionalQuality.reduce(stack, 1);
            };
        };

        public final String name;

        public abstract float getFloat(ItemStack stack);
        public abstract int getInt(ItemStack stack);

        QualityValue() {
            this.name = Lang.asId(name());
        };

        @Override
        public String getSerializedName() {
            return name;
        };

        public static QualityValue getByName(String name) {
            for (QualityValue value : values()) if (value.name.equals(name)) return value;
            throw new IllegalArgumentException("Invalid entity target " + name);
        };
    };
    
};
