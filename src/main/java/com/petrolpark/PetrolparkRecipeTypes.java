package com.petrolpark;

import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.petrolpark.badge.BadgeDuplicationRecipe;
import com.petrolpark.item.decay.recipe.DecayingItemCookingRecipeSerializer;
import com.petrolpark.recipe.IPetrolparkRecipeTypes;
import com.petrolpark.recipe.contamination.CombineContaminatedItemsRecipe;
import com.petrolpark.recipe.manualonly.ManualOnlyShapedRecipe;
import com.petrolpark.util.Lang;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

public enum PetrolparkRecipeTypes implements IPetrolparkRecipeTypes {

    DECAYING_ITEM_COOKING(DecayingItemCookingRecipeSerializer::new),
    MANUAL_ONLY_CRAFTING_SHAPED(ManualOnlyShapedRecipe.Serializer::new, () -> RecipeType.CRAFTING),
    CONTAMINATED_ITEM_COMBINATION(() -> CombineContaminatedItemsRecipe.SERIALIZER, () -> RecipeType.CRAFTING),
    BADGE_DUPLICATION(() -> BadgeDuplicationRecipe.BADGE_DUPLICATION, () -> RecipeType.CRAFTING),
    ;

    private final ResourceLocation id;
    private final RegistryObject<RecipeSerializer<?>> serializerObject;
    @Nullable
    private final RegistryObject<RecipeType<?>> typeObject;
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
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) serializerObject.get();
    };

    @SuppressWarnings("unchecked")
    public <T extends RecipeType<?>> T getType() {
        return (T) type.get();
    };

    PetrolparkRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier) {
        String name = Lang.asId(name());
        id = Petrolpark.asResource(name);
        serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        typeObject = Registers.TYPE_REGISTER.register(name, () -> RecipeType.simple(id));
        type = typeObject;
    };

    public <C extends Container, T extends Recipe<C>> Optional<T> find(C inv, Level world) {
        return world.getRecipeManager()
            .getRecipeFor(getType(), inv, world);
    };

    public static final void register() {};
    
};
