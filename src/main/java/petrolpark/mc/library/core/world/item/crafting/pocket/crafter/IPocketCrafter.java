package petrolpark.mc.library.core.world.item.crafting.pocket.crafter;

import java.util.Collections;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import petrolpark.mc.library.core.world.item.crafting.pocket.IPocketCraftingContext;
import petrolpark.mc.library.core.world.item.crafting.pocket.PocketCrafting;
import petrolpark.mc.library.core.world.item.crafting.pocket.interpretedSlot.IInterpretedSlot;
import petrolpark.mc.library.core.world.item.crafting.pocket.interpretedSlot.ItemInterpretedSlot;
import petrolpark.mc.library.registry.PetrolparkRegistries;

@ParametersAreNonnullByDefault
public interface IPocketCrafter<R extends Recipe<?>> {

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<IPocketCrafter<?>>> STREAM_CODEC = ByteBufCodecs.holderRegistry(PetrolparkRegistries.Keys.POCKET_CRAFTER);

    @OnlyIn(Dist.CLIENT)
    public MutableComponent getName();

    public ItemStack getDefaultToolStack();

    public default List<IInterpretedSlot<?>> getSlotInterpretations(IPocketCraftingContext context, Slot slot) {
        return Collections.singletonList(new ItemInterpretedSlot(slot));
    };

    @OnlyIn(Dist.CLIENT)
    public List<RecipeHolder<? extends R>> getRecipes(IPocketCraftingContext.Client context, List<IInterpretedSlot<?>> slots);

    public default int getRecipeLength(IPocketCraftingContext context, RecipeHolder<? extends R> recipe) {
        return 0;
    };
    
    public PocketCrafting.Result craft(IPocketCraftingContext context, boolean simulate, RecipeHolder<? extends R> recipeHolder, List<IInterpretedSlot<?>> inputSlots, Slot outputSlot);
    
};
