package com.petrolpark.compat.create.common.redstone.programmer;

import java.util.List;
import java.util.WeakHashMap;

import javax.annotation.Nonnull;

import com.petrolpark.compat.create.PetrolparkCreateMenuTypes;
import com.petrolpark.compat.create.common.redstone.programmer.RedstoneProgram.Channel;
import com.petrolpark.compat.jei.ghost.IConditionalGhostSlot;
import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.core.inventory.DummyContainer;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;
import com.simibubi.create.foundation.gui.menu.GhostItemMenu;

import net.createmod.catnip.data.Couple;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

@EventBusSubscriber
public class RedstoneProgrammerMenu extends GhostItemMenu<RedstoneProgram> {

    public static final int SCREEN_ITEM_AREA_X = 3;
    public static final int SCREEN_ITEM_AREA_Y = 31;
    public static final int SCREEN_ITEM_AREA_WIDTH = 73;
    public static final int SCREEN_ITEM_AREA_HEIGHT = 154;
    public static final int SCREEN_DISTANCE_BETWEEN_CHANNELS = 20;

    private int offset = 0;

    public RedstoneProgrammerMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf buf) {
        super(type, id, inv, buf);
    };

    protected RedstoneProgrammerMenu(MenuType<?> type, int id, Inventory inv, RedstoneProgram contentHolder) {
        super(type, id, inv, contentHolder);
    };

    public static RedstoneProgrammerMenu create(int id, Inventory inv, RedstoneProgram program) {
        return new RedstoneProgrammerMenu(PetrolparkCreateMenuTypes.REDSTONE_PROGRAMMER.get(), id, inv, program);
    };

    @Override
    protected RedstoneProgram createOnClient(RegistryFriendlyByteBuf extraData) {
        final DummyRedstoneProgram program = DummyRedstoneProgram.STREAM_CODEC.decode(extraData);
        program.powered = extraData.readBoolean();
        return program;
    };

    @Override
    protected ItemStackHandler createGhostInventory() {
        return new ItemStackHandler(maxSlots(contentHolder));
    };

    @Override
    protected boolean allowRepeats() {
        return true;
    };

    @Override
    public boolean canDragTo(Slot slotIn) {
        return true;
    };

    @Override
    protected void addSlots() {
        refreshSlots(0); //TODO determine actual max and channel spacing
    };

    public void refreshSlots() {
        refreshSlots(this.offset);
    };

    public void refreshSlots(int offset) {
        ghostInventory = createGhostInventory();
        this.offset = offset;
        slots.clear();
        lastSlots.clear();
        remoteSlots.clear();
        DummyContainer.addFakePlayerSlots(this::addSlot); // Create GhostItemSubmitPacket assumes all GhostItemMenus include 36 slots of Player Inventory
        int i = 0;
        int position = SCREEN_ITEM_AREA_Y - 16 - this.offset;
        for (int channel = 0; channel < Math.min(contentHolder.getChannels().size() + 1, PetrolparkConfigs.server().redstoneProgrammerMaxChannels.get()); channel++) {
            position += SCREEN_DISTANCE_BETWEEN_CHANNELS;
            Slot slot1 = addSlot(new FrequencySlotItemHandler(i++, SCREEN_ITEM_AREA_X + 32, position, channel, true));
            Slot slot2 = addSlot(new FrequencySlotItemHandler(i++, SCREEN_ITEM_AREA_X + 50, position, channel, false));
            if (channel < contentHolder.getChannels().size()) {
                slot1.set(contentHolder.getChannels().get(channel).networkKey.getFirst().getStack());
                slot2.set(contentHolder.getChannels().get(channel).networkKey.getSecond().getStack());
            };
        };
    };

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
        // Do nothing
    };

    public class FrequencySlotItemHandler extends SlotItemHandler implements IConditionalGhostSlot {

        public final int channelIndex;
        public final boolean first;

        public FrequencySlotItemHandler(int index, int xPosition, int yPosition, int channelIndex, boolean first) {
            super(ghostInventory, index, xPosition, yPosition);
            this.channelIndex = channelIndex;
            this.first = first;
        };

        @Override
        public void set(@Nonnull ItemStack stack) {
            boolean sync = true;
            if (channelIndex >= contentHolder.getChannels().size()) { // Add a new channel
                if (stack.isEmpty()) {
                    sync = false; 
                } else {
                    final Couple<Frequency> networkKey = Couple.create(Frequency.of(ItemStack.EMPTY), Frequency.of(ItemStack.EMPTY));
                    networkKey.set(first, Frequency.of(stack));
                    contentHolder.addBlankChannel(networkKey);
                };
            } else if (!contentHolder.getChannels().isEmpty()) {
                final Channel channel = contentHolder.getChannels().get(channelIndex);
                if (channel.networkKey.get(first).getStack().equals(stack)) {
                    sync = false;
                } else {
                    channel.networkKey.set(first, Frequency.of(stack));
                    if (channel.networkKey.both(f -> f.getStack().isEmpty())) contentHolder.remove(channel);                    
                };
            } else {
                sync = false;
            };
            if (sync) {
                sync();
                refreshSlots();
            };
            super.set(stack);
        };

        @Override
        public boolean canSetGhostItem() {
            int position = SCREEN_DISTANCE_BETWEEN_CHANNELS * channelIndex - offset;
            return position > -1 && position < SCREEN_ITEM_AREA_HEIGHT - SCREEN_DISTANCE_BETWEEN_CHANNELS;
        };

    };

    public void sync() {
        CatnipServices.NETWORK.sendToServer(new SetRedstoneProgramPacket(contentHolder));
    };

    @Override
    protected void saveData(RedstoneProgram contentHolder) {

    };

    public static int maxSlots(RedstoneProgram program) {
        return 2 * Math.min(program.getChannels().size() + 1, PetrolparkConfigs.server().redstoneProgrammerMaxChannels.get());
    };

    public static class DummyRedstoneProgram extends RedstoneProgram {

        public static final StreamCodec<RegistryFriendlyByteBuf, DummyRedstoneProgram> STREAM_CODEC = RedstoneProgram.streamCodec(DummyRedstoneProgram::new);

        public boolean powered;

        public DummyRedstoneProgram() {
            super();
        };

        public DummyRedstoneProgram(PlayMode mode, int length, int playtime, int ticksToNextBeat, boolean paused, boolean pausedLastTick, boolean poweredLastTick, List<ChannelData> channels, int ticksPerBeat, int beatsPerLine, int linesPerBar) {
            super(mode, length, playtime, ticksToNextBeat, paused, pausedLastTick, poweredLastTick, channels, ticksPerBeat, beatsPerLine, linesPerBar);
        };

        @Override
        public void load() {
            // Do nothing, this should never be on a network
        };

        @Override
        public boolean hasPower() {
            return powered;
        };

        @Override
        public BlockPos getBlockPos() {
            return null;
        };

        @Override
        public boolean shouldTransmit() {
            return false;
        };

        @Override
        public LevelAccessor getWorld() {
            return null;
        };

    };

    private static final WeakHashMap<ServerPlayer, Boolean> programmersPowered = new WeakHashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (player.containerMenu instanceof final RedstoneProgrammerMenu menu) {
                final boolean currentPower = menu.contentHolder.hasPower();
                final Boolean oldPower = programmersPowered.get(player);
                if (oldPower == null || currentPower != oldPower) {
                    CatnipServices.NETWORK.sendToClient(player, new ChangeRedstoneProgrammerPowerPacket(menu.contentHolder.hasPower()));
                    programmersPowered.put(player, currentPower);
                };
            } else {
                programmersPowered.remove(player);
            };
        };
    };
    
};
