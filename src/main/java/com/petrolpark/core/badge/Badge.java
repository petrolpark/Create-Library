package com.petrolpark.core.badge;

import java.util.Date;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.petrolpark.registry.PetrolparkRegistries;
import com.tterrag.registrate.util.entry.ItemEntry;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class Badge implements ItemLike {

    protected ResourceLocation id;
    protected ItemEntry<BadgeItem> itemEntry;

    protected Supplier<Ingredient> duplicationIngredient;

    public Badge() {
        duplicationIngredient = () -> Ingredient.EMPTY;
    };

    public void setDuplicationItem(Supplier<Ingredient> ingredient) {
        if (duplicationIngredient.get() != Ingredient.EMPTY) throw new UnsupportedOperationException("Cannot modify Badge's duplication Item");
        duplicationIngredient = ingredient;
    };

    @Nullable
    public Ingredient getDuplicationIngredient() {
        return duplicationIngredient.get(); 
    };

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public CraftingRecipe getExampleDuplicationRecipe() {
        Ingredient ingredient = getDuplicationIngredient();
        if (ingredient == null) return null;

        Minecraft minecraft = Minecraft.getInstance();
        ItemStack stack = BadgeItem.of(minecraft.player, this, new Date(System.currentTimeMillis()));

        return new ShapelessRecipe(
            getId().getNamespace() + "badge_duplication",
            CraftingBookCategory.MISC,
            stack,
            NonNullList.of(Ingredient.of(stack), ingredient)
        );
    };

    public void setId(ResourceLocation id) {
        if (this.id != null) throw new UnsupportedOperationException("Cannot change a Badge's ID");
        this.id = id;
    };

    public void setBadgeItem(ItemEntry<BadgeItem> entry) {
        if (itemEntry != null) throw new UnsupportedOperationException("Cannot reset a Badge's Item");
        itemEntry = entry;
    };

    public Component getName() {
        return Component.translatable(Util.makeDescriptionId("badge", getId()));
    };

    public Component getDescription() {
        return Component.translatable(Util.makeDescriptionId("badge", getId()) + ".description");
    };

    public ResourceLocation getId() {
        return id;
    };

    @Override
    public BadgeItem asItem() {
        return itemEntry.get();
    };

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Badge badge && getId() != null && badge.getId() == getId();
    };

    @Nullable
    public static Badge getBadge(String namespace, String name) {
        return getBadge(ResourceLocation.fromNamespaceAndPath(namespace, name));
    };
    
    @Nullable
    public static Badge getBadge(ResourceLocation id) {
        return PetrolparkRegistries.BADGES.get(id);
    };

};
