package petrolpark.mc.library.shared.world.item.crafting.drying;

import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.world.item.decay.DecayTime;
import petrolpark.mc.library.core.world.item.decay.IApplyDecayRecipe;
import petrolpark.mc.library.core.world.item.decay.product.ChangeItemDecayProduct;
import petrolpark.mc.library.core.world.item.decay.product.IDecayProduct;
import petrolpark.mc.library.shared.registry.SharedRecipeSerializers;
import petrolpark.mc.library.shared.registry.SharedRecipeTypes;

import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

public record DryingRecipe(Ingredient ingredient, IDecayProduct decayProduct, DecayTime decayTime) implements IApplyDecayRecipe {

    public static final MapCodec<DryingRecipe> CODEC = IApplyDecayRecipe.codec(DryingRecipe::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, DryingRecipe> STREAM_CODEC = IApplyDecayRecipe.streamCodec(DryingRecipe::new);

    public static final String DRYING_TRANSLATION_KEY = Util.makeDescriptionId("item", Petrolpark.asResource("drying_item.remaining"));

    public static final DryingRecipe of(Ingredient ingredient, ItemLike result, int decayTime) {
        return new DryingRecipe(ingredient, ChangeItemDecayProduct.of(result), new DecayTime(DRYING_TRANSLATION_KEY, decayTime));
    };

    @Override
    public RecipeSerializer<DryingRecipe> getSerializer() {
        return SharedRecipeSerializers.DRYING.get();
    };

    @Override
    public RecipeType<DryingRecipe> getType() {
        return SharedRecipeTypes.DRYING.get();
    };

};
