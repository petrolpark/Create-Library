package petrolpark.mc.library.core.world.item.crafting.pocket;

import java.util.ArrayList;
import java.util.List;

import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import petrolpark.mc.library.core.world.item.crafting.pocket.crafter.IPocketCrafter;
import petrolpark.mc.library.core.world.item.crafting.pocket.interpretedSlot.IInterpretedSlot;

public record PocketCraftPacket<R extends Recipe<?>>(
    Holder<IPocketCrafter<R>> crafter,
    RecipeType<? extends R> recipeType,
    ResourceLocation recipeId,
    List<SlotAndInterpretation> inputSlotsAndInterpretations,
    int outputSlot
) implements ServerboundPacketPayload {

    @Override
    @SuppressWarnings("unchecked")
    public void handle(ServerPlayer player) {
        player.level().getRecipeManager().byKey(recipeId())
            .filter(rh -> rh.value().getType() == recipeType())
            .map(rh -> new RecipeHolder<>(rh.id(), (R)rh.value()))
            .ifPresent(rh -> handle(player, crafter().value(), rh, inputSlotsAndInterpretations(), outputSlot()));
    };

    public static <R extends Recipe<?>> void handle(ServerPlayer player, IPocketCrafter<R> crafter, RecipeHolder<? extends R> recipeHolder, List<SlotAndInterpretation> inputSlotAndInterpretations, int outputSlotIndex) {
        final AbstractContainerMenu menu = player.containerMenu;
        if (menu == null) return;
        final IPocketCraftingContext context = new IPocketCraftingContext.Impl(player.level(), player, menu, ItemStack.EMPTY); //TODO
        final List<IInterpretedSlot> interpretedSlots = new ArrayList<>(inputSlotAndInterpretations.size());
        for (SlotAndInterpretation inputSlotAndInterpretation : inputSlotAndInterpretations) {
            final Slot inputSlot;
            try {
                inputSlot = menu.getSlot(inputSlotAndInterpretation.slot());
            } catch (Throwable e) {
                return;
            };
            final List<IInterpretedSlot> slotInterpretations = crafter.getSlotInterpretations(player.level(), player, menu, inputSlot);
            if (inputSlotAndInterpretation.interpretation() < 0 || inputSlotAndInterpretation.interpretation() >= inputSlotAndInterpretations.size()) return;
            interpretedSlots.add(slotInterpretations.get(inputSlotAndInterpretation.interpretation()));
        };
        final Slot outputSlot;
        try {
            outputSlot = menu.getSlot(outputSlotIndex);
        } catch (Throwable e) {
            return;
        };

        for (boolean simulate : Iterate.trueAndFalse) {
            if (!crafter.craft(context, simulate, recipeHolder, interpretedSlots, outputSlot).successful()) return;
        };
    };

    @Override
    public PacketTypeProvider getTypeProvider() {
        // TODO Auto-generated method stub
        return null;
    };
    
    public record SlotAndInterpretation(int slot, int interpretation) {

    };
};
