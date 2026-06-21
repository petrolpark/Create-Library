package petrolpark.mc.library.core.world.item.decay;

import java.util.function.Function;

import javax.annotation.Nonnull;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.flags.recipe.IHandleFlagsMyselfRecipe;
import petrolpark.mc.library.util.codec.CodecHelper;

import io.netty.handler.codec.DecoderException;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;

public class DecayingItemCookingRecipe extends AbstractCookingRecipe implements IHandleFlagsMyselfRecipe<SingleRecipeInput> {

    public static final MapCodec<DecayingItemCookingRecipe> CODEC = CodecHelper.singleFieldMap(
        Recipe.CODEC.comapFlatMap(r -> r instanceof AbstractCookingRecipe cr ? DataResult.success(cr) : DataResult.error(() -> "Not a cooking recipe"), Function.identity()),
        "recipe",
        DecayingItemCookingRecipe::getWrappedRecipe,
        DecayingItemCookingRecipe::new
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, DecayingItemCookingRecipe> STREAM_CODEC = StreamCodec.composite(
        Recipe.STREAM_CODEC.map(r -> {
            if (r instanceof AbstractCookingRecipe cr) return cr;
            throw new DecoderException("Not a cooking recipe");
        }, Function.identity()), DecayingItemCookingRecipe::getWrappedRecipe,
        DecayingItemCookingRecipe::new
    );

    public static ItemStack withDecay(ItemStack stack) {
        return Petrolpark.runForDist(() -> () -> stack, () -> () -> {
            ItemStack copy = stack.copy();
            ItemDecay.startDecay(copy);
            return copy;
        });
    };

    protected final AbstractCookingRecipe wrappedRecipe;

    public DecayingItemCookingRecipe(AbstractCookingRecipe wrappedRecipe) {
        super(
            wrappedRecipe.getType(),
            wrappedRecipe.getGroup(),
            wrappedRecipe.category(),
            wrappedRecipe.getIngredients().get(0),
            wrappedRecipe.result,
            wrappedRecipe.getExperience(),
            wrappedRecipe.getCookingTime()
        );
        this.wrappedRecipe = wrappedRecipe;
    };

    public AbstractCookingRecipe getWrappedRecipe() {
        return wrappedRecipe;
    };

    @Override
    public ItemStack assemble(@Nonnull SingleRecipeInput input, @Nonnull HolderLookup.Provider registries) {
        return withDecay(super.assemble(input, registries));
    };

    @Override
    public ItemStack getResultItem(@Nonnull HolderLookup.Provider registries) {
        return withDecay(super.getResultItem(registries));
    };

    @Override
    public RecipeSerializer<? extends AbstractCookingRecipe> getSerializer() {
        return SERIALIZER;
    };

    public static final RecipeSerializer<DecayingItemCookingRecipe> SERIALIZER = new RecipeSerializer<DecayingItemCookingRecipe>() {

        @Override
        public MapCodec<DecayingItemCookingRecipe> codec() {
            return CODEC;
        };

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, DecayingItemCookingRecipe> streamCodec() {
            return STREAM_CODEC;
        };
        
    };
    
};
