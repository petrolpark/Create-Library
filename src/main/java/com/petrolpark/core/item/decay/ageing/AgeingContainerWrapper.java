package com.petrolpark.core.item.decay.ageing;

import javax.annotation.Nonnull;

import com.petrolpark.PetrolparkConfig;
import com.petrolpark.PetrolparkRecipeTypes;
import com.petrolpark.core.item.decay.ItemDecay;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;

public interface AgeingContainerWrapper extends Container {

    public static boolean ageingInVanillaBarrelsEnabled() {
        return PetrolparkConfig.SERVER.ageingInVanillaBarrels.getAsBoolean();
    };

    public static boolean isAgeingContainer(Container container) {
        if (container instanceof AgeingContainerWrapper) return true;
        return container instanceof BarrelBlockEntity && ageingInVanillaBarrelsEnabled();
    };

    public static ItemStack getItem(Level level, GetItem getItem, int slot) {
        return checkDecay(level, getItem.getItem(slot));
    };

    public static ItemStack removeItem(Level level, RemoveItem removeItem, int slot, int amount) {
        return withAgeingDecayRemoved(level, removeItem.removeItem(slot, amount));
    };

    public static ItemStack removeItemNoUpdate(Level level, RemoveItemNoUpdate removeItemNoUpdate, int slot) {
        return withAgeingDecayRemoved(level, removeItemNoUpdate.removeItemNoUpdate(slot));
    };

    public static void setItem(Level level, SetItem setItem, int slot, @Nonnull ItemStack stack) {
        setItem.setItem(slot, withAgeingDecay(level, ItemDecay.checkDecay(stack), true));
    };

    public static ItemStack withAgeingDecayRemoved(Level level, ItemStack stack) {
        level.getRecipeManager().getRecipesFor(PetrolparkRecipeTypes.AGEING.getType(), new AgeingRecipe.Input(stack), level).stream().findAny()
            .ifPresent(rh -> ItemDecay.removeAppliedDecay(stack));
        return checkDecay(level, stack);
    };

    public static ItemStack withAgeingDecay(Level level, ItemStack stack, boolean startDecay) {
        AgeingRecipe.Input input = new AgeingRecipe.Input(stack);
        return checkDecay(level, level.getRecipeManager().getRecipesFor(PetrolparkRecipeTypes.AGEING.getType(), input, level).stream().findAny()
            .map(RecipeHolder::value)
            .map(AgeingRecipe::cast)
            .map(recipe -> recipe.assemble(input, startDecay))
            .orElse(stack));
    };

    public static ItemStack checkDecay(Level level, ItemStack stack) {
        return ItemDecay.checkDecay(stack, s -> withAgeingDecay(level, s, false));
    };

    @FunctionalInterface
    public static interface GetItem {
        public ItemStack getItem(int slot);
    };

    @FunctionalInterface
    public static interface RemoveItem {
        public ItemStack removeItem(int slot, int amount);
    };

    @FunctionalInterface
    public static interface RemoveItemNoUpdate {
        public ItemStack removeItemNoUpdate(int slot);
    };

    @FunctionalInterface
    public static interface SetItem {
        public void setItem(int slot, @Nonnull ItemStack stack);
    };

    public Container getWrappedContainer();

    public Level getLevel();

    @Override
    public default void clearContent() {
        getWrappedContainer().clearContent();
    };

    @Override
    public default int getContainerSize() {
        return getWrappedContainer().getContainerSize();
    };

    @Override
    public default boolean isEmpty() {
        return getWrappedContainer().isEmpty();
    };

    @Override
    default ItemStack getItem(int slot) {
        return getItem(getLevel(), getWrappedContainer()::getItem, slot);
    };

    @Override
    public default ItemStack removeItem(int slot, int amount) {
        return removeItem(getLevel(), getWrappedContainer()::removeItem, slot, amount);
    };

    @Override
    public default ItemStack removeItemNoUpdate(int slot) {
        return removeItemNoUpdate(getLevel(), getWrappedContainer()::removeItemNoUpdate, slot);
    };

    @Override
    public default void setItem(int slot, @Nonnull ItemStack stack) {
        setItem(getLevel(), getWrappedContainer()::setItem, slot, stack);
    };
    
};
