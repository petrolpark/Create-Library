package petrolpark.mc.library.compat.create.shared.content.processing.centrifuge;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.mojang.datafixers.util.Either;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateRecipeTypes;
import petrolpark.mc.library.compat.create.util.CreateRecyclingHelper;
import petrolpark.mc.library.config.PetrolparkConfigs;
import petrolpark.mc.library.core.world.item.recycling.RecyclingManager;
import petrolpark.mc.library.core.world.item.recycling.RecyclingOutputs;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllFluids;
import com.simibubi.create.content.fluids.potion.PotionFluid.BottleType;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.DataComponentFluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class PotionCentrifugation {

    protected static final Map<BottleType, Map<Holder<Potion>, PotionCentrifugationRecipe>> RECIPES = new HashMap<>();
    private static boolean initialized = false;

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
        //TODO recipes
        initialized = true;
    };

    public static final Stream<RecipeHolder<PotionCentrifugationRecipe>> streamAllRecipes(PotionBrewing brewing) {
        if (!initialized) createAllRecipes(brewing);
        id = 0;
        return RECIPES.values().stream().map(Map::values).flatMap(Collection::stream).map(r -> new RecipeHolder<>(Petrolpark.asResource("potion_separation_" + nextId()), r));
    };

    private static int id = 0;
    private static final int nextId() {
        return id++;
    };

    protected static final PotionCentrifugationRecipe create(NonNullList<Ingredient> ingredients, FluidStack from, FluidStack to, Item item) {
        return new PotionCentrifugationRecipe(ingredients, new SizedFluidIngredient(DataComponentFluidIngredient.of(false, from), 1000), Either.left(new ItemStack(item)), to);
    };

    protected static final PotionCentrifugationRecipe create(FluidStack from, FluidStack to, Item item) {
        return new PotionCentrifugationRecipe(NonNullList.create(), new SizedFluidIngredient(DataComponentFluidIngredient.of(false, from), 1000), Either.left(new ItemStack(item)), to);
    };

    protected static final PotionCentrifugationRecipe create(FluidStack from, FluidStack to, RecyclingOutputs outputs) {
        return new PotionCentrifugationRecipe(
            NonNullList.of(Ingredient.of(), outputs.getMaxPossibleStacks().stream()
                .map(ItemStack::getCraftingRemainingItem)
                .dropWhile(ItemStack::isEmpty)
                .map(Ingredient::of)
                .toArray(Ingredient[]::new)
            ),
            new SizedFluidIngredient(DataComponentFluidIngredient.of(false, from), 1000),
            Either.right(outputs),
            to
        );
    };

    protected static final Map<Holder<Potion>, PotionCentrifugationRecipe> get(BottleType bottleType) {
        return RECIPES.computeIfAbsent(bottleType, $ -> new HashMap<>());
    };

    protected static final Optional<PotionCentrifugationRecipe> get(BottleType bottleType, Holder<Potion> potion) {
        return Optional.ofNullable(get(bottleType).get(potion));
    };
    
    @SuppressWarnings("null")
    public static final void onCentrifugation(CentrifugationEvent event) {
        if (!PetrolparkConfigs.server().potionCentrifugation.get()) return;
        if (!initialized) createAllRecipes(event.getCentrifuge().getLevel().potionBrewing());
        final FluidStack input = event.getCentrifuge().getInputStack();
        if (input.getFluid().isSame(AllFluids.POTION.get())) {
            final BottleType bottleType = input.get(AllDataComponents.POTION_FLUID_BOTTLE_TYPE);
            final PotionContents potionContents = input.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            if (bottleType != null && potionContents.potion().isPresent() && potionContents.customColor().isEmpty() && potionContents.customEffects().isEmpty())
                get(bottleType, potionContents.potion().get()).ifPresent(event::addRecipe);
        };
        
    };

    public static record PotionCentrifugationRecipe(NonNullList<Ingredient> ingredients, SizedFluidIngredient fluidIngredient, Either<ItemStack, RecyclingOutputs> result, FluidStack denseOutput) implements Recipe<RecipeInput>, ICentrifugationRecipe {

        @Override
        public NonNullList<Ingredient> getCentrifugationIngredients() {
            return ingredients();
        };

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
            return 200;
        };

        @Override
        public List<ProcessingOutput> getRollableResults() {
            return result().map(stack -> Collections.singletonList(new ProcessingOutput(stack, 1f)), CreateRecyclingHelper::asProcessingOutputs);
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
            return denseOutput();
        };

        @Override
        public FluidStack getLightOutputFluid() {
            return FluidStack.EMPTY;
        };

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
            return SharedCreateRecipeTypes.CENTRIFUGATION.getType(); //TODO check if not dodgy
        };

    };
};
