package petrolpark.mc.library.compat.create.shared.content.processing.mandrel;

import javax.annotation.Nonnull;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import petrolpark.mc.library.compat.create.shared.content.processing.mandrel.animation.IMandrelAnimation;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateRecipeTypes;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public record MandrelRecipe(Ingredient ingredient, ItemStack result, IMandrelAnimation animation) implements Recipe<SingleRecipeInput> {

    public static final MapCodec<MandrelRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(MandrelRecipe::ingredient),
        ItemStack.CODEC.fieldOf("result").forGetter(MandrelRecipe::result),
        IMandrelAnimation.CODEC.fieldOf("animation").forGetter(MandrelRecipe::animation)
    ).apply(instance, MandrelRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MandrelRecipe> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, MandrelRecipe::ingredient,
        ItemStack.STREAM_CODEC, MandrelRecipe::result,
        IMandrelAnimation.STREAM_CODEC, MandrelRecipe::animation,
        MandrelRecipe::new
    );

    @Override
    public boolean matches(@Nonnull SingleRecipeInput input, @Nonnull Level level) {
        return ingredient().test(input.item());
    };

    @Override
    public ItemStack assemble(@Nonnull SingleRecipeInput input, @Nonnull HolderLookup.Provider registries) {
        return result();
    };

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    };

    @Override
    public ItemStack getResultItem(@Nonnull HolderLookup.Provider registries) {
        return result();
    };

    @Override
    public RecipeSerializer<MandrelRecipe> getSerializer() {
        return SharedCreateRecipeTypes.MANDREL.getSerializer();
    };

    @Override
    public RecipeType<MandrelRecipe> getType() {
        return SharedCreateRecipeTypes.MANDREL.getType();
    };

    public static class Serializer implements RecipeSerializer<MandrelRecipe> {

        @Override
        public MapCodec<MandrelRecipe> codec() {
            return CODEC;
        };

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MandrelRecipe> streamCodec() {
            return STREAM_CODEC;
        };

    };
    
};
