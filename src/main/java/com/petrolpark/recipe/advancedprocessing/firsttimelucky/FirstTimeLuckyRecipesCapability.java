package com.petrolpark.recipe.advancedprocessing.firsttimelucky;

import java.util.HashSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class FirstTimeLuckyRecipesCapability extends HashSet<ResourceLocation> {

    public void copyFrom(FirstTimeLuckyRecipesCapability plfr) {
        clear();
        addAll(plfr);
    };

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

        public static Capability<FirstTimeLuckyRecipesCapability> PLAYER_LUCKY_FIRST_RECIPES = CapabilityManager.get(new CapabilityToken<FirstTimeLuckyRecipesCapability>() {});

        private FirstTimeLuckyRecipesCapability playerRecipes = null;
        private final LazyOptional<FirstTimeLuckyRecipesCapability> lazyOp = LazyOptional.of(this::getPlayerLuckyFirstRecipes);

        public FirstTimeLuckyRecipesCapability getPlayerLuckyFirstRecipes() {
            if (playerRecipes == null) playerRecipes = new FirstTimeLuckyRecipesCapability();
            return playerRecipes;
        };

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.put("DoneRecipes", getPlayerLuckyFirstRecipes().stream().map(ResourceLocation::toString).map(StringTag::valueOf).collect(ListTag::new, ListTag::add, ListTag::addAll));
            return tag;
        };

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            getPlayerLuckyFirstRecipes().clear();
            getPlayerLuckyFirstRecipes().addAll(nbt.getList("DoneRecipes", Tag.TAG_STRING).stream().map(Tag::getAsString).map(ResourceLocation::new).toList());
        };

        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
           if (cap == PLAYER_LUCKY_FIRST_RECIPES) return lazyOp.cast();
           return LazyOptional.empty();
        };

    };
};
