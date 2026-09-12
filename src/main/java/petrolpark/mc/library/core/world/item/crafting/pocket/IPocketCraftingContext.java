package petrolpark.mc.library.core.world.item.crafting.pocket;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface IPocketCraftingContext {
    
    public Level level();

    public Player player();

    public AbstractContainerMenu menu();

    public ItemStack toolStack();

    public default HolderLookup.Provider registries() {
        return level().registryAccess();
    };

    public default RecipeManager recipeManager() {
        return level().getRecipeManager();
    };

    public record Impl(Level level, Player player, AbstractContainerMenu menu, ItemStack toolStack) implements IPocketCraftingContext {};

    @OnlyIn(Dist.CLIENT)
    public interface Client extends IPocketCraftingContext {

        public ClientLevel level();

        public LocalPlayer player();

        @OnlyIn(Dist.CLIENT)
        public record Impl(ClientLevel level, LocalPlayer player, AbstractContainerMenu menu, ItemStack toolStack) implements IPocketCraftingContext.Client {};
    };
};
