package petrolpark.mc.library.core.data.recipe.ingredient.advanced;

import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;

public class PassAdvancedIngredient<STACK> implements IAdvancedIngredient<STACK>, IAdvancedIngredientType<STACK> {

    protected final MapCodec<PassAdvancedIngredient<STACK>> codec;
    protected final Codec<PassAdvancedIngredient<STACK>> inlineCodec;
    protected final StreamCodec<ByteBuf, PassAdvancedIngredient<STACK>> streamCodec;

    public PassAdvancedIngredient() {
        codec = MapCodec.unit(this);
        inlineCodec = Codec.unit(this);
        streamCodec = StreamCodec.unit(this);
    };

    @Override
    public boolean test(Object stack) {
        return true;
    };

    @Override
    public Stream<STACK> modifyExamples(Stream<STACK> exampleStacks) {
        return exampleStacks;
    };

    @Override
    public Stream<STACK> modifyCounterExamples(Stream<STACK> counterExampleStacks) {
        return Stream.empty();
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder description) {};

    @Override
    public void addToCounterDescription(IndentedTooltipBuilder description) {};

    @Override
    public IAdvancedIngredientType<STACK> getType() {
        return this;
    };

    @Override
    public MapCodec<PassAdvancedIngredient<STACK>> codec() {
        return codec;
    };

    public Codec<PassAdvancedIngredient<STACK>> inlineCodec() {
        return inlineCodec;
    };

    @Override
    public StreamCodec<ByteBuf, PassAdvancedIngredient<STACK>> streamCodec() {
        return streamCodec;
    };
    
};
