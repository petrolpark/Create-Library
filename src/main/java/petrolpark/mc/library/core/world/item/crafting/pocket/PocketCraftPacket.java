package petrolpark.mc.library.core.world.item.crafting.pocket;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import petrolpark.mc.library.core.world.item.crafting.pocket.crafter.IPocketCrafter;
import petrolpark.mc.library.core.world.item.crafting.pocket.interpretedSlot.IInterpretedSlot;
import petrolpark.mc.library.registry.PetrolparkPackets;
import petrolpark.mc.library.registry.PetrolparkRegistries;

public record PocketCraftPacket(
    IPocketCrafter<?> crafter,
    ResourceLocation recipeId,
    List<SlotAndInterpretation> inputSlotsAndInterpretations,
    Optional<Integer> outputSlot
) implements ServerboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, PocketCraftPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.registry(PetrolparkRegistries.Keys.POCKET_CRAFTER), PocketCraftPacket::crafter,
        ResourceLocation.STREAM_CODEC, PocketCraftPacket::recipeId,
        SlotAndInterpretation.STREAM_CODEC.apply(ByteBufCodecs.list()), PocketCraftPacket::inputSlotsAndInterpretations,
        ByteBufCodecs.optional(ByteBufCodecs.INT), PocketCraftPacket::outputSlot,
        PocketCraftPacket::new
    );

    @Override
    public void handle(ServerPlayer player) {
        handleTyped(player, crafter());
    };

    @SuppressWarnings("unchecked")
    public <R extends Recipe<?>> void handleTyped(ServerPlayer player, IPocketCrafter<R> crafter) {
        player.level().getRecipeManager().byKey(recipeId())
            .filter(rh -> crafter().canCastRecipe(rh.value()))
            .map(rh -> new RecipeHolder<>(rh.id(), (R)rh.value()))
            .ifPresent(rh -> handle(player, crafter, rh, inputSlotsAndInterpretations(), outputSlot()));
    };

    public static <R extends Recipe<?>> void handle(ServerPlayer player, IPocketCrafter<R> crafter, RecipeHolder<? extends R> recipeHolder, List<SlotAndInterpretation> inputSlotAndInterpretations, Optional<Integer> outputSlotIndex) {
        final AbstractContainerMenu menu = player.containerMenu;
        if (menu == null) return;
        final IPocketCraftingContext context = new IPocketCraftingContext.Impl(player.level(), player, menu, ItemStack.EMPTY); //TODO
        final List<IInterpretedSlot<?>> interpretedSlots = new ArrayList<>(inputSlotAndInterpretations.size());
        for (SlotAndInterpretation inputSlotAndInterpretation : inputSlotAndInterpretations) {
            final Slot inputSlot;
            try {
                inputSlot = menu.getSlot(inputSlotAndInterpretation.slot());
            } catch (IndexOutOfBoundsException e) {
                return;
            };
            final List<IInterpretedSlot<?>> slotInterpretations = crafter.getSlotInterpretations(context, inputSlot);
            if (inputSlotAndInterpretation.interpretation() < 0 || inputSlotAndInterpretation.interpretation() >= inputSlotAndInterpretations.size()) return;
            interpretedSlots.add(slotInterpretations.get(inputSlotAndInterpretation.interpretation()));
        };
        Optional<Slot> outputSlot;
        try {
            outputSlot = outputSlotIndex.map(menu::getSlot);
        } catch (IndexOutOfBoundsException | NoSuchElementException e) {
            outputSlot = Optional.empty();
        };

        for (boolean simulate : Iterate.trueAndFalse) {
            if (!crafter.craft(context, simulate, recipeHolder, interpretedSlots, outputSlot.orElse(null)).successful()) return;
        };
    };

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PetrolparkPackets.POCKET_CRAFT;
    };
    
    public record SlotAndInterpretation(int slot, int interpretation) {

        public static final StreamCodec<FriendlyByteBuf, SlotAndInterpretation> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SlotAndInterpretation::slot,
            ByteBufCodecs.INT, SlotAndInterpretation::interpretation,
            SlotAndInterpretation::new
        );
    };
};
