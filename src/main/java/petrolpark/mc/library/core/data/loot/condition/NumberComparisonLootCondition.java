package petrolpark.mc.library.core.data.loot.condition;

import javax.annotation.Nonnull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.createmod.catnip.lang.Lang;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import petrolpark.mc.library.registry.PetrolparkLootConditionTypes;

public record NumberComparisonLootCondition(NumberProvider first, NumberProvider second, Comparison comparison, boolean useInts) implements LootItemCondition {

    public static final MapCodec<NumberComparisonLootCondition> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            NumberProviders.CODEC.fieldOf("first").forGetter(NumberComparisonLootCondition::first),
            NumberProviders.CODEC.fieldOf("second").forGetter(NumberComparisonLootCondition::second),
            Comparison.CODEC.fieldOf("operation").forGetter(NumberComparisonLootCondition::comparison),
            Codec.BOOL.optionalFieldOf("compare_integers", false).forGetter(NumberComparisonLootCondition::useInts)
        ).apply(instance, NumberComparisonLootCondition::new)
    );
  
    @Override
    public boolean test(LootContext context) {
        return useInts() ? comparison().compareInt(first().getInt(context), second().getInt(context)) : comparison().compareFloat(first().getFloat(context), second().getFloat(context));
    };

    public enum Comparison implements StringRepresentable {

        EQUALS {
            @Override
            public boolean compareInt(int int1, int int2) {
                return int1 == int2;
            }

            @Override
            public boolean compareFloat(float float1, float float2) {
                return Math.abs(float1 - float2) <= 1 / 256 / 256;
            }
        },
        NOT_EQUAL {
            @Override
            public boolean compareInt(int int1, int int2) {
                return int1 != int2;
            }

            @Override
            public boolean compareFloat(float float1, float float2) {
                return !Comparison.EQUALS.compareFloat(float1, float2);
            }
        },
        GREATER_THAN {
            @Override
            public boolean compareInt(int int1, int int2) {
                return int1 > int2;
            }

            @Override
            public boolean compareFloat(float float1, float float2) {
                return float1 > float2;
            }
        },
        LESS_THAN {
            @Override
            public boolean compareInt(int int1, int int2) {
                return int1 < int2;
            }

            @Override
            public boolean compareFloat(float float1, float float2) {
                return float1 < float2;
            }
        },
        GEQ {
            @Override
            public boolean compareInt(int int1, int int2) {
                return int1 >= int2;
            }

            @Override
            public boolean compareFloat(float float1, float float2) {
                return float1 >= float2;
            }
        },
        LEQ {
            @Override
            public boolean compareInt(int int1, int int2) {
                return int1 <= int2;
            }

            @Override
            public boolean compareFloat(float float1, float float2) {
                return float1 <= float2;
            }
        };

        public static final Codec<Comparison> CODEC = StringRepresentable.fromEnum(Comparison::values);

        private final String name;

        Comparison() {
            this.name = Lang.asId(name());  
        };

        public abstract boolean compareInt(int int1, int int2);

        public abstract boolean compareFloat(float float1, float float2);

        @Override
        public String getSerializedName() {
            return name;
        };
    };

    @Override
    public LootItemConditionType getType() {
        return PetrolparkLootConditionTypes.NUMBER_COMPARISON.get();
    };

    @Override
    public void validate(@Nonnull ValidationContext context) {
        LootItemCondition.super.validate(context);
        first().validate(context.forChild(".first_number"));
        second().validate(context.forChild(".second_number"));
    };
};
