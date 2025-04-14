package com.petrolpark;

import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.petrolpark.core.badge.BadgeDuplicationRecipe;
import com.petrolpark.core.contamination.recipe.CombineContaminatedItemsRecipe;
import com.petrolpark.core.item.decay.DecayingItemCookingRecipe;
import com.petrolpark.core.item.decay.ageing.AgeingRecipe;
import com.petrolpark.core.recipe.IPetrolparkRecipeTypes;
import com.petrolpark.core.recipe.manualonly.ManualOnlyShapedRecipe;
import com.petrolpark.core.recipe.recycling.DirectRecyclingRecipe;
import com.petrolpark.core.recipe.recycling.IRecyclingRecipe;
import com.petrolpark.core.recipe.recycling.IngredientRecyclingRecipe;
import com.petrolpark.util.Lang;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;

public enum PetrolparkRecipeTypes implements IPetrolparkRecipeTypes {

    AGEING(AgeingRecipe.class, AgeingRecipe.Serializer::new),

    RECYCLING(DirectRecyclingRecipe.class, IRecyclingRecipe.serializer(DirectRecyclingRecipe::new)),
    INGREDIENT_RECYCLING(IngredientRecyclingRecipe.class, IRecyclingRecipe.serializer(IngredientRecyclingRecipe::new)),

    DECAYING_ITEM_COOKING(() -> DecayingItemCookingRecipe.SERIALIZER, () -> null, false), //TODO remove null
    
    MANUAL_ONLY_CRAFTING_SHAPED(ManualOnlyShapedRecipe.Serializer::new, () -> RecipeType.CRAFTING),
    CONTAMINATED_ITEM_COMBINATION(() -> CombineContaminatedItemsRecipe.SERIALIZER, () -> RecipeType.CRAFTING),
    BADGE_DUPLICATION(() -> BadgeDuplicationRecipe.BADGE_DUPLICATION, () -> RecipeType.CRAFTING),
    ;

    private final ResourceLocation id;
    private final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> serializerObject;
    @Nullable
    private final DeferredHolder<RecipeType<?>, RecipeType<?>> typeObject;
    private final Supplier<RecipeType<?>> type;

    PetrolparkRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier, Supplier<RecipeType<?>> typeSupplier) {
        this(serializerSupplier, typeSupplier, false);
    };

    PetrolparkRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier, Supplier<RecipeType<?>> typeSupplier, boolean registerType) {
        String name = Lang.asId(name());
        id = Petrolpark.asResource(name);
        serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        if (registerType) {
            typeObject = Registers.TYPE_REGISTER.register(name, typeSupplier);
            type = typeObject;
        } else {
            typeObject = null;
            type = typeSupplier;
        };
    };

    @Override
    public ResourceLocation getId() {
        return id;
    };

    @SuppressWarnings("unchecked")
    public <S extends RecipeSerializer<?>> S getSerializer() {
        return (S) serializerObject.get();
    };

    @SuppressWarnings("unchecked")
    public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
        return (RecipeType<R>) type.get();
    };

    <I extends RecipeInput, R extends Recipe<I>> PetrolparkRecipeTypes(Class<R> recipeClass, Supplier<RecipeSerializer<R>> serializerSupplier) {
        String name = Lang.asId(name());
        id = Petrolpark.asResource(name);
        serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        typeObject = Registers.TYPE_REGISTER.register(name, () -> RecipeType.simple(id));
        type = typeObject;
    };

    public <I extends RecipeInput, R extends Recipe<I>> Optional<RecipeHolder<R>> find(I craftingInput, Level world) {
        return world.getRecipeManager().getRecipeFor(getType(), craftingInput, world);
    };

    public static final void register() {};
    
};
