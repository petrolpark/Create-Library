package com.petrolpark.core.badge;

import java.util.Collection;
import java.util.function.Supplier;

import com.petrolpark.compat.curios.PetrolparkCuriosSetup;
import com.petrolpark.core.registrate.AbstractPetrolparkRegistrate;
import com.petrolpark.registry.PetrolparkRegistries;
import com.tterrag.registrate.builders.AbstractBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonnullType;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;

public class BadgeRegistrateBuilder<T extends Badge, P> extends AbstractBuilder<Badge, T, P, BadgeRegistrateBuilder<T, P>> {
    
    private final NonNullSupplier<T> factory;

    protected ItemEntry<BadgeItem> item;
    protected Supplier<Ingredient> duplicationIngredient;

    public static <T extends Badge, P> BadgeRegistrateBuilder<T, P> create(AbstractPetrolparkRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullSupplier<T> factory) {
        return new BadgeRegistrateBuilder<>(owner, parent, name, callback, factory);
    };

    public BadgeRegistrateBuilder(AbstractPetrolparkRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullSupplier<T> factory) {
        super(owner, parent, name, callback, PetrolparkRegistries.Keys.BADGE);
        this.factory = factory;

        duplicationIngredient = () -> Ingredient.EMPTY;
        item = getOwner().item("badge/"+getName(), p -> new BadgeItem(p, () -> this.getEntry()))
            //.tab(null)
            .properties(p -> p
                .stacksTo(1)
            ).setData(ProviderType.LANG, (c, p) -> {})
            .setData(ProviderType.ITEM_MODEL, (c, p) -> {})
            .register();

        PetrolparkCuriosSetup.BADGES.add(item);
    };

    public BadgeRegistrateBuilder<T, P> duplicationIngredient(Supplier<Ingredient> ingredient) {
        duplicationIngredient = ingredient;
        return this;
    };

    @Override
    protected @NonnullType T createEntry() {
        T badge = factory.get();

        badge.setId(ResourceLocation.fromNamespaceAndPath(getOwner().getModid(), getName()));
        badge.setBadgeItem(item);
        badge.setDuplicationItem(duplicationIngredient);

        return badge;
    };

    public static Collection<CraftingRecipe> getExampleDuplicationRecipes() {
        return PetrolparkRegistries.BADGES.stream().map(Badge::getExampleDuplicationRecipe).filter(r -> r != null).toList();
    };
    
};
