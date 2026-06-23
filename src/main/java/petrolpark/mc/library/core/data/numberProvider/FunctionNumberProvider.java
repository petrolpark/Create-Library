package petrolpark.mc.library.core.data.numberProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import petrolpark.mc.library.util.codec.CodecHelper;

public abstract class FunctionNumberProvider implements IEstimableNumberProvider {

    protected static final Map<LootNumberProviderType, FunctionNumberProvider.Factory<?>> FACTORIES = new HashMap<>();

    public static final void register(LootNumberProviderType type, FunctionNumberProvider.Factory<?> constructor) {
        FACTORIES.put(type, constructor);
    };

    public static final FunctionNumberProvider.Factory<?> get(LootNumberProviderType type) {
        return FACTORIES.get(type);
    };

    public static final DataResult<LootNumberProviderType> isFunction(LootNumberProviderType type) {
        return FACTORIES.containsKey(type) ? DataResult.success(type) : DataResult.error(() -> "Not a Function Loot Number Provider (min, max, mean, sum or product)");
    };

    public static final <PROVIDER extends FunctionNumberProvider> MapCodec<PROVIDER> codec(FunctionNumberProvider.Factory<PROVIDER> constructor) {
        return CodecHelper.singleFieldMap(NumberProviders.CODEC.listOf(), "values", FunctionNumberProvider::getChildren, constructor::create);
    };

    protected final List<NumberProvider> children;

    public FunctionNumberProvider(List<NumberProvider> children) {
        this.children = children;
    };

    public List<NumberProvider> getChildren() {
        return children;
    };

    @Override
    public final float getFloat(@Nonnull LootContext lootContext) {
        return applyFloat(lootContext, children.stream().mapToDouble(child -> child.getFloat(lootContext)));
    };

    @Override
    public final int getInt(@Nonnull LootContext lootContext) {
        return applyInt(lootContext, children.stream().mapToInt(child -> child.getInt(lootContext)));
    };

    @Override
    public final NumberEstimate getEstimate() {
        return applyEstimate(children.stream().map(NumberEstimate::get).dropWhile(NumberEstimate::unknown));
    };

    @Override
    public final float getMaxFloat(LootContext context) {
        return applyFloat(context, children.stream().mapToDouble(p -> NumberEstimate.getMax(context, p)));
    };

    public abstract float applyFloat(LootContext lootContext, DoubleStream childResults);

    public abstract int applyInt(LootContext lootContext, IntStream childResults);

    public abstract NumberEstimate applyEstimate(Stream<NumberEstimate> estimates);

    @FunctionalInterface
    public interface Factory<PROVIDER extends FunctionNumberProvider> {

        public PROVIDER create(List<NumberProvider> children);
    };
};
