package petrolpark.mc.library.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import java.util.List;

import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.world.item.crafting.pocket.crafter.CookingPocketCrafter;
import petrolpark.mc.library.core.world.item.crafting.pocket.crafter.CraftingPocketCrafter;
import petrolpark.mc.library.core.world.item.crafting.pocket.crafter.IPocketCrafter;

public class PetrolparkPocketCrafters {
  
    public static final RegistryEntry<IPocketCrafter<?>, CookingPocketCrafter<SmeltingRecipe>> SMELTING = REGISTRATE.simple("smelting", PetrolparkRegistries.Keys.POCKET_CRAFTER, () -> new CookingPocketCrafter<>(Petrolpark.translationKey("pocketCrafting.crafter.smelting"), List.of(RecipeType.SMELTING), () -> new ItemStack(Items.FURNACE)));
    public static final RegistryEntry<IPocketCrafter<?>, CraftingPocketCrafter> CRAFTING = REGISTRATE.simple("crafting", PetrolparkRegistries.Keys.POCKET_CRAFTER, () -> new CraftingPocketCrafter());

    public static final void register() {};
};
