package petrolpark.mc.library.compat.create.shared.registry;

import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.compat.create.core.data.recipe.AdvancedProcessingRecipe;
import petrolpark.mc.library.compat.create.core.data.recipe.AdvancedProcessingRecipeParams;
import petrolpark.mc.library.compat.create.shared.content.processing.basinLid.LiddedBasinRecipe;
import petrolpark.mc.library.compat.create.shared.content.processing.blender.BlendingRecipe;
import petrolpark.mc.library.compat.create.shared.content.processing.centrifuge.CentrifugationRecipe;
import petrolpark.mc.library.compat.create.shared.content.processing.extrusion.ExtrusionRecipe;
import petrolpark.mc.library.compat.create.shared.content.processing.mandrel.MandrelRecipe;
import petrolpark.mc.library.compat.create.shared.content.processing.meshBasin.BoilingRecipe;
import petrolpark.mc.library.compat.create.shared.content.processing.meshBasin.FilteringRecipe;
import petrolpark.mc.library.compat.create.shared.content.processing.meshBasin.JuicingRecipe;
import petrolpark.mc.library.core.data.recipe.IPetrolparkRecipeTypes;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import petrolpark.mc.library.shared.world.item.crafting.SharedRecipeType;
import petrolpark.mc.library.util.Lang;

public enum SharedCreateRecipeTypes implements IPetrolparkRecipeTypes, IRecipeTypeInfo {

    BLENDING(SharedFeatureFlag.BLENDER, BlendingRecipe.Serializer::new),
    BOILING(SharedFeatureFlag.MESH_BASIN, BoilingRecipe.Serializer::new),
    CENTRIFUGATION(SharedFeatureFlag.CENTRIFUGE, CentrifugationRecipe::new),
    EXTRUSION(SharedFeatureFlag.EXTRUSION, ExtrusionRecipe.Serializer::new),
    FILTERING(SharedFeatureFlag.MESH_BASIN, FilteringRecipe.Serializer::new),
    JUICING(SharedFeatureFlag.MESH_BASIN, JuicingRecipe.Serializer::new),
    LIDDED_BASIN(SharedFeatureFlag.BASIN_LID, LiddedBasinRecipe.Serializer::new),
    MANDREL(SharedFeatureFlag.MANDREL, MandrelRecipe.Serializer::new),
    ;

    /**
     * The ResourceLocation of both the Serializer, and Type (if the Type is registered here).
     */
    private final ResourceLocation id;
    private final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> serializerObject;
    private final @Nullable DeferredHolder<RecipeType<?>, RecipeType<?>> typeObject;
    private final Supplier<RecipeType<?>> type;

    /**
     * Create the Serializer and Type similar to how Create does it, just with {@link AdvancedProcessingRecipe} Params and Serializers.
     * @param <R> Type of the Advanced Processing Recipe
     * @param processingFactory
     */
    <R extends AdvancedProcessingRecipe<?>> SharedCreateRecipeTypes(ProcessingRecipe.Factory<AdvancedProcessingRecipeParams, R> processingFactory) {
        this(() -> new AdvancedProcessingRecipe.Serializer<>(processingFactory));
    };

    /**
     * Create the Serializer and Type similar to how Create does it, just with {@link AdvancedProcessingRecipe} Params and Serializers.
     * @param <R> Type of the Advanced Processing Recipe
     * @param processingFactory
     */
    <R extends AdvancedProcessingRecipe<?>> SharedCreateRecipeTypes(SharedFeatureFlag featureFlag, ProcessingRecipe.Factory<AdvancedProcessingRecipeParams, R> processingFactory) {
        this(featureFlag, () -> new AdvancedProcessingRecipe.Serializer<>(processingFactory));
    };

    SharedCreateRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier) {
        String name = Lang.asId(name());
        id = Petrolpark.asResource(name);
        serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        typeObject = Registers.TYPE_REGISTER.register(name, () -> RecipeType.simple(id));
        type = typeObject;
    };

    SharedCreateRecipeTypes(SharedFeatureFlag featureFlag, Supplier<RecipeSerializer<?>> serializerSupplier) {
        String name = Lang.asId(name());
        id = Petrolpark.asResource(name);
        serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        typeObject = Registers.TYPE_REGISTER.register(name, () -> new SharedRecipeType<>(id, featureFlag));
        type = typeObject;
    };

    <R extends AdvancedProcessingRecipe<?>> SharedCreateRecipeTypes(ProcessingRecipe.Factory<AdvancedProcessingRecipeParams, R> processingFactory, Supplier<RecipeType<?>> typeSupplier) {
        this(() -> new AdvancedProcessingRecipe.Serializer<>(processingFactory), typeSupplier);
    };

    SharedCreateRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier, Supplier<RecipeType<?>> typeSupplier) {
        this(serializerSupplier, typeSupplier, false);
    };

    SharedCreateRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier, Supplier<RecipeType<?>> typeSupplier, boolean registerType) {
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

    /**
     * The ResourceLocation of both the Serializer, and Type (if the Type is registered here).
     */
    @Override
    public ResourceLocation getId() {
        return id;
    };

    @SuppressWarnings("unchecked")
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) serializerObject.get();
    };

    @Override
    @SuppressWarnings("unchecked")
    public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
        return (RecipeType<R>) type.get();
    };

    @SuppressWarnings("unchecked")
    public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType(Class<R> recipeClass) {
        return (RecipeType<R>) type.get();
    };

    public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> find(I inv, Level world) {
        return world.getRecipeManager().getRecipeFor(getType(), inv, world);
    };

    public static final void init() {};
    
};
