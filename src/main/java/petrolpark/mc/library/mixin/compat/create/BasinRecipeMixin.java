package petrolpark.mc.library.mixin.compat.create;

import java.util.List;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.items.IItemHandler;
import petrolpark.mc.library.PetrolparkTags;
import petrolpark.mc.library.compat.create.core.data.recipe.firstTimeLucky.IFTLProcessingRecipe;
import petrolpark.mc.library.compat.create.core.world.block.entity.basin.IDifferentBasinBlockEntity;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateBlockEntityTypes;
import petrolpark.mc.library.config.PetrolparkConfigs;
import petrolpark.mc.library.core.flags.IFlagPole;
import petrolpark.mc.library.core.world.item.decay.ItemDecay;
import petrolpark.mc.library.mixin.compat.create.accessor.BasinBlockEntityAccessor;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import petrolpark.mc.library.shared.world.effect.CryingMobEffect;

@Mixin(BasinRecipe.class)
public class BasinRecipeMixin {

    @Inject(
        method = "Lcom/simibubi/create/content/processing/basin/BasinRecipe;apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;Z)Z",
        at = @At("HEAD"),
        cancellable = true
    )
    private static final void petrolpark$cancelIfWrongBasin(BasinBlockEntity basin, Recipe<?> recipe, boolean test, CallbackInfoReturnable<Boolean> cir) {
        if (basin instanceof IDifferentBasinBlockEntity differentBasin && !differentBasin.matchStaticFilters(recipe)) cir.setReturnValue(false);
    };
    
    /**
     * Start {@link ItemDecay} and propagate Flags in Basin Recipes.
     */
    @Inject(
        method = "Lcom/simibubi/create/content/processing/basin/BasinRecipe;apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;Z)Z",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;acceptOutputs(Ljava/util/List;Ljava/util/List;Z)Z",
            shift = Shift.BEFORE
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT,
        remap = false
    )
    private static final void petrolpark$propagateFlags(
        BasinBlockEntity basin, Recipe<?> recipe, boolean test, CallbackInfoReturnable<Boolean> cir,
        boolean isBasinRecipe, IItemHandler availableItems, IFluidHandler availableFluids, BlazeBurnerBlock.HeatLevel heat,
        List<ItemStack> recipeOutputItems, List<FluidStack> recipeOutputFluids,
        List<Ingredient> ingredients, List<FluidIngredient> fluidIngredients,
        boolean trueAndFalse[], int i1, int i2, boolean simulate,
        int extractedItemsFromSlot[], int extractedFluidsFromTank[],
        @Share("causeCrying") LocalBooleanRef causeCrying
    ) {
        if (simulate) {
            recipeOutputItems.forEach(ItemDecay::startDecay);

            if (PetrolparkConfigs.server().createBasinRecipesPropagateFlags.get()) {
                final ItemStack[] itemInputs = new ItemStack[availableItems.getSlots()];
                for (int slot = 0; slot < availableItems.getSlots(); slot++) {
                    final ItemStack stack = availableItems.getStackInSlot(slot).copyWithCount(extractedItemsFromSlot[slot]);
                    if (PetrolparkTags.Items.CUTTING_CAUSES_CRYING.matches(stack)) causeCrying.set(true);
                    itemInputs[slot] = stack;
                };
                final FluidStack[] fluidInputs = new FluidStack[availableFluids.getTanks()];
                for (int tank = 0; tank < availableFluids.getTanks(); tank++) {
                    final FluidStack stack = availableFluids.getFluidInTank(tank).copy();
                    if (!stack.isEmpty()) stack.setAmount(extractedFluidsFromTank[tank]);
                    fluidInputs[tank] = stack;
                };

                final Level level = basin.getLevel();
                if (level != null) IFlagPole.perpetuate(Stream.of(itemInputs), Stream.of(fluidInputs), PetrolparkConfigs.server().createFluidFlagWeight.get(), recipeOutputItems.stream(), recipeOutputFluids.stream());
            };
        };
    };

    /**
     * Replace normal Recipe Outputs with First-Time-Lucky outputs, if applicable.
     * @param basinRecipe
     * @param original
     * @param basin
     * @param recipe
     * @param test
     */
    @WrapOperation(
        method = "Lcom/simibubi/create/content/processing/basin/BasinRecipe;apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;Z)Z",
        at = @At(
            value = "INVOKE",
            target = "rollResults"
        ),
        remap = false
    )
    @SuppressWarnings("unchecked")
    private static final List<ItemStack> petrolpark$getLuckyResults(BasinRecipe basinRecipe, RandomSource random, Operation<List<ItemStack>> original, BasinBlockEntity basin, Recipe<?> recipe, boolean test) {
        if (basinRecipe instanceof IFTLProcessingRecipe ftlRecipe) return ftlRecipe.rollLuckyResults(basin, random);
        return original.call(basinRecipe, random);
    };

    @Inject(
        method = "Lcom/simibubi/create/content/processing/basin/BasinRecipe;apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;Z)Z",
        at = @At("TAIL")
    )
    private static final void petrolpark$causeCrying(BasinBlockEntity basin, Recipe<?> recipe, boolean test, CallbackInfoReturnable<Boolean> cir, @Share("causeCrying") LocalBooleanRef causeCrying) {
        if (
            SharedFeatureFlag.CRYING.enabled() &&
            SharedFeatureFlag.BLENDER.enabled() &&
            causeCrying.get() &&
            ((BasinBlockEntityAccessor)basin).callGetOperator()
                .filter(be -> be.getType() == SharedCreateBlockEntityTypes.BLENDER.get()).isPresent()
        )
            CryingMobEffect.applyInRange(basin.getLevel(), basin.getBlockPos());
    };
};
