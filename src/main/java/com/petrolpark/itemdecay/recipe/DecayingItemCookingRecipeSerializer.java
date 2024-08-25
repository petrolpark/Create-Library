package com.petrolpark.itemdecay.recipe;

import java.util.Optional;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.petrolpark.itemdecay.recipe.IDecayingItemCookingRecipe.DecayingItemBlastingRecipe;
import com.petrolpark.itemdecay.recipe.IDecayingItemCookingRecipe.DecayingItemSmeltingRecipe;
import com.petrolpark.itemdecay.recipe.IDecayingItemCookingRecipe.DecayingItemSmokingRecipe;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.ForgeRegistries;

public class DecayingItemCookingRecipeSerializer implements RecipeSerializer<IDecayingItemCookingRecipe<?>> {
    
    @Override
    public IDecayingItemCookingRecipe<?> fromJson(ResourceLocation recipeId, JsonObject json) {
        return fromJsonInternal(recipeId, json);
    };

    protected <R extends AbstractCookingRecipe> IDecayingItemCookingRecipe<R> fromJsonInternal(ResourceLocation recipeId, JsonObject json) {
        RecipeSerializer<R> serializer = getWrappedSerializer(new ResourceLocation(GsonHelper.getAsString(json, "serializer")));
        return getWrapped(serializer.fromJson(recipeId, json));
    };

    @Override
    public @Nullable IDecayingItemCookingRecipe<?> fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        return fromNetworkInternal(recipeId, buffer);
    };

    protected <R extends AbstractCookingRecipe> IDecayingItemCookingRecipe<R> fromNetworkInternal(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        RecipeSerializer<R> serializer = getWrappedSerializer(buffer.readResourceLocation());
        return getWrapped(serializer.fromNetwork(recipeId, buffer));
    };

    @Override
    public void toNetwork(FriendlyByteBuf buffer, IDecayingItemCookingRecipe<?> recipe) {
        toNetworkInternal(buffer, recipe);
    };

    protected <R extends AbstractCookingRecipe> void toNetworkInternal(FriendlyByteBuf buffer, IDecayingItemCookingRecipe<R> recipe) {
        buffer.writeRegistryId(ForgeRegistries.RECIPE_SERIALIZERS, recipe.getWrappedSerializer());
        recipe.getWrappedSerializer().toNetwork(buffer, recipe.getAsWrappedRecipe());
    };

    @SuppressWarnings("unchecked")
    protected <R extends AbstractCookingRecipe> RecipeSerializer<R> getWrappedSerializer(ResourceLocation id) {
        try {
            return (RecipeSerializer<R>)Optional.ofNullable(ForgeRegistries.RECIPE_SERIALIZERS.getValue(id)).orElseThrow(() -> new JsonSyntaxException("Invalid or unsupported recipe serializer '" + id.toString() + "'"));
        } catch (ClassCastException e) {
            throw new JsonSyntaxException("Recipe serializer '" + id.toString() + "' does not give a cooking recipe.");
        }
    };

    @SuppressWarnings("unchecked")
    public <R extends AbstractCookingRecipe> IDecayingItemCookingRecipe<R> getWrapped(R recipe) {
        if (recipe.getType() == RecipeType.SMELTING) return (IDecayingItemCookingRecipe<R>)new DecayingItemSmeltingRecipe(recipe);
        if (recipe.getType() == RecipeType.BLASTING) return (IDecayingItemCookingRecipe<R>)new DecayingItemBlastingRecipe(recipe);
        if (recipe.getType() == RecipeType.SMOKING) return (IDecayingItemCookingRecipe<R>)new DecayingItemSmokingRecipe(recipe);
        throw new IllegalArgumentException("Recipe '"+recipe.getType()+"' must be minecraft:smelting, minecraft:blasting or minecraft:smoking");
    };
};
