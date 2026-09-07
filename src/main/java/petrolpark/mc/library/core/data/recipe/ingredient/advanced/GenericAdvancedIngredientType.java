package petrolpark.mc.library.core.data.recipe.ingredient.advanced;

import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

public class GenericAdvancedIngredientType<STACK, TYPELESS_INGREDIENT extends ITypelessAdvancedIngredient<STACK>> implements IAdvancedIngredientType<STACK> {

    private final MapCodec<? extends IAdvancedIngredient<STACK>> codec;
    private final StreamCodec<? super RegistryFriendlyByteBuf, ? extends IAdvancedIngredient<STACK>> streamCodec;

    public GenericAdvancedIngredientType(MapCodec<TYPELESS_INGREDIENT> untypedCodec, StreamCodec<? super RegistryFriendlyByteBuf, TYPELESS_INGREDIENT> untypedStreamCodec) {
        codec = untypedCodec.xmap(untyped -> new TypeAttachedAdvancedIngredient<>(untyped, this), TypeAttachedAdvancedIngredient::untypedIngredient);
        streamCodec = untypedStreamCodec.map(untyped -> new TypeAttachedAdvancedIngredient<>(untyped, this), TypeAttachedAdvancedIngredient::untypedIngredient);
    };

    @Override
    public final MapCodec<? extends IAdvancedIngredient<STACK>> codec() {
        return codec;
    };

    @Override
    public final StreamCodec<? super RegistryFriendlyByteBuf, ? extends IAdvancedIngredient<STACK>> streamCodec() {
        return streamCodec;
    };

    public Stream<TYPELESS_INGREDIENT> streamApplicableTypelessIngredients(Level level, STACK stack) {
        return Stream.empty();
    };

    @Override
    public Stream<TypeAttachedAdvancedIngredient<STACK, TYPELESS_INGREDIENT>> streamApplicableIngredients(Level level, STACK stack) {
        return streamApplicableTypelessIngredients(level, stack).map(this::create);
    };

    public final TypeAttachedAdvancedIngredient<STACK, TYPELESS_INGREDIENT> create(TYPELESS_INGREDIENT ingredient) {
        return new TypeAttachedAdvancedIngredient<>(ingredient, this);
    };
    
};
