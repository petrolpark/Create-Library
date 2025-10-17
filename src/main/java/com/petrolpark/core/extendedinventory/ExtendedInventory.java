package com.petrolpark.core.extendedinventory;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.ApiStatus;

import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkAttributes;
import com.petrolpark.PetrolparkFeatureFlags;
import com.petrolpark.PetrolparkTags;
import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.util.EntityHelper;

import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@ApiStatus.Experimental
@EventBusSubscriber(modid = Petrolpark.MOD_ID)
public class ExtendedInventory extends Inventory {

    /**
     * Whether the {@link PetrolparkFeatureFlags#EXTENDED_INVENTORY Extended Inventory experiment} is enabled in this world.
     * @return {@code false} if it is not enabled, or if it is too early to say
     * @see ExtendedInventory#enabled(FeatureFlagSet) Safer method to use if possible
     */
    public static final boolean enabled() {
        if (Petrolpark.runForDist(() -> () -> Minecraft.getInstance().getConnection() == null, () -> () -> false)) return false; // Not initialized before we join the world
        return PetrolparkFeatureFlags.EXTENDED_INVENTORY.isEnabled();
    };

    /**
     * Whether the {@link PetrolparkFeatureFlags#EXTENDED_INVENTORY Extended Inventory experiment} is enabled in this world.
     * @param flagSet
     * @see ExtendedInventory#enabled() Less safe method to use if necessary
     */
    public static final boolean enabled(FeatureFlagSet flagSet) {
        return PetrolparkFeatureFlags.EXTENDED_INVENTORY.isEnabled(flagSet);
    };

    /**
     * The ItemStacks additional to those in the Vanilla Inventory, including all those on the Hotbar.
     * Controlled by {@link PetrolparkAttributes#EXTRA_INVENTORY_SIZE}.
     */
    public NonNullList<ItemStack> extraItems = NonNullList.of(ItemStack.EMPTY);
    /**
     * The number of additional Hotbar Slots. This is limited by the size of {@link ExtendedInventory#extraItems}.
     * Controlled by {@link PetrolparkAttributes#EXTRA_HOTBAR_SLOTS}.
     */
    private int extraHotbarSlots = 0;

    public ExtendedInventory(Player player) {
        super(player);
        updateSize();
    };

    /**
     * Attempt to access the Player's Extended Inventory (cast as such).
     * @param player
     * @return The Player's Extended Inventory, if they are enabled in this world and one can be found
     */
    public static Optional<ExtendedInventory> get(Player player) {
        return player.getInventory() instanceof ExtendedInventory extendedInv ? Optional.of(extendedInv) : Optional.empty();
    };

    /**
     * @see ExtendedInventory#updateSize(boolean)
     */
    public void updateSize() {
        updateSize(false);
    };

    /**
     * Update the size of the Extended Inventory and Hotbar based on the corresponding {@link PetrolparkAttributes}.
     * @param forceSync Whether to sync the size of the Extended Inventory to the client
     * @see ExtendedInventory#updateSize() Shortcut call with {@code forceSync = false}
     */
    public void updateSize(boolean forceSync) {
        int sizeBefore = extraItems.size();
        int hotbarBefore = extraHotbarSlots;
        if (player.getAttributes().hasAttribute(PetrolparkAttributes.EXTRA_HOTBAR_SLOTS.getDelegate())) setExtraHotbarSlots((int)player.getAttributeValue(PetrolparkAttributes.EXTRA_HOTBAR_SLOTS.getDelegate()));
        if (player.getAttributes().hasAttribute(PetrolparkAttributes.EXTRA_INVENTORY_SIZE.getDelegate())) setExtraInventorySize((int)player.getAttributeValue(PetrolparkAttributes.EXTRA_INVENTORY_SIZE.getDelegate()));
        if ((forceSync || sizeBefore != extraItems.size() || hotbarBefore != extraHotbarSlots) && !player.level().isClientSide() && player instanceof ServerPlayer sp && sp.connection != null) {
            player.closeContainer();
            CatnipServices.NETWORK.sendToClient(sp, new ExtraInventorySizeChangePacket(extraItems.size(), extraHotbarSlots, false));
            refreshPlayerInventoryMenuServer(sp);
        };
    };

    /**
     * Re-create the Player's {@link Player#inventoryMenu Inventory Menu} with the right number of extra Slots, in the right places.
     * This is the riskiest part of the whole Extended Inventory API as we are reassigning what was originally a {@code final} field.
     * @param player
     * @param columns How many columns in the (non-Hotbar) Inventory set of Slots
     * @param invX Left side of the (non-Hotbar) Inventory set of Slots
     * @param invY Top side of the (non-Hotbar) Inventory set of Slots
     * @param leftHotbarSlots How many Hotbar Slots to put on the left of the Vanilla Hotbar
     * @param leftHotbarX Left side of the extra Hotbar Slots to the left of the Vanilla Hotbar
     * @param leftHotbarY Top side of the extra Hotbar Slots to the left of the Vanilla Hotbar
     * @param rightHotbarX Left side of the extra Hotbar Slots to the right of the Vanilla Hotbar
     * @param rightHotbarY Right side of the extra Hotbar Slots to the right of the Vanilla Hotbar
     * @see ExtendedInventory#refreshPlayerInventoryMenuServer(Player) Refresh the Inventory Menu with the Slots in unknown positions (possible on the server side)
     */
    public static void refreshPlayerInventoryMenu(Player player, int columns, int invX, int invY, int leftHotbarSlots, int leftHotbarX, int leftHotbarY, int rightHotbarX, int rightHotbarY) {
        InventoryMenu oldMenu = player.inventoryMenu;
        InventoryMenu newMenu = new InventoryMenu(player.getInventory(), !player.level().isClientSide(), player); // Usually this field would be final; don't tell anybody I did this
        player.inventoryMenu = newMenu;
        get(player).ifPresent(inv -> inv.addExtraInventorySlotsToMenu(player.inventoryMenu, columns, invX, invY, leftHotbarSlots, leftHotbarX, leftHotbarY, rightHotbarX, rightHotbarY));
        player.containerMenu = player.inventoryMenu;
        if (player instanceof ServerPlayer sp && sp.containerSynchronizer != null && sp.containerListener != null) sp.initInventoryMenu();
        newMenu.containerListeners.addAll(oldMenu.containerListeners);
    };

    /**
     * Re-create the Player's {@link Player#inventoryMenu Inventory Menu} on the logical server.
     * All Slots are added at (0,0) (or some arbitrary location) because their location does not matter on the server side.
     * @param player
     * @see ExtendedInventory#refreshPlayerInventoryMenu(Player, int, int, int, int, int, int, int, int) Specify the locations of the Slots
     */
    public static void refreshPlayerInventoryMenuServer(Player player) {
        refreshPlayerInventoryMenu(player, 5, 0, 0, 0, 0, 0, 0, 0);
    };

    /**
     * Whether the given Slot index corresponds to a Vanilla Hotbar Slot.
     * @param index
     * @return {@code false} if the Slot is not in the Vanilla Hotbar, even if there are additional Hotbar Slots
     */
    public static boolean isVanillaHotbarSlot(int index) {
        return Inventory.isHotbarSlot(index);
    };

    /**
     * @param size The additional Slots beyond what the Player would usually have
     */
    public void setExtraInventorySize(int size) {
        size = Math.max(size, 0);
        if (size == extraItems.size()) return;
        if (size < extraItems.size()) {
            for (int stack = size; stack < extraItems.size(); stack++) {
                placeItemBackInInventory(extraItems.get(stack), true);
            };
        };
        NonNullList<ItemStack> newExtraItems = NonNullList.withSize(size, ItemStack.EMPTY);
        for (int i = 0; i < size && i < extraItems.size(); i++) newExtraItems.set(i, extraItems.get(i));
        extraItems = newExtraItems;
        setChanged();
    };

    /**
     * @param extraSlots The additional Slots beyond what the Player would usually have
     */
    public void setExtraHotbarSlots(int extraSlots) {
        extraSlots = Math.max(extraSlots, 0);
        if (extraSlots == extraHotbarSlots) return;
        extraHotbarSlots = extraSlots;
        setChanged();
    };

    /**
     * The number of additional Hotbar Slots beyond the usual 9.
     */
    public int getExtraHotbarSlots() {
        return Math.min(extraItems.size(), extraHotbarSlots);
    };

    public int getExtraInventoryStartSlotIndex() {
        return super.getContainerSize();
    };
    /**
     * Whether the given Slot index is part of the extended Hotbar
     * @param index
     * @return {@code true} if it's a Vanilla or extended index
     */
    public boolean isFullHotbarSlot(int index) {
        int extraInventoryStart = getExtraInventoryStartSlotIndex();
        return isVanillaHotbarSlot(index) || (index >= extraInventoryStart && index - extraInventoryStart < getExtraHotbarSlots());
    };

    /**
     * The total Hotbar size, Vanilla + extra Slots
     * @see ExtendedInventory#getExtraHotbarSlots()
     */
    public int getHotbarSize() {
        return getSelectionSize() + getExtraHotbarSlots();
    };

    /**
     * Get the Slot index of the given index in the displayed Hotbar - how far right the selected Slot is, considering the sides on which the extra Slots are
     * @param hotbarIndex A number from {@code 0} to {@code 8 + getExtraHotbarSlots()}
     */
    protected int getSlotIndex(int hotbarIndex) {
        if (hotbarIndex < 0 || hotbarIndex >= getHotbarSize()) return -1;
        if (hotbarIndex < 9) return hotbarIndex;
        return getExtraInventoryStartSlotIndex() + hotbarIndex - getSelectionSize();
    };

    /**
     * The Slot index of the currently selected Slot 
     * @return
     */
    public int getSelectedHotbarIndex() {
        if (isVanillaHotbarSlot(selected)) return selected;
        return selected - getExtraInventoryStartSlotIndex() + getSelectionSize();
    };

    @SubscribeEvent
    public static void onPlayerJoinsWorld(PlayerEvent.PlayerLoggedInEvent event) {
        if (!enabled(event.getEntity().level().enabledFeatures())) return;
        refreshPlayerInventoryMenuServer(event.getEntity());
        Optional<ExtendedInventory> invOp = get(event.getEntity());
        if (event.getEntity() instanceof ServerPlayer player && invOp.isPresent()) {
            CatnipServices.NETWORK.sendToClient(player, new ExtraInventorySizeChangePacket(invOp.get().extraItems.size(), invOp.get().extraHotbarSlots, true));
        };
    };

    @SubscribeEvent
    public static void onOpenContainer(PlayerContainerEvent.Open event) {
        if (!enabled()) return;
        AbstractContainerMenu menu = event.getContainer();
        if (!supportsExtraInventory(menu) || menu instanceof IExtendedInventoryMenu) return;
        get(event.getEntity()).ifPresent(inv -> inv.addExtraInventorySlotsToMenu(menu, 5, 0, 0, 0, 0, 0, 0, 0));
    };

    public static boolean supportsExtraInventory(AbstractContainerMenu menu) {
        if (menu instanceof IExtendedInventoryMenu) return true;
        try {
            MenuType<?> menuType = menu.getType();
            if (menuType == null) return false;
            if (PetrolparkConfigs.server().extendedInventorySafeMode.get()) {
                return PetrolparkTags.MenuTypes.ALWAYS_SHOWS_EXTENDED_INVENTORY.matches(menuType);
            } else {
                return !PetrolparkTags.MenuTypes.NEVER_SHOWS_EXTENDED_INVENTORY.matches(menuType);
            }
        } catch (UnsupportedOperationException e) {
            return false;
        }
    };

    public void addExtraInventorySlotsToMenu(AbstractContainerMenu menu, int columns, int invX, int invY, int leftHotbarSlots, int leftHotbarX, int leftHotbarY, int rightHotbarX, int rightHotbarY) {
        addExtraInventorySlotsToMenu(menu::addSlot, Slot::new, columns, invX, invY, leftHotbarSlots, leftHotbarX, leftHotbarY, rightHotbarX, rightHotbarY);
    };

    public void addExtraInventorySlotsToMenu(Consumer<Slot> slotAdder, SlotFactory slotFactory, int columns, int invX, int invY, int leftHotbarSlots, int leftHotbarX, int leftHotbarY, int rightHotbarX, int rightHotbarY) {
        int extraItemsStart = getExtraInventoryStartSlotIndex();

        // Add right Hotbar Slots
        for (int i = 0; i < getExtraHotbarSlots() - leftHotbarSlots; i++) {
            slotAdder.accept(slotFactory.create(this, extraItemsStart + i, rightHotbarX + i * 18, rightHotbarY));
        };
        
        // Add left Hotbar Slots
        int j = 0;
        for (int i = getExtraHotbarSlots() - leftHotbarSlots; i < getExtraHotbarSlots(); i++) {
            slotAdder.accept(slotFactory.create(this, extraItemsStart + i, leftHotbarX + j * 18, leftHotbarY));
            j++;
        };

        // Add non-Hotbar Slots
        j = 0;
        for (int i = getExtraHotbarSlots(); i < extraItems.size(); i++) {
            slotAdder.accept(slotFactory.create(this, extraItemsStart + i, invX + 18 * (j % columns), invY + 18 * (j / columns)));
            j++;
        };
    };

    @FunctionalInterface
    public static interface SlotFactory {
        public Slot create(Container container, int slotIndex, int x, int y);
    };

    public void forEach(Consumer<? super ItemStack> action) {
        items.forEach(action);
        armor.forEach(action);
        offhand.forEach(action);
        extraItems.forEach(action);
    };

    public Stream<ItemStack> stream() {
        return Stream.concat(Stream.concat(items.stream(), armor.stream()), Stream.concat(offhand.stream(), extraItems.stream()));
    };

    @Override
    public ItemStack getSelected() {
        int extraInventoryStart = getExtraInventoryStartSlotIndex();
        if (selected >= extraInventoryStart) {
            int selectedExtra = selected - extraInventoryStart;
            if (selectedExtra < getExtraHotbarSlots()) return extraItems.get(selectedExtra);
        };
        return super.getSelected();
    };

    @Override
    public int getFreeSlot() {
        int freeSlot = super.getFreeSlot();
        if (freeSlot == -1) {
            for (int i = 0; i < extraItems.size(); i++) {
                if (extraItems.get(i).isEmpty()) return getExtraInventoryStartSlotIndex() + i;
            };
            return -1;
        } else {
            return freeSlot;
        }
    };

    /**
     * Pick an Item in Creative mode
     */
    @Override
    public void setPickedItem(@Nonnull ItemStack stack) {
        int matchingSlot = findSlotMatchingItem(stack);
        if (isFullHotbarSlot(matchingSlot)) {
            selected = matchingSlot;
        } else if (matchingSlot != -1) {
            pickSlot(matchingSlot);
        } else {
            selected = getSuitableHotbarSlot(); // Switch to a new or replaceable Hotbar Slot
            if (!getItem(selected).isEmpty()) { // Find a place to put the old Item which was selected
                int freeSlot = getFreeSlot();
                if (freeSlot != -1) setItem(freeSlot, stack);
            };
            setItem(selected, stack); // Give the Item
        };
    };

    /**
     * Stick an Item from the Inventory in the Hotbar
     */
    @Override
    public void pickSlot(int index) {
        selected = getSuitableHotbarSlot();
        ItemStack oldSelectedStack = getItem(selected);
        setItem(selected, getItem(index)); // Swap the two Items
        setItem(index, oldSelectedStack);
    };

    @Override
    public int findSlotMatchingItem(@Nonnull ItemStack stack) {
        return findSlot(s -> !s.isEmpty() && ItemStack.isSameItemSameComponents(s, stack));
    };

    @Override
    public int findSlotMatchingUnusedItem(@Nonnull ItemStack stack) {
        return findSlot(s -> !s.isEmpty() && ItemStack.isSameItemSameComponents(s, stack) && !s.isDamaged() && !s.isEnchanted() && !s.has(DataComponents.CUSTOM_NAME));
    };

    /**
     * Search for a Slot in the Vanilla and extended Inventories (i.e. not armor or offhand).
     * @param stackPredicate
     */
    public int findSlot(Predicate<ItemStack> stackPredicate) {
        for (int i = 0; i < items.size(); i++) {
            if (stackPredicate.test(items.get(i))) return i;
        };
        for (int i = 0; i < extraItems.size(); i++) {
            if (stackPredicate.test(extraItems.get(i))) return getExtraInventoryStartSlotIndex() + i;
        };
        return -1;
    };

    @Override
    public int getSuitableHotbarSlot() {
        int selectedHotbarSlot = getSelectedHotbarIndex();
        for (int i = 0; i < getHotbarSize(); i++) {
            int nextSlot = getSlotIndex((selectedHotbarSlot + i) % getHotbarSize());
            if (getItem(nextSlot).isEmpty()) return nextSlot;
        };
        for (int i = 0; i < getHotbarSize(); i++) {
            int nextSlot = getSlotIndex((selectedHotbarSlot + i) % getHotbarSize());
            if (!getItem(nextSlot).isNotReplaceableByPickAction(player, nextSlot)) return nextSlot;
        };
        return -1;
    };

    @Override
    public void swapPaint(double scroll) {
        int d = (int)Math.signum(scroll);
        int selectedHotbarSlot = getSelectedHotbarIndex();
        for (selectedHotbarSlot -= d; selectedHotbarSlot < 0; selectedHotbarSlot += getHotbarSize());
        while (selectedHotbarSlot >= getHotbarSize()) selectedHotbarSlot -= getHotbarSize();
        selected = getSlotIndex(selectedHotbarSlot);
    };

    @Override
    public int getSlotWithRemainingSpace(@Nonnull ItemStack stack) {
        int slot = super.getSlotWithRemainingSpace(stack);
        if (slot == -1) {
            for (int i = 0; i < extraItems.size(); i++) {
                if (hasRemainingSpaceForItem(extraItems.get(i), stack)) return getExtraInventoryStartSlotIndex() + i;
            };
        };
        return slot;
    };

    @Override
    public void tick() {
        updateSize();
        super.tick();
        for (int i = 0; i < extraItems.size(); i++) {
            int slot = getExtraInventoryStartSlotIndex() + i;
            extraItems.get(i).inventoryTick(player.level(), player, slot, selected == slot);
        };
    };

    /**
     * Copied from {@link Inventory#add(int, ItemStack) Minecraft source code}.
     * Place an ItemStack in the specified Slot, and shrink the ItemStack appropriately.
     * @return {@code true} if any of the ItemStack could be placed in the Slot
     */
    @Override
    public boolean add(int slot, @Nonnull ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        } else {
            try {
                if (stack.isDamaged()) {
                    if (slot == -1) slot = getFreeSlot();
                    if (slot >= getExtraInventoryStartSlotIndex()) {
                        int extraItemsIndex = slot - getExtraInventoryStartSlotIndex();
                        extraItems.set(extraItemsIndex, stack.copyAndClear());
                        extraItems.get(extraItemsIndex).setPopTime(5);
                        return true;
                    } else if (slot >= 0) {
                        items.set(slot, stack.copyAndClear());
                        items.get(slot).setPopTime(5);
                        return true;
                    } else if (player.getAbilities().instabuild) { // Creative players can delete Items
                        stack.setCount(0);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    int count;
                    do {
                        count = stack.getCount();
                        if (slot == -1) {
                            stack.setCount(addResource(stack));
                        } else {
                            stack.setCount(addResource(slot, stack));
                        };
                    } while (!stack.isEmpty() && stack.getCount() < count);

                    if (stack.getCount() == count && player.getAbilities().instabuild) {
                        stack.setCount(0);
                        return true;
                    } else {
                        return stack.getCount() < count;
                    }
                }
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Adding item to inventory");
                CrashReportCategory crashreportcategory = crashreport.addCategory("Item being added");
                crashreportcategory.setDetail("Registry Name", () -> String.valueOf(BuiltInRegistries.ITEM.getKey(stack.getItem())));
                crashreportcategory.setDetail("Item Class", () -> stack.getItem().getClass().getName());
                crashreportcategory.setDetail("Item ID", Item.getId(stack.getItem()));
                crashreportcategory.setDetail("Item data", stack.getDamageValue());
                crashreportcategory.setDetail("Item name", () -> {
                    return stack.getHoverName().getString();
                });
                throw new ReportedException(crashreport);
            }
        }
    };

    @Override
    public ItemStack removeItem(int slotIndex, int count) {
        if (slotIndex >= getExtraInventoryStartSlotIndex()) {
            slotIndex -= getExtraInventoryStartSlotIndex();
            if (slotIndex < extraItems.size()) {
                if (extraItems.get(slotIndex).isEmpty()) return ItemStack.EMPTY; 
                return ContainerHelper.removeItem(extraItems, slotIndex, count);
            };
        };
        return super.removeItem(slotIndex, count);
    };

    @Override
    public void removeItem(@Nonnull ItemStack stack) {
        if (!extraItems.removeIf(s -> s == stack)) super.removeItem(stack);
    };

    @Override
    public ItemStack removeItemNoUpdate(int slotIndex) {
        if (slotIndex >= getExtraInventoryStartSlotIndex()) {
            slotIndex -= getExtraInventoryStartSlotIndex();
            if (slotIndex < extraItems.size()) {
                ItemStack stack = extraItems.get(slotIndex);
                if (stack.isEmpty()) return ItemStack.EMPTY; 
                extraItems.set(slotIndex, ItemStack.EMPTY);
                return stack;
            };
        };
        return super.removeItemNoUpdate(slotIndex);
    };

    @Override
    public void setItem(int slotIndex, @Nonnull ItemStack stack) {
        if (slotIndex >= getExtraInventoryStartSlotIndex()) {
            slotIndex -= getExtraInventoryStartSlotIndex();
            if (slotIndex < extraItems.size()) {
                extraItems.set(slotIndex, stack);
                return;
            };
        };
        super.setItem(slotIndex, stack);
    };

    @Override
    public float getDestroySpeed(@Nonnull BlockState state) {
        return getItem(selected).getDestroySpeed(state);
    };

    @Override
    public ListTag save(@Nonnull ListTag listTag) {
        listTag = super.save(listTag);

        int extraInventoryStart = getExtraInventoryStartSlotIndex();
        for (int i = 0; i < extraItems.size(); i++) {
            ItemStack stack = extraItems.get(i);
            if (!stack.isEmpty()) {
               CompoundTag tag = new CompoundTag();
               tag.putByte("Slot", (byte)(i + extraInventoryStart));
               listTag.add(stack.save(player.registryAccess(), tag));
            };
        };

        return listTag;
    };

    @Override
    public void load(@Nonnull ListTag listTag) {
        super.load(listTag);
        EntityHelper.refreshEquipmentAttributeModifiers(player); // Need to do this now as Attribute Modifiers due to equipped Items don't usually load until after the whole Inventory
        updateSize();
        int extraInventoryStart = getExtraInventoryStartSlotIndex();
        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag tag = listTag.getCompound(i);
            if (tag.contains("Slot", Tag.TAG_BYTE)) {
                int slotIndex = tag.getByte("Slot") & 255;
                if (slotIndex >= extraInventoryStart) {
                    slotIndex -= extraInventoryStart;
                    if (slotIndex < extraItems.size()) {
                        ItemStack stack = ItemStack.parse(player.registryAccess(), tag).orElse(ItemStack.EMPTY);
                        extraItems.set(slotIndex, stack);
                    };
                };
            };
        };
    };

    @Override
    public int getContainerSize() {
        return super.getContainerSize() + extraItems.size();
    };

    @Override
    public boolean isEmpty() {
        return super.isEmpty() && !extraItems.stream().anyMatch(stack -> !stack.isEmpty());
    };

    @Override
    public ItemStack getItem(int slotIndex) {
        int extraInventoryStart = getExtraInventoryStartSlotIndex();
        if (slotIndex >= extraInventoryStart) {
            slotIndex -= extraInventoryStart;
            if (slotIndex < extraItems.size()) return extraItems.get(slotIndex);
        };
        return super.getItem(slotIndex);
    };

    @Override
    public void dropAll() {
        super.dropAll();
        for (int i = 0; i < extraItems.size(); i++) {
            ItemStack stack = extraItems.get(i);
            if (stack.isEmpty()) continue;
            player.drop(stack, true, false);
            extraItems.set(i, ItemStack.EMPTY);
        };
    };

    @Override
    public boolean contains(@Nonnull ItemStack stack) {
        return findSlot(s -> ItemStack.isSameItemSameComponents(s, stack)) != -1;
    };

    @Override
    public boolean contains(@Nonnull TagKey<Item> tag) {
        return findSlot(s -> s.is(tag)) != -1;
    };

    @Override
    public void replaceWith(@Nonnull Inventory playerInventory) {
        if (playerInventory instanceof ExtendedInventory extendedInv) {
            setExtraHotbarSlots(extendedInv.extraHotbarSlots);
            setExtraInventorySize(extendedInv.extraItems.size());
        };
        super.replaceWith(playerInventory);
    };

    @Override
    public void clearContent() {
        super.clearContent();
        extraItems.clear();
    };

    @Override
    public void fillStackedContents(@Nonnull StackedContents stackedContents) {
        super.fillStackedContents(stackedContents);
        extraItems.forEach(stackedContents::accountSimpleStack);
    };

    public static interface DelayedSlotPopulation {
        public void populateDelayedSlots();
    };
    
};
