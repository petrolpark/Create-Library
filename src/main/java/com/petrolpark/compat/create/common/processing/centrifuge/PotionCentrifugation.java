package com.petrolpark.compat.create.common.processing.centrifuge;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.mojang.datafixers.util.Either;
import com.petrolpark.core.recipe.recycling.RecyclingManager;
import com.petrolpark.core.recipe.recycling.RecyclingOutputs;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllFluids;
import com.simibubi.create.content.fluids.potion.PotionFluid.BottleType;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.DataComponentFluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class PotionCentrifugation {

    protected static final Map<BottleType, Map<Holder<Potion>, PotionCentrifugationRecipe>> RECIPES = new HashMap<>();

    public static final void createAllRecipes(PotionBrewing brewing) {
        RECIPES.clear();
        BuiltInRegistries.POTION.holders().forEach(holder -> {
            final PotionContents potionContents = new PotionContents(holder);
            final FluidStack splashFluid = PotionFluidHandler.getFluidFromPotion(potionContents, BottleType.SPLASH, 1000);
            get(BottleType.LINGERING).put(holder, create(
                NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.GLASS_BOTTLE)),
                PotionFluidHandler.getFluidFromPotion(potionContents, BottleType.LINGERING, 1000),
                splashFluid,
                Items.DRAGON_BREATH
            ));
            get(BottleType.SPLASH).put(holder, create(
                splashFluid,
                PotionFluidHandler.getFluidFromPotion(potionContents, BottleType.REGULAR, 1000),
                Items.GUNPOWDER
            ));
        });
        brewing.potionMixes.forEach(mix -> 
            get(BottleType.REGULAR).put(mix.to(), create(
                PotionFluidHandler.getFluidFromPotion(new PotionContents(mix.to()), BottleType.REGULAR, 1000),
                PotionFluidHandler.getFluidFromPotion(new PotionContents(mix.from()), BottleType.REGULAR, 1000),
                RecyclingManager.getInverse(mix.ingredient())
            ))
        );
    };

    protected static final PotionCentrifugationRecipe create(NonNullList<Ingredient> ingredients, FluidStack from, FluidStack to, Item item) {
        return new PotionCentrifugationRecipe(ingredients, new SizedFluidIngredient(DataComponentFluidIngredient.of(false, from), 1000), Either.left(new ItemStack(item)), to);
    };

    protected static final PotionCentrifugationRecipe create(FluidStack from, FluidStack to, Item item) {
        return new PotionCentrifugationRecipe(NonNullList.create(), new SizedFluidIngredient(DataComponentFluidIngredient.of(false, from), 1000), Either.left(new ItemStack(item)), to);
    };

    protected static final PotionCentrifugationRecipe create(FluidStack from, FluidStack to, RecyclingOutputs outputs) {
        return new PotionCentrifugationRecipe(NonNullList.create(), new SizedFluidIngredient(DataComponentFluidIngredient.of(false, from), 1000), Either.right(outputs), to);
    };

    protected static final Map<Holder<Potion>, PotionCentrifugationRecipe> get(BottleType bottleType) {
        return RECIPES.computeIfAbsent(bottleType, $ -> new HashMap<>());
    };

    protected static final Optional<PotionCentrifugationRecipe> get(BottleType bottleType, Holder<Potion> potion) {
        return Optional.ofNullable(get(bottleType).get(potion));
    };
    
    @SubscribeEvent
    public static final void onCentrifugation(CentrifugationEvent event) {
        //TODO config
        final FluidStack input = event.getCentrifuge().getInputStack();
        if (AllFluids.POTION.is(input)) {
            final BottleType bottleType = input.get(AllDataComponents.POTION_FLUID_BOTTLE_TYPE);
            final PotionContents potionContents = input.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            if (bottleType != null && potionContents.potion().isPresent() && potionContents.customColor().isEmpty() && potionContents.customEffects().isEmpty())
                get(bottleType, potionContents.potion().get()).ifPresent(event::addRecipe);
        };
        
    };

    public static record PotionCentrifugationRecipe(NonNullList<Ingredient> ingredients, SizedFluidIngredient fluidIngredient, Either<ItemStack, RecyclingOutputs> result, FluidStack lightOutput) implements Recipe<RecipeInput>, ICentrifugationRecipe {

        @Override
        public NonNullList<Ingredient> getIngredients() {
            return ingredients();
        };

        @Override
        public NonNullList<SizedFluidIngredient> getFluidIngredients() {
            return NonNullList.of(fluidIngredient(), fluidIngredient());
        };

        @Override
        public int getProcessingDuration() {
            return 200; //TODO config
        };

        @Override
        public List<ItemStack> getRollableResultsAsItemStacks() {
            return result().map(Collections::singletonList, RecyclingOutputs::getAllPossibleStacks);
        };

        @Override
        public List<ItemStack> rollLuckyResults(SmartBlockEntity blockEntity, RandomSource random) {
            return result().map(Collections::singletonList, outputs -> outputs.rollStacks(random));
        };

        @Override
        public NonNullList<ItemStack> getRemainingItems(@Nonnull RecipeInput input) {
            return NonNullList.create();
        };

        @Override
        public FluidStack getDenseOutputFluid() {
            return FluidStack.EMPTY;
        };

        @Override
        public FluidStack getLightOutputFluid() {
            return lightOutput();
        }

        @Override
        public boolean matches(@Nonnull RecipeInput input, @Nonnull Level level) {
            return false;
        };

        @Override
        public ItemStack assemble(@Nonnull RecipeInput input, @Nonnull HolderLookup.Provider registries) {
            return getResultItem(registries);
        };

        @Override
        public boolean canCraftInDimensions(int width, int height) {
            return false;
        };

        @Override
        public ItemStack getResultItem(@Nonnull HolderLookup.Provider registries) {
            return result().map(Function.identity(), outputs -> outputs.getFirst().getItem());
        };

        @Override
        public RecipeSerializer<?> getSerializer() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'getSerializer'");
        };

        @Override
        public RecipeType<?> getType() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'getType'");
        };

    };
};
