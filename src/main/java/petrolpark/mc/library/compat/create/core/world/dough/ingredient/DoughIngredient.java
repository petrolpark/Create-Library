package petrolpark.mc.library.compat.create.core.world.dough.ingredient;

import java.util.function.Function;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import petrolpark.mc.library.compat.create.core.world.dough.DoughData;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreateRegistries;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredientType;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.INamedAdvancedIngredientType;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.ItemAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.PassAdvancedIngredient;

public interface DoughIngredient extends IAdvancedIngredient<DoughData> {

    /**
     * Use {@link ItemAdvancedIngredient#CODEC instead}.
     */
    @ApiStatus.Internal
    static final Codec<IAdvancedIngredient<? super DoughData>> TYPED_CODEC = PetrolparkCreateRegistries.DOUGH_INGREDIENT_TYPES
        .byNameCodec()
        .dispatch(IAdvancedIngredient::getType, IAdvancedIngredientType::codec);

    public static final Codec<IAdvancedIngredient<? super DoughData>> CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, Codec.unit(PassAdvancedIngredient.PASS)));

    public static final Codec<IAdvancedIngredient<DoughData>> STRICT_CODEC = CODEC.comapFlatMap(DoughIngredient::cast, Function.identity());

    public static final StreamCodec<RegistryFriendlyByteBuf, IAdvancedIngredient<? super DoughData>> STREAM_CODEC = ByteBufCodecs.registry(PetrolparkCreateRegistries.Keys.DOUGH_INGREDIENT_TYPE)
        .dispatch(IAdvancedIngredient::getType, IAdvancedIngredientType::streamCodec);

    @SuppressWarnings("unchecked")
    public static final StreamCodec<RegistryFriendlyByteBuf, IAdvancedIngredient<DoughData>> STRICT_STREAM_CODEC = DoughIngredient.STREAM_CODEC.map(ingredient -> (IAdvancedIngredient<DoughData>)ingredient, Function.identity());
    
    default Component translate(String postfix, Object... translationArgs) {
        return Component.translatable(getType().translationKey() + "." + postfix, translationArgs);
    };

    default Component translateSimple(Object... translationArgs) {
        return Component.translatable(getType().translationKey());
    };

    default Component translateInverse(Object... translationArgs) {
        return translate("inverse", translationArgs);
    };

    @Override
    public INamedAdvancedIngredientType<DoughData> getType();

    @SuppressWarnings("unchecked")
    public static DataResult<IAdvancedIngredient<DoughData>> cast(IAdvancedIngredient<? super DoughData> ingredient) {
        try {
            return DataResult.success((IAdvancedIngredient<DoughData>)ingredient);
        } catch (ClassCastException e) {
            return DataResult.error(() -> ingredient.toString() + " is not a direct Dough Ingredient");
        }
    };
};
