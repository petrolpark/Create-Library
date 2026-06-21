package petrolpark.mc.library.compat.jei.ingredient;

import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import petrolpark.mc.library.compat.jei.ingredient.HolderIngredientHelper.HolderHolder;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.library.ingredients.TypedIngredient;
import mezz.jei.library.plugins.jei.tags.ITagInfoRecipe;
import mezz.jei.library.plugins.jei.tags.TagInfoRecipe;
import mezz.jei.library.plugins.jei.tags.TagInfoRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * JEI sorts Ingredient Types based on the Class of ingredient. Seeing as we want to preserve the Holder that wraps data-registered objects,
 * but generic types are lost at runtime, we need to wrap the Holder, which is why we need this HolderHolder nonsense.
 */
@ParametersAreNonnullByDefault
public class HolderIngredientHelper<T, H extends HolderHolder<T>> implements IIngredientHelper<H> {

    public final IIngredientType<H> ingredientType;
    public final ResourceKey<Registry<T>> registryKey;
    public final Function<Holder<T>, H> holderHolderer;
    public final String langKey;

    public final RecipeType<ITagInfoRecipe> tagInfoRecipeType;

    private final Minecraft mc = Minecraft.getInstance();
    @SuppressWarnings("null") private final RegistryAccess registryAccess = mc.level.registryAccess();

    public HolderIngredientHelper(IIngredientType<H> ingredientType, ResourceKey<Registry<T>> registryKey, Function<Holder<T>, H> holderHolderer) {
        this(ingredientType, registryKey, holderHolderer, registryKey.location().getPath());
    };

    public HolderIngredientHelper(IIngredientType<H> ingredientType, ResourceKey<Registry<T>> registryKey, Function<Holder<T>, H> holderHolderer, String langKey) {
        this.ingredientType = ingredientType;
        this.registryKey = registryKey;
        this.holderHolderer = holderHolderer;
        this.langKey = langKey;

        this.tagInfoRecipeType = RecipeType.create(registryKey.location().getNamespace(), "tag_recipes/" + registryKey.location().getPath(), ITagInfoRecipe.class);
    };

    @Override
    public IIngredientType<H> getIngredientType() {
        return ingredientType;
    };

    public Registry<T> getRegistry() {
        return registryAccess.registryOrThrow(registryKey);
    };

    @Override
    public String getDisplayName(H ingredient) {
        return getDisplayNameComponent(ingredient).getString();
    };

    public Component getDisplayNameComponent(H ingredient) {
        final ResourceLocation rl = getResourceLocation(ingredient);
        if (rl == null) return Component.literal("[unregistered]");
        return Component.translatable(rl.toLanguageKey(langKey));
    };

    @Override
    public String getUniqueId(H ingredient, UidContext context) {
        final ResourceLocation rl = getResourceLocation(ingredient);
        if (rl == null) return "unknown";
        return rl.toString();
    };

    @Override
    public ResourceLocation getResourceLocation(H ingredient) {
        return ingredient.getKey().location();
    };

    @Override
    public H copyIngredient(H ingredient) {
        return ingredient;
    };

    @Override
    public String getErrorInfo(@Nullable H ingredient) {
        return "";
    };

    @Override
    public String getDisplayModId(H ingredient) {
        final ResourceLocation rl = getResourceLocation(ingredient);
        if (rl == null) return "unknown";
        return rl.getNamespace();
    };
    
    public Codec<H> codec() {
        return getRegistry().holderByNameCodec().xmap(holderHolderer, HolderHolder::holder);
    };

    public Stream<H> streamAll() {
        return getRegistry().holders().map(holderHolderer);
    };

    public interface HolderHolder<T> {

        public Holder<T> holder();

        public default T value() {
            return holder().value();
        };

        public default ResourceKey<T> getKey() {
            return holder().getKey();
        };
    };

    public void registerTagInfoCategory(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new TagInfoRecipeCategory<>(registration.getJeiHelpers().getGuiHelper(), tagInfoRecipeType, registryKey.location()));
    };

    public void addTagRecipes(IRecipeRegistration registration) {
        registration.addRecipes(
            tagInfoRecipeType,
            getRegistry().getTagNames()
                .map(tag ->
                    (ITagInfoRecipe)new TagInfoRecipe<>(tag, StreamSupport.stream(getRegistry().getTagOrEmpty(tag).spliterator(), false)
                        .map(holder -> TypedIngredient.createAndFilterInvalid(registration.getIngredientManager(), ingredientType, holderHolderer.apply(holder), false))
                        .toList()
                    )
                ).filter(recipe -> !recipe.getTypedIngredients().isEmpty())
                .toList()
        );
    };
    
};
