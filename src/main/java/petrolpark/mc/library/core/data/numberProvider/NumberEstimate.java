package petrolpark.mc.library.core.data.numberProvider;

import java.text.DecimalFormat;
import java.util.function.Function;
import java.util.stream.IntStream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import petrolpark.mc.library.util.Lang;
import petrolpark.mc.library.util.Neither;

public sealed abstract class NumberEstimate permits NumberEstimate.Exact, NumberEstimate.Range, NumberEstimate.Unknown {

    public static final NumberEstimate ZERO = exactly(0f);
    public static final NumberEstimate ONE = exactly(1f);
    public static final NumberEstimate POSITIVE = ranged(0f, Float.NaN);
    public static final NumberEstimate E = exactly((float)Math.E);
    public static final NumberEstimate UNKNOWN = new Unknown();

    public static final MapCodec<NumberEstimate> fieldCodec(final String key) {
        return Neither.fieldCodec(NumberEstimate.Exact.CODEC, NumberEstimate.Range.CODEC, key).xmap(NumberEstimate::unwrap, NumberEstimate::wrap);
    };

    public static final StreamCodec<ByteBuf, NumberEstimate> STREAM_CODEC = Neither.streamCodec(NumberEstimate.Exact.STREAM_CODEC, NumberEstimate.Range.STREAM_CODEC).map(NumberEstimate::unwrap, NumberEstimate::wrap);

    @Deprecated
    public static float getMax(LootContext context, NumberProvider provider) {
        return getMax(context, provider, 10);
    };

    @Deprecated
    public static float getMax(LootContext context, NumberProvider provider, int rolls) {
        return switch (provider) {
            case IEstimableNumberProvider estimable -> estimable.getMaxFloat(context);
            case ConstantValue constant -> constant.value();
            case UniformGenerator uniform -> getMax(context, uniform.max(), rolls);
            case BinomialDistributionGenerator binomial -> getMax(context, binomial.n(), rolls);
            default -> (float)IntStream.range(0, rolls).mapToDouble(i -> provider.getFloat(context)).max().orElse(0);
        };
    };

    public static NumberEstimate get(NumberProvider numberProvider) {
        return switch (numberProvider) {
            case IEstimableNumberProvider estimable -> estimable.getEstimate();
            case ConstantValue constant -> exactly(constant.value());
            case UniformGenerator uniform -> ranged(get(uniform.min()), get(uniform.max()));
            case BinomialDistributionGenerator binomial -> getBinomial(binomial);
            default -> NumberEstimate.UNKNOWN;
        };
    };

    public static final NumberEstimate getBinomial(BinomialDistributionGenerator binomial) {
        NumberEstimate n = get(binomial.n());
        NumberEstimate p = get(binomial.p());
        NumberEstimate mean = n.multiply(p);
        NumberEstimate twoSD = mean.multiply(ONE.subtract(p)).pow(0.5f).multiply(2f);
        return ranged(mean.subtract(twoSD), mean.add(twoSD), true);
    };

    public static final NumberEstimate exactly(float value) {
        if (value == Float.NaN) return UNKNOWN;
        return new Exact(value, false);
    };

    public static final NumberEstimate approximately(float value) {
        if (value == Float.NaN) return UNKNOWN;
        return new Exact(value, true);
    };

    public static final NumberEstimate ranged(NumberEstimate minEstimate, NumberEstimate maxEstimate) {
        return ranged(minEstimate, maxEstimate, false);  
    };

    public static final NumberEstimate ranged(NumberEstimate minEstimate, NumberEstimate maxEstimate, boolean approximate) {
        if (minEstimate == UNKNOWN && maxEstimate == UNKNOWN) return UNKNOWN;
        return new Range(switch(minEstimate) {
            case Exact exact -> exact.value;
            case Range range -> range.min;
            case Unknown unknown -> 0f; // Should never be called
        }, switch (maxEstimate) {
            case Exact exact -> exact.value;
            case Range range -> range.max;
            case Unknown unknown -> 0f; // Should never be called
        }, approximate || minEstimate.approximate || maxEstimate.approximate);
    };

    public static final NumberEstimate ranged(float min, float max, boolean approximate) {
        if (min == Float.NaN && max == Float.NaN) return UNKNOWN;
        return new Range(min, max, approximate);
    };

    public static final NumberEstimate ranged(float min, float max) {
        return ranged(min, max, false);
    };

    private final boolean approximate;

    protected NumberEstimate(boolean approximate) {
        this.approximate = approximate;  
    };

    @OnlyIn(Dist.CLIENT)
    public MutableComponent getIntComponent() {
        return getComponent(Lang.INT_DF);
    };

    @OnlyIn(Dist.CLIENT)
    public abstract MutableComponent getComponent(DecimalFormat df);

    public final boolean approximate() {
        return approximate;
    };

    public abstract float min();

    public abstract float max();

    public abstract NumberEstimate add(NumberEstimate estimate);

    public NumberEstimate subtract(NumberEstimate estimate) {
        return add(estimate.negative());
    };

    public abstract NumberEstimate add(float value);

    public abstract NumberEstimate multiply(NumberEstimate estimate);

    public NumberEstimate divide(NumberEstimate estimate) {
        return multiply(estimate.reciprocal());
    };

    public abstract NumberEstimate multiply(float value);

    public abstract NumberEstimate reciprocal();

    public abstract NumberEstimate negative();

    public abstract NumberEstimate pow(float exponent);

    public abstract NumberEstimate exp();

    public abstract NumberEstimate or(NumberEstimate estimate);

    public abstract IntStream streamPossibleInts();

    protected abstract Neither<NumberEstimate.Exact, NumberEstimate.Range> wrap();

    private static final NumberEstimate unwrap(Neither<NumberEstimate.Exact, NumberEstimate.Range> neither) {
        return neither.map(Function.identity(), Function.identity()).orElse(UNKNOWN);
    };

    public boolean unknown() {
        return this == UNKNOWN;
    };

    public static final class Exact extends NumberEstimate {

        private static final Codec<NumberEstimate.Exact> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.FLOAT.fieldOf("value").forGetter(NumberEstimate.Exact::min),
                Codec.BOOL.optionalFieldOf("approximate", false).forGetter(NumberEstimate::approximate)
            ).apply(instance, NumberEstimate.Exact::new)
        );

        private static final StreamCodec<ByteBuf, NumberEstimate.Exact> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, NumberEstimate.Exact::min,
            ByteBufCodecs.BOOL, NumberEstimate.Exact::approximate,
            NumberEstimate.Exact::new
        );

        public final float value;

        public Exact(float value, boolean approximate) {
            super(approximate);
            this.value = value;
        };

        @Override
        public MutableComponent getComponent(DecimalFormat df) {
            return Component.literal(df.format(value));
        };

        @Override
        public float min() {
            return value;
        };

        @Override
        public float max() {
            return value;
        };

        @Override
        public NumberEstimate add(NumberEstimate estimate) {
            return switch (estimate) {
                case Exact exact -> new Exact(this.value + exact.value, approximate() || estimate.approximate);
                case Range range -> range.add(this);
                case Unknown unknown -> UNKNOWN;
            };
        };

        @Override
        public NumberEstimate.Exact add(float value) {
            return new Exact(this.value + value, approximate());
        };

        @Override
        public NumberEstimate multiply(NumberEstimate estimate) {
            return switch (estimate) {
                case Exact exact -> new Exact(this.value * exact.value, approximate() || estimate.approximate);
                case Range range -> range.multiply(this);
                case Unknown unknown -> UNKNOWN;
            };
        };

        @Override
        public NumberEstimate.Exact multiply(float value) {
            return new Exact(this.value * value, approximate());
        };

        @Override
        public NumberEstimate.Exact reciprocal() {
            return new Exact(1f / this.value, approximate());
        };

        @Override
        public NumberEstimate.Exact negative() {
            return new Exact(-this.value, approximate());
        };

        @Override
        public NumberEstimate.Exact pow(float exponent) {
            return new Exact((float)Math.pow(value, exponent), approximate());
        };

        @Override
        public NumberEstimate.Exact exp() {
            return new Exact((float)Math.exp(value), approximate());
        };

        @Override
        public NumberEstimate or(NumberEstimate estimate) {
            if (estimate instanceof Range range) return range.or(this);
            else if (estimate instanceof Exact exact) return ranged(Math.min(value, exact.value), Math.max(value, exact.value), approximate() || estimate.approximate());
            else return UNKNOWN;
        };

        @Override
        public IntStream streamPossibleInts() {
            return IntStream.of((int)value);
        };

        @Override
        protected Neither<NumberEstimate.Exact, NumberEstimate.Range> wrap() {
            return Neither.left(this);
        };

    };

    public static final class Range extends NumberEstimate {

        private static final Codec<NumberEstimate.Range> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.FLOAT.optionalFieldOf("min", Float.NaN).forGetter(NumberEstimate.Range::min),
                Codec.FLOAT.optionalFieldOf("max", Float.NaN).forGetter(NumberEstimate.Range::max),
                Codec.BOOL.optionalFieldOf("approximate", false).forGetter(NumberEstimate::approximate)
            ).apply(instance, NumberEstimate.Range::new)
        );

        private static final StreamCodec<ByteBuf, NumberEstimate.Range> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, NumberEstimate.Range::min,
            ByteBufCodecs.FLOAT, NumberEstimate.Range::max,
            ByteBufCodecs.BOOL, NumberEstimate.Range::approximate,
            NumberEstimate.Range::new
        );

        public final float min;
        public final float max;

        public Range(float min, float max, boolean approximate) {
            super(approximate);
            this.min = min;
            this.max = max;
        };

        @Override
        public MutableComponent getComponent(DecimalFormat df) {
            return Lang.range(min, max, approximate(), df);
        };

        @Override
        public float min() {
            return min();
        };

        @Override
        public float max() {
            return max();
        };

        @Override
        public NumberEstimate add(NumberEstimate estimate) {
            return switch (estimate) {
                case Exact exact -> ranged(min + exact.value, max + exact.value, approximate() || estimate.approximate());
                case Range range -> ranged(min + range.min, max + range.max, approximate() || estimate.approximate());
                case Unknown unknown -> UNKNOWN;
            };
        };

        @Override
        public NumberEstimate add(float value) {
            return ranged(min + value, max + value, approximate());
        };

        @Override
        public NumberEstimate multiply(NumberEstimate estimate) {
            return switch (estimate) {
                case Exact exact -> ranged(min * exact.value, max * exact.value, approximate() || estimate.approximate());
                case Range range -> ranged(min * range.min, max * range.max, approximate() || estimate.approximate());
                case Unknown unknown -> UNKNOWN;
            };
        };

        @Override
        public NumberEstimate multiply(float value) {
            return ranged(min * value, max * value, approximate());
        };
        
        @Override
        public NumberEstimate reciprocal() {
            return ranged(1f / max, 1f / min, approximate());
        };

        @Override
        public NumberEstimate negative() {
            return ranged(-max, -min, approximate());
        };

        @Override
        public NumberEstimate pow(float exponent) {
            float a = (float)Math.pow(min, exponent);
            float b = (float)Math.pow(max, exponent);
            return ranged(Math.min(a, b), Math.min(a, b), approximate());
        };

        @Override
        public NumberEstimate exp() {
            return ranged((float)Math.exp(min), (float)Math.exp(max), approximate());
        };

        @Override
        public NumberEstimate or(NumberEstimate estimate) {
            if (estimate instanceof Exact exact) {
                if (exact.value > max) {
                    return ranged(min, exact.value, approximate() || estimate.approximate());
                } else if (exact.value < min) {
                    return ranged(exact.value, max, approximate() || estimate.approximate());
                } else return this;
            } else if (estimate instanceof Range range) {
                return ranged(Math.min(min, range.min), Math.max(max, range.max), approximate() || estimate.approximate());
            } else return UNKNOWN;
        };

        @Override
        public IntStream streamPossibleInts() {
            if (min == Float.NaN || max == Float.NaN) return IntStream.empty();
            return IntStream.range((int)min(), (int)max());
        };

        @Override
        protected Neither<Exact, Range> wrap() {
            return Neither.right(this);
        };
    };

    public static final class Unknown extends NumberEstimate {

        public Unknown() {
            super(true);
        };

        @Override
        public MutableComponent getComponent(DecimalFormat df) {
            return Lang.unknownRange();
        };

        @Override
        public float min() {
            return -Float.NaN;
        };

        public float max() {
            return Float.NaN;
        };

        @Override
        public NumberEstimate add(NumberEstimate estimate) {
            return UNKNOWN;
        };

        @Override
        public NumberEstimate add(float value) {
            return UNKNOWN;
        };

        @Override
        public NumberEstimate multiply(NumberEstimate estimate) {
            return UNKNOWN;
        };

        @Override
        public NumberEstimate multiply(float value) {
            return UNKNOWN;
        };

        @Override
        public NumberEstimate reciprocal() {
            return UNKNOWN;
        };

        @Override
        public NumberEstimate negative() {
            return UNKNOWN;
        };

        @Override
        public NumberEstimate pow(float exponent) {
            return UNKNOWN;
        };

        public NumberEstimate exp() {
            return UNKNOWN;
        };

        @Override
        public NumberEstimate or(NumberEstimate estimate) {
            return UNKNOWN;
        };

        @Override
        public IntStream streamPossibleInts() {
            return IntStream.empty();
        };

        @Override
        protected Neither<Exact, Range> wrap() {
            return Neither.neither();
        };

    };
};
