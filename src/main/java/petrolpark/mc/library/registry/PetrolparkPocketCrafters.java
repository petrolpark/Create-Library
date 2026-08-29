package petrolpark.mc.library.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import com.tterrag.registrate.util.entry.RegistryEntry;

import petrolpark.mc.library.core.world.item.crafting.pocket.crafter.CraftingPocketCrafter;
import petrolpark.mc.library.core.world.item.crafting.pocket.crafter.IPocketCrafter;

public class PetrolparkPocketCrafters {
  
    public static final RegistryEntry<IPocketCrafter<?>, CraftingPocketCrafter> CRAFTING = REGISTRATE.simple("crafting", PetrolparkRegistries.Keys.POCKET_CRAFTER, () -> new CraftingPocketCrafter());

    public static final void register() {};
};
