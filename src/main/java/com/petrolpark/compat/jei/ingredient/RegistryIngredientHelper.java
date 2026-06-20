package com.petrolpark.compat.jei.ingredient;

import java.util.stream.Stream;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.Nullable;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

@ParametersAreNonnullByDefault
public class RegistryIngredientHelper<T> implements IIngredientHelper<T> {

    public final IIngredientType<T> ingredientType;
    public final ResourceKey<Registry<T>> registryKey;
    public final String langKey;

    private final Minecraft mc = Minecraft.getInstance();
    @SuppressWarnings("null") private final RegistryAccess registryAccess = mc.level.registryAccess();

    public RegistryIngredientHelper(IIngredientType<T> ingredientType, ResourceKey<Registry<T>> registryKey) {
        this(ingredientType, registryKey, registryKey.location().getPath());
    };

    public RegistryIngredientHelper(IIngredientType<T> ingredientType, ResourceKey<Registry<T>> registryKey, String langKey) {
        this.ingredientType = ingredientType;
        this.registryKey = registryKey;
        this.langKey = langKey;
    };

    @Override
    public IIngredientType<T> getIngredientType() {
        return ingredientType;
    };

    public Registry<T> getRegistry() {
        return registryAccess.registryOrThrow(registryKey);
    };

    @Override
    public String getDisplayName(T ingredient) {
        return getDisplayNameComponent(ingredient).getString();
    };

    public Component getDisplayNameComponent(T ingredient) {
        final ResourceLocation rl = getResourceLocation(ingredient);
        if (rl == null) return Component.literal("[unregistered]");
        return Component.translatable(rl.toLanguageKey(langKey));
    };

    @Override
    public String getUniqueId(T ingredient, UidContext context) {
        final ResourceLocation rl = getResourceLocation(ingredient);
        if (rl == null) return "unknown";
        return rl.toString();
    };

    @Override
    public ResourceLocation getResourceLocation(T ingredient) {
        return getRegistry().getKey(ingredient);
    };

    @Override
    public T copyIngredient(T ingredient) {
        return ingredient;
    };

    @Override
    public String getErrorInfo(@Nullable T ingredient) {
        return "";
    };

    @Override
    public String getDisplayModId(T ingredient) {
        final ResourceLocation rl = getResourceLocation(ingredient);
        if (rl == null) return "unknown";
        return rl.getNamespace();
    };

    public Holder<T> wrapAsHolder(T ingredient) {
        return getRegistry().wrapAsHolder(ingredient);
    };

    public Stream<T> streamAll() {
        return getRegistry().stream();
    };
    
};
