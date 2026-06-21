package petrolpark.mc.library.core.world.item.crafting.recipeBook;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import petrolpark.mc.library.core.data.recipe.INamedRecipe;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

public record RecipeReferenceDataComponent(ResourceLocation recipeId, Optional<ResourceLocation> jeiRecipeTypeId) {

    public static final Codec<RecipeReferenceDataComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("recipe").forGetter(RecipeReferenceDataComponent::recipeId),
        ResourceLocation.CODEC.optionalFieldOf("jei_recipe_type").forGetter(RecipeReferenceDataComponent::jeiRecipeTypeId)
    ).apply(instance, RecipeReferenceDataComponent::new));

    public static final StreamCodec<FriendlyByteBuf, RecipeReferenceDataComponent> STREAM_CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC, RecipeReferenceDataComponent::recipeId,
        ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), RecipeReferenceDataComponent::jeiRecipeTypeId,
        RecipeReferenceDataComponent::new
    );

    public Optional<RecipeHolder<?>> getRecipeHolder(RecipeManager recipeManager) {
        return recipeManager.byKey(recipeId());
    };

    public Component getRecipeName(RecipeManager recipeManager) {
        return getRecipeHolder(recipeManager).map(INamedRecipe::getName).orElse(INamedRecipe.unknownRecipeName());
    };
};
