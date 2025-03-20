package com.petrolpark.compat.create.core.recipe;
// package com.petrolpark.recipe.advancedprocessing;

// import java.util.HashSet;
// import java.util.Set;

// import com.google.gson.JsonArray;
// import com.google.gson.JsonObject;
// import com.google.gson.JsonSyntaxException;
// import com.mojang.serialization.Codec;
// import com.petrolpark.Petrolpark;
// import com.petrolpark.RequiresCreate;
// import com.petrolpark.recipe.advancedprocessing.firsttimelucky.IFTLProcessingRecipe;
// import com.simibubi.create.AllRecipeTypes;
// import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
// import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeFactory;

// import net.minecraft.network.FriendlyByteBuf;
// import net.minecraft.network.RegistryFriendlyByteBuf;
// import net.minecraft.resources.ResourceLocation;

// import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;

// @RequiresCreate
// public class AdvancedProcessingRecipeSerializer<T extends ProcessingRecipe<?>> extends ProcessingRecipeSerializer<T> {

//     public final Codec<T> CODEC = AllRecipeTypes.CODEC.dispatchMap(ProcessingRecipe::getRecipeType, AllRecipeTypes::processingCodec)

//     public AdvancedProcessingRecipeSerializer(ProcessingRecipeFactory<T> factory) {
//         super(factory);
//     };

//     @Override
//     protected void writeToJson(JsonObject json, T recipe) {
//         super.writeToJson(json, recipe);

//         // Biome
//         if (recipe instanceof IBiomeSpecificProcessingRecipe biomeRecipe) {
//             Set<BiomeValue> biomes = biomeRecipe.getAllowedBiomes();
//             if (!biomes.isEmpty()) {
//                 JsonArray jsonArray = new JsonArray(biomes.size());
//                 biomes.forEach(biome -> jsonArray.add(biome.serialize()));
//                 json.add("biomes", jsonArray);
//             };
//         };

//         // Lucky first time
//         if (recipe instanceof IFTLProcessingRecipe luckyRecipe) {
//             json.addProperty("lucky_first_time", luckyRecipe.shouldBeLuckyFirstTime());
//         };
//     };

//     @Override
//     protected T readFromJson(ResourceLocation recipeId, JsonObject json) {
//         T recipe = super.readFromJson(recipeId, json);

//         // Biome
//         if (json.has("biomes") && json.get("biomes").isJsonArray()) {
//             if (recipe instanceof IBiomeSpecificProcessingRecipe biomeRecipe) {
//                 Set<BiomeValue> biomes = new HashSet<>();
//                 json.get("biomes").getAsJsonArray().forEach(e -> {
//                     if (!e.isJsonPrimitive()) throw new JsonSyntaxException("Biome list must contain only names of biomes");
//                     biomes.add(IBiomeSpecificProcessingRecipe.valueFromString(e.getAsString()));
//                 });
//                 biomeRecipe.setAllowedBiomes(biomes);
//             } else {
//                 Petrolpark.LOGGER.warn("Recipe "+recipeId+" specifies a biome filter but that is not supported by this recipe type.");
//             };
//         };

//         // Lucky first time
//         if (json.has("lucky_first_time")) {
//             if (recipe instanceof IFTLProcessingRecipe luckyRecipe) {
//                 boolean isLucky = false;
//                 try {
//                     isLucky = json.get("lucky_first_time").getAsBoolean();
//                 } catch(UnsupportedOperationException e) {
//                     throw new JsonSyntaxException("The field 'lucky_first_time' must be a single boolean");
//                 } catch(IllegalStateException e) {
//                     throw new JsonSyntaxException("The field 'lucky_first_time' must be a single boolean");
//                 } finally {
//                     luckyRecipe.setLuckyFirstTime(isLucky);
//                 };
//             } else {
//                 Petrolpark.LOGGER.warn("Recipe "+recipeId+" is marked as first-time-lucky but that is not supported by this recipe type.");
//             };
//         };

//         return recipe;
//     };

//     @Override
//     protected void writeToBuffer(RegistryFriendlyByteBuf buffer, T recipe) {
//         super.writeToBuffer(buffer, recipe);

//         // Biome
//         if (recipe instanceof IBiomeSpecificProcessingRecipe biomeRecipe) {
//             Set<BiomeValue> biomes = biomeRecipe.getAllowedBiomes();
//             buffer.writeVarInt(biomes.size());
//             biomes.forEach(biome -> buffer.writeUtf(biome.serialize()));
//         };

//         // Lucky first time
//         if (recipe instanceof IFTLProcessingRecipe luckyRecipe) {
//             buffer.writeBoolean(luckyRecipe.shouldBeLuckyFirstTime());
//         };
//     };

//     @Override
//     protected T readFromBuffer(ResourceLocation recipeId, FriendlyByteBuf buffer) {
//         T recipe = super.readFromBuffer(recipeId, buffer);

//         // Biome
//         if (recipe instanceof IBiomeSpecificProcessingRecipe biomeRecipe) {
//             int biomeCount = buffer.readVarInt();
//             Set<BiomeValue> biomes = new HashSet<>(biomeCount);
//             for (int i = 0; i < biomeCount; i++) {
//                 biomes.add(IBiomeSpecificProcessingRecipe.valueFromString(buffer.readUtf()));
//             };
//             biomeRecipe.setAllowedBiomes(biomes);
//         };

//         // Lucky first time
//         if (recipe instanceof IFTLProcessingRecipe luckyRecipe) {
//             luckyRecipe.setLuckyFirstTime(buffer.readBoolean());
//         };

//         return recipe;
//     };
    
// };
