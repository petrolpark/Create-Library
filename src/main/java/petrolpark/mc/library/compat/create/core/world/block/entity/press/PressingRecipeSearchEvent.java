package petrolpark.mc.library.compat.create.core.world.block.entity.press;

import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingRecipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

//TODO add to wiki/mod description
public class PressingRecipeSearchEvent extends Event implements ICancellableEvent {
    private final MechanicalPressBlockEntity blockEntity;
	private final ItemStack stack;
	@Nullable RecipeHolder<PressingRecipe> recipe = null;
	private int maxPriority = 0;

	public PressingRecipeSearchEvent(MechanicalPressBlockEntity blockEntity, ItemStack stack) {
		this.blockEntity = blockEntity;
		this.stack = stack;
	};

	public MechanicalPressBlockEntity getBlockEntity() {
		return blockEntity;
	};

	public ItemStack getStack() {
		return stack;
	};

	// lazyness to not scan for recipes that aren't selected
	public boolean shouldAddRecipeWithPriority(int priority) {
		return !isCanceled() && priority > maxPriority;
	};

	@Nullable
	public RecipeHolder<PressingRecipe> getRecipe() {
		if (isCanceled()) return null;
		return recipe;
	};

	public void addRecipe(Supplier<Optional<RecipeHolder<PressingRecipe>>> recipeSupplier, int priority) {
		if (!shouldAddRecipeWithPriority(priority)) return;
		recipeSupplier.get().ifPresent(newRecipe -> {
			this.recipe = newRecipe;
			maxPriority = priority;
		});
	}
};
