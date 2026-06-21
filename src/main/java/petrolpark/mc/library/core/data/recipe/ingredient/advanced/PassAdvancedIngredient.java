package petrolpark.mc.library.core.data.recipe.ingredient.advanced;

import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class PassAdvancedIngredient implements IAdvancedIngredient<Object> {

    public static final PassAdvancedIngredient INSTANCE = new PassAdvancedIngredient();
    public static final MapCodec<PassAdvancedIngredient> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<ByteBuf, PassAdvancedIngredient> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final IAdvancedIngredientType<Object> TYPE = new AdvancedIngredientGenericType<>(Petrolpark.translationKey("advancedIngredient.pass"), CODEC, STREAM_CODEC);

    private PassAdvancedIngredient() {};

    @Override
    public boolean test(Object stack) {
        return true;
    };

    @Override
    public Stream<Object> modifyExamples(Stream<Object> exampleStacks) {
        return exampleStacks;
    };

    @Override
    public Stream<Object> modifyCounterExamples(Stream<Object> counterExampleStacks) {
        return Stream.empty();
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder description) {};

    @Override
    public void addToCounterDescription(IndentedTooltipBuilder description) {};

    @Override
    public IAdvancedIngredientType<? super Object> getType() {
        return TYPE;
    };
    
};
