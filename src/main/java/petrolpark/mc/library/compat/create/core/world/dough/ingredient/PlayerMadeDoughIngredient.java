package petrolpark.mc.library.compat.create.core.world.dough.ingredient;

import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.compat.create.core.world.dough.DoughData;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.AdvancedIngredientGenericType;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.INamedAdvancedIngredientType;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;

public final class PlayerMadeDoughIngredient implements DoughIngredient {

    public static final PlayerMadeDoughIngredient INSTANCE = new PlayerMadeDoughIngredient();
    public static final MapCodec<PlayerMadeDoughIngredient> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<ByteBuf, PlayerMadeDoughIngredient> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final AdvancedIngredientGenericType<DoughData> TYPE = new AdvancedIngredientGenericType<DoughData>(Petrolpark.translationKey("advancedIngredient.dough.player_made"), CODEC, STREAM_CODEC);

    private PlayerMadeDoughIngredient() {};

    @Override
    public void addToDescription(IndentedTooltipBuilder description) {
        description.add(translateSimple());
    };

    @Override
    public void addToCounterDescription(IndentedTooltipBuilder description) {
        description.add(translateInverse());
    };

    @Override
    public boolean test(DoughData t) {
        return t.madeByPlayer();
    };

    @Override
    public Stream<DoughData> modifyExamples(Stream<DoughData> exampleStacks) {
        return exampleStacks.map(data -> data.madeByPlayer(true));
    };

    @Override
    public Stream<DoughData> modifyCounterExamples(Stream<DoughData> counterExampleStacks) {
        return counterExampleStacks.map(data -> data.madeByPlayer(false));
    };

    @Override
    public INamedAdvancedIngredientType<DoughData> getType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getType'");
    };
    
};
