package com.petrolpark.recipe.ingredient.modifier;

import java.util.List;

import java.util.Optional;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.contamination.Contaminant;
import com.petrolpark.contamination.ItemContamination;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ContaminatedIngredientModifier implements IngredientModifier {

    public final ResourceLocation contaminantRL;
    protected Contaminant contaminant = null;

    public ContaminatedIngredientModifier(ResourceLocation contaminantRL) {
        this.contaminantRL = contaminantRL;
    };

    public Optional<Contaminant> getContaminant(Level level) {
        if (contaminant == null) contaminant = level.registryAccess().registryOrThrow(PetrolparkRegistries.Keys.CONTAMINANT).get(contaminantRL);
        if (contaminant == null) Petrolpark.LOGGER.warn("Unknown Contaminant: "+contaminantRL.toString());
        return Optional.ofNullable(contaminant);
    };

    @Override
    public boolean test(ItemStack stack, Level level) {
        return getContaminant(level).map(ItemContamination.get(stack)::has).orElse(false);
    };

    @Override
    public void modifyExamples(List<ItemStack> exampleStacks, Level level) {
        getContaminant(level).ifPresent(c -> {
            exampleStacks.stream().map(ItemContamination::get).forEach(co -> co.contaminate(contaminant));
        });
    };

    @Override
    public void modifyCounterExamples(List<ItemStack> counterExampleStacks, Level level) {
        getContaminant(level).ifPresent(c -> {
            counterExampleStacks.stream().map(ItemContamination::get).forEach(co -> co.decontaminateOnly(contaminant));
        });
    };

    @Override
    public void addToDescription(List<Component> description, Level level) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addToDescription'");
    };

    @Override
    public IngredientModifierType getType() {
        return IngredientModifierTypes.CONTAMINATED.get();
    };

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<ContaminatedIngredientModifier> {

        @Override
        public void serialize(JsonObject json, ContaminatedIngredientModifier value, JsonSerializationContext serializationContext) {
            json.addProperty("contaminant", value.contaminantRL.toString());
        };

        @Override
        public ContaminatedIngredientModifier deserialize(JsonObject json, JsonDeserializationContext deserializationContext) {
            return new ContaminatedIngredientModifier(ResourceLocation.fromNamespaceAndPath(GsonHelper.getAsString(json, "contaminant")));
        };

    };
    
};
