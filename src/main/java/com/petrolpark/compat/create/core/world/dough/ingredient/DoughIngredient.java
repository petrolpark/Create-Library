package com.petrolpark.compat.create.core.world.dough.ingredient;

import java.util.function.Function;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.petrolpark.compat.create.core.world.dough.DoughData;
import com.petrolpark.compat.create.registry.PetrolparkCreateRegistries;
import com.petrolpark.core.data.recipe.ingredient.advanced.IAdvancedIngredient;
import com.petrolpark.core.data.recipe.ingredient.advanced.IAdvancedIngredientType;
import com.petrolpark.core.data.recipe.ingredient.advanced.INamedAdvancedIngredientType;
import com.petrolpark.core.data.recipe.ingredient.advanced.ItemAdvancedIngredient;
import com.petrolpark.core.data.recipe.ingredient.advanced.PassAdvancedIngredient;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface DoughIngredient extends IAdvancedIngredient<DoughData> {

    /**
     * Use {@link ItemAdvancedIngredient#CODEC instead}.
     */
    @ApiStatus.Internal
    static final Codec<IAdvancedIngredient<? super DoughData>> TYPED_CODEC = PetrolparkCreateRegistries.DOUGH_INGREDIENT_TYPES
        .byNameCodec()
        .dispatch(IAdvancedIngredient::getType, IAdvancedIngredientType::codec);

    public static final Codec<IAdvancedIngredient<? super DoughData>> CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, Codec.unit(PassAdvancedIngredient.INSTANCE)));

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
