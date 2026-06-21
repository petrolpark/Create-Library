package petrolpark.mc.library.shared.world.item.crafting.ageing;

import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.core.world.item.decay.DecayTime;
import petrolpark.mc.library.core.world.item.decay.IApplyDecayRecipe;
import petrolpark.mc.library.core.world.item.decay.product.IDecayProduct;
import petrolpark.mc.library.shared.registry.SharedRecipeSerializers;
import petrolpark.mc.library.shared.registry.SharedRecipeTypes;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public record AgeingRecipe(Ingredient ingredient, IDecayProduct decayProduct, DecayTime decayTime) implements IApplyDecayRecipe {

    public static final MapCodec<AgeingRecipe> CODEC = IApplyDecayRecipe.codec(AgeingRecipe::new);

    public static final StreamCodec<RegistryFriendlyByteBuf, AgeingRecipe> STREAM_CODEC = IApplyDecayRecipe.streamCodec(AgeingRecipe::new);

    @Override
    public RecipeSerializer<AgeingRecipe> getSerializer() {
        return SharedRecipeSerializers.AGEING.get();
    };

    @Override
    public RecipeType<AgeingRecipe> getType() {
        return SharedRecipeTypes.AGEING.get();
    };
};
