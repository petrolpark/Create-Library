package petrolpark.mc.library.core.world.item.crafting.pocket.crafter;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.createmod.catnip.data.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.world.item.crafting.BookRequiredCraftingRecipe;
import petrolpark.mc.library.core.world.item.crafting.pocket.IPocketCraftingContext;
import petrolpark.mc.library.core.world.item.crafting.pocket.ItemsPocketCraftingResult;
import petrolpark.mc.library.core.world.item.crafting.pocket.PocketCrafting;
import petrolpark.mc.library.core.world.item.crafting.pocket.PocketCrafting.SlotGrid;
import petrolpark.mc.library.core.world.item.crafting.pocket.interpretedSlot.IInterpretedSlot;

@ParametersAreNonnullByDefault
public class CraftingPocketCrafter implements IPocketCrafter<CraftingRecipe> {

    public static final String TRANSLATION_KEY = Petrolpark.translationKey("pocketCrafting.crafter.crafting");

    @Override
    public MutableComponent getName() {
        return Component.translatable(TRANSLATION_KEY);
    };

    @Override
    public ItemStack getDefaultToolStack() {
        // TODO Auto-generated method stub
        return null;
    };

    @Override
    public boolean canCastRecipe(Recipe<?> recipe) {
        return recipe instanceof CraftingRecipe;
    };

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<RecipeHolder<? extends CraftingRecipe>> getRecipes(IPocketCraftingContext.Client context, List<IInterpretedSlot<?>> interpretedSlots, PocketCrafting.SlotArrangement slotArrangement) {
        final CraftingInput input = getCraftingInput(interpretedSlots, context.menu(), slotArrangement);
        return Stream.concat(
            context.recipeManager().getAllRecipesFor(RecipeType.CRAFTING).stream()
                .filter(rh -> rh.value().matches(input, context.level())),
            BookRequiredCraftingRecipe.streamMatching(context.level(), input, context.player().getRecipeBook(), context.player(), context.menu())
        ).toList();
    };

    @Override
    public PocketCrafting.Result craft(IPocketCraftingContext context, boolean simulate, RecipeHolder<? extends CraftingRecipe> recipeHolder, List<IInterpretedSlot<?>> inputSlots, @Nullable Slot outputSlot) {
        return ItemsPocketCraftingResult.success(recipeHolder.value().getResultItem(context.registries()));
    };

    public CraftingInput getCraftingInput(List<IInterpretedSlot<?>> interpretedSlots, AbstractContainerMenu menu, PocketCrafting.SlotArrangement slotArrangement) {
        final Pair<Int2ObjectMap<Int2ObjectMap<Slot>>, Int2ObjectMap<SlotGrid>> slotsAndGrids = PocketCrafting.organiseSlots(menu, interpretedSlots.stream().map(IInterpretedSlot::slot).toList(), slotArrangement);
        final Set<SlotGrid> uniqueGrids = new HashSet<>(slotsAndGrids.getSecond().values());

        // Try shaped crafting
        if (uniqueGrids.size() == 1) {
            final SlotGrid craftingSlotGrid = uniqueGrids.iterator().next();
            final List<ItemStack> stacks = craftingSlotGrid.streamSlots().map(Slot::getItem).toList();
            return CraftingInput.of(craftingSlotGrid.width(), stacks.size() / craftingSlotGrid.width(), stacks);
        } else {
            final List<ItemStack> stacks = slotsAndGrids.getFirst().values().stream()
                .map(Int2ObjectMap::values).flatMap(Collection::stream).map(Slot::getItem).toList();
            return new CraftingInput(stacks.size(), 1, stacks);
        }
    };
    
};
