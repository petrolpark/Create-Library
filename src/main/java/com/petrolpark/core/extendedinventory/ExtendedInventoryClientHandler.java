package com.petrolpark.core.extendedinventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkKeys;
import com.petrolpark.client.rendering.PetrolparkGuiTexture;
import com.petrolpark.client.rendering.PetrolparkNineSlices;
import com.petrolpark.config.PetrolparkClientConfig;
import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.core.extendedinventory.ExtendedInventory.DelayedSlotPopulation;
import com.petrolpark.core.extendedinventory.ExtendedInventory.SlotFactory;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;

import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.GameType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@OnlyIn(Dist.CLIENT)
@ApiStatus.Experimental
public class ExtendedInventoryClientHandler {

    /**
     * Ordered list of Keys which switch to the different Hotbar slots.
     */
    private static final List<KeyMapping> HOTBAR_KEYS = new ArrayList<>(17);

    /**
     * Whether {@link ExtendedInventoryClientHandler#HOTBAR_KEYS_INITIALIZED} is the complete list yet (it may not be as the Keys from the Petrolpark Library are added after registration).
     */
    private static boolean HOTBAR_KEYS_INITIALIZED = false;

    /**
     * The last known settings for where the Extended Inventory Slots should be rendered in a menu.
     */
    private ExtraInventoryClientSettings settings = null;
    
    /**
     * Tick the Extended Inventory, client side.
     * This:<ul>
     * <li>Checks to see if the {@link ExtendedInventoryClientHandler#settings render settings} of the Extended Inventory has been changed in the configs
     * <li>Consumes Hotbar key presses
     * </ul>
     * <p> </p>
     * @param event
     */
    @SubscribeEvent
    public final void onClientTick(ClientTickEvent.Pre event) {
        if (!ExtendedInventory.enabled()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        Optional<ExtendedInventory> invOp = ExtendedInventory.get(mc.player);
        if (invOp.isEmpty()) return;
        ExtendedInventory inv = invOp.get();

        // Update survival Inventory  screen if the layout of the Extended Inventory has changed
        ExtraInventoryClientSettings currentSettings = getExtraInventoryClientSettings();
        if (!currentSettings.equals(settings)) {
            settings = currentSettings;
            refreshClientInventoryMenu(inv);
        };

        // Initialize Key Mappings
        if (!HOTBAR_KEYS_INITIALIZED) {
            Collections.addAll(HOTBAR_KEYS, mc.options.keyHotbarSlots);
            Collections.addAll(HOTBAR_KEYS, PetrolparkKeys.HOTBAR_SLOT_9.keybind, PetrolparkKeys.HOTBAR_SLOT_10.keybind, PetrolparkKeys.HOTBAR_SLOT_11.keybind, PetrolparkKeys.HOTBAR_SLOT_12.keybind, PetrolparkKeys.HOTBAR_SLOT_13.keybind, PetrolparkKeys.HOTBAR_SLOT_14.keybind, PetrolparkKeys.HOTBAR_SLOT_15.keybind, PetrolparkKeys.HOTBAR_SLOT_16.keybind);
            HOTBAR_KEYS_INITIALIZED = true;
        };

        // Allow switching to extended Hotbar Slots
        if (mc.getOverlay() != null || mc.screen != null || mc.options.keyLoadHotbarActivator.isDown() || mc.options.keySaveHotbarActivator.isDown()) return;
        for (int i = 0; i < inv.getHotbarSize(); i++) {
            if (HOTBAR_KEYS.get(i).consumeClick()) {
                int slot = i - getLeftExtraHotbarSlots(inv.getExtraHotbarSlots());
                if (slot < 0) slot += inv.getHotbarSize();
                inv.selected = inv.getSlotIndex(slot);
            };
        };
    };

    /**
     * Refresh the locations of the Extended Inventory Slots in the Survival Inventory only.
     * @param inv The Player's Extended Inventory
     */
    public static void refreshClientInventoryMenu(ExtendedInventory inv) {
        Rect2i screenArea = new Rect2i(0, 0, 176, 166);
        Rect2i leftHotbar = getLeftHotbarLocation(inv, screenArea, 142);
        Rect2i rightHotbar  = getRightHotbarLocation(inv, screenArea, 142);
        Rect2i combinedInventoryHotbar = getCombinedInventoryHotbarLocation(inv, screenArea, 142);
        Rect2i inventory = combinedInventoryHotbar == null ? getInventoryLocation(inv, screenArea, 142) : combinedInventoryHotbar;
        int invX;
        int invY;
        if (inventory == null) {
            invX = 0;
            invY = 0;
        } else {
            invX = inventory.getX();
            invY = inventory.getY();
        };
        int leftX;
        int leftY;
        if (leftHotbar != null) {
            leftX = leftHotbar.getX() + INVENTORY_PADDING;
            leftY = leftHotbar.getY() + INVENTORY_PADDING;
        } else {
            leftX = 0;
            leftY = 0;
        };
        int rightX;
        int rightY;
        if (rightHotbar != null) {
            rightX = rightHotbar.getX() + INVENTORY_PADDING;
            rightY = rightHotbar.getY() + INVENTORY_PADDING;
        } else {
            rightX = 0;
            rightY = 0;
        };
        ExtendedInventory.refreshPlayerInventoryMenu(inv.player,getExtraInventoryWidth(), invX + INVENTORY_PADDING, invY + INVENTORY_PADDING, getLeftExtraHotbarSlots(inv.getExtraHotbarSlots()), leftX, leftY, rightX, rightY);
    };

    /**
     * Syncronize the additional Inventory Slots and Hotbar Slots on the client so they match the server. This should be done whenever attributes are synced anyway, but we have this just in case.
     * @param packet
     */
    public static void handleExtendedInventorySizeChange(ExtraInventorySizeChangePacket packet) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        Optional<ExtendedInventory> invOp = ExtendedInventory.get(mc.player);
        if (invOp.isEmpty()) return;
        ExtendedInventory inv = invOp.get();
        inv.setExtraInventorySize(packet.extraInventorySize());
        inv.setExtraHotbarSlots(packet.extraHotbarSlots());
        refreshClientInventoryMenu(inv);
        if (packet.requestFullState()) CatnipServices.NETWORK.sendToServer(RequestInventoryFullStatePacket.INSTANCE); // Get an up-to-date list of the Items in the additional Slots
    };

    /**
     * The space between the edge of the Extended Inventory "window" and the actual Slot.
     */
    public static final int INVENTORY_PADDING = 7;
    /**
     * The space between the regular Inventory "window" and any Extended Inventory "window".
     */
    public static final int INVENTORY_SPACING = 4;
    /**
     * The vertical space between the main Inventory and Hotbar Slots.
     */
    public static final int INVENTORY_HOTBAR_SPACING = 4;

    /**
     * The number of additional Hotbar Slots to the left of the Vanilla Hotbar.
     * @param totalExtraHotbarSlots The number of extra Hotbar Slots on either side of the vanilla hotbar
     */
    public static final int getLeftExtraHotbarSlots(int totalExtraHotbarSlots) {
        if (totalExtraHotbarSlots == 0) return 0;
        switch (getExtraHotbarSlotLocations()) {
            case ALL_LEFT: return totalExtraHotbarSlots;
            case ALL_RIGHT: return 0;
            case START_LEFT: return (totalExtraHotbarSlots % 2) + (totalExtraHotbarSlots / 2);
            case START_RIGHT: return totalExtraHotbarSlots / 2;
            case PRIORITY_LEFT: return Math.min(totalExtraHotbarSlots, getExtraHotbarPrioritySlots());
            case PRIORITY_RIGHT: return Math.max(0, totalExtraHotbarSlots - getExtraHotbarPrioritySlots());
        };
        return 0;
    };

    /**
     * The number of additional Hotbar Slots to the right of the Vanilla Hotbar.
     * @param totalExtraHotbarSlots The number of extra Hotbar Slots on either side of the vanilla hotbar
     */
    public static final int getRightExtraHotbarSlots(int totalExtraHotbarSlots) {
        return totalExtraHotbarSlots - getLeftExtraHotbarSlots(totalExtraHotbarSlots);
    };

    /**
     * Where the extra non-Hotbar Slots of the Extended Inventory are.
     * @return {@code true} on left, {@code false} on right
     */
    public static final boolean isExtraInventoryOnLeft() {
        return PetrolparkConfigs.client().extraInventoryLeft.get();
    };

    /**
     * The number of Slots wide non-hotbar section of the Extended Inventory is.
     */
    public static final int getExtraInventoryWidth() {
        return PetrolparkConfigs.client().extraInventoryWidth.get();
    };

    /**
     * How the additional Hotbar Slots should be arranged according to the Player's configs.
     */
    public static final ExtraHotbarSlotLocations getExtraHotbarSlotLocations() {
        return PetrolparkConfigs.client().extraHotbarSlotLocations.get();
    };

    /**
     * @see PetrolparkClientConfig#extraHotbarPrioritySlotCount
     */
    public static final int getExtraHotbarPrioritySlots() {
        return PetrolparkConfigs.client().extraHotbarPrioritySlotCount.get();
    };

    /**
     * @see PetrolparkClientConfig#extraInventory
     */
    public ExtraInventoryClientSettings getExtraInventoryClientSettings() {
        return new ExtraInventoryClientSettings(isExtraInventoryOnLeft(), getExtraInventoryWidth(), getExtraHotbarSlotLocations(), getExtraHotbarPrioritySlots());
    };

    /**
     * The location of the very top left of the "window" for Extended Inventory Hotbar Slots on the left.
     * @return {@code null} if there are no Hotbar Slots rendered on the left
     */
    public static final Rect2i getLeftHotbarLocation(ExtendedInventory inv, Rect2i screenArea, int hotbarY) {
        int slots = getLeftExtraHotbarSlots(inv.getExtraHotbarSlots());
        if (slots == 0) return null;
        return getHotbarLocation(screenArea, hotbarY, - INVENTORY_SPACING - 2 * INVENTORY_PADDING - slots * 18, slots);
    };

    /**
     * The location of the very top left of the "window" for Extended Inventory Hotbar Slots on the right.
     * @return {@code null} if there are no Hotbar Slots rendered on the right
     */
    public static final Rect2i getRightHotbarLocation(ExtendedInventory inv, Rect2i screenArea, int hotbarY) {
        int slots = getRightExtraHotbarSlots(inv.getExtraHotbarSlots());
        if (slots == 0) return null;
        return getHotbarLocation(screenArea, hotbarY, screenArea.getWidth() + INVENTORY_SPACING, slots);
    };

    /**
     * The location of an Extended Inventory Hotbar "window" with padding applied.
     */
    protected static final Rect2i getHotbarLocation(Rect2i screenArea, int hotbarY, int xOffset, int slots) {
        return new Rect2i(
            screenArea.getX() + xOffset,
            hotbarY - INVENTORY_PADDING,
            2 * INVENTORY_PADDING + slots * 18,
            2 * INVENTORY_PADDING + 18
        );
    };

    /**
     * The location of a combined "window" for both the main Extended Inventory Slots and the Hotbar Slots on the same side.
     * @return {@code null} if the main Extended Inventory section is too short to merge with the Hotbar "window", or if there is no Hotbar "window" on the side of the main Inventory.
     */
    public static final Rect2i getCombinedInventoryHotbarLocation(ExtendedInventory inv, Rect2i screenArea, int hotbarY) {
        boolean left = isExtraInventoryOnLeft();
        int inventorySlots = inv.extraItems.size() - inv.getExtraHotbarSlots();
        int inventoryWidth = getExtraInventoryWidth();
        int inventoryHeight = inventorySlots / inventoryWidth;
        if (inventorySlots % inventoryWidth > 0) inventoryHeight++;
        int hotbarSlots = left ? getLeftExtraHotbarSlots(inv.getExtraHotbarSlots()) : getRightExtraHotbarSlots(inv.getExtraHotbarSlots());
        int width = Math.max(inventoryWidth, hotbarSlots);
        if (hotbarSlots > 0 && inventoryHeight >= 3) {
            return new Rect2i(
                screenArea.getX() + (left ? -INVENTORY_SPACING - 2 * INVENTORY_PADDING - width * 18 : INVENTORY_SPACING + screenArea.getWidth()),
                hotbarY - INVENTORY_HOTBAR_SPACING - 18 * inventoryHeight - INVENTORY_PADDING,
                2 * INVENTORY_PADDING + width * 18,
                18 * (inventoryHeight + 1) + 2 * INVENTORY_PADDING + INVENTORY_HOTBAR_SPACING);
        } else {
            return null;
        }
    };

    /**
     * The location of the "window" for the Extended Inventory Slots that are not on the hotbar.
     * @return {@code null} if there are no Slots of that description
     */
    public static final Rect2i getInventoryLocation(ExtendedInventory inv, Rect2i screenArea, int hotbarY) {
        int inventorySlots = inv.extraItems.size() - inv.getExtraHotbarSlots();
        if (inventorySlots <= 0) return null;
        int inventoryWidth = getExtraInventoryWidth();
        int inventoryHeight = inventorySlots / inventoryWidth;
        if (inventorySlots % inventoryWidth > 0) inventoryHeight++;
        return new Rect2i(
            screenArea.getX() + (isExtraInventoryOnLeft() ? - INVENTORY_SPACING - 2 * INVENTORY_PADDING - inventoryWidth * 18: INVENTORY_SPACING + screenArea.getWidth()),
            hotbarY - INVENTORY_HOTBAR_SPACING - INVENTORY_PADDING - 18 * Math.max(3, inventoryHeight),
            2 * INVENTORY_PADDING + inventoryWidth * 18,
            2 * INVENTORY_PADDING + 18 * inventoryHeight
        );
    };

    /**
     * Get the maximum space the given Screen (without the Extended Inventory "windows" added on) occupies, accounting for any sticky-outy bits.
     */
    public static final Rect2i getScreenArea(AbstractContainerScreen<?> screen) {
        Rect2i area = new Rect2i(0, 0, screen.getXSize(), screen.getYSize());
        if (screen instanceof AbstractSimiContainerScreen<?> simiScreen) {
            for (Rect2i extraArea : simiScreen.getExtraAreas()) {
                area.setX(Math.min(area.getX(), extraArea.getX()));
                //area.setY(Math.min(area.getY(), extraArea.getY()));
                area.setWidth(Math.max(area.getWidth(), extraArea.getX() - area.getX() + extraArea.getWidth()));
                //area.setHeight(Math.max(area.getHeight(), extraArea.getY() + extraArea.getHeight()));
            };
        };
        return area;
    };

    /**
     * The Screen currently being rendered with extra Slots, or {@code null} if no Screen showing extra Slots is being rendered.
     */
    public AbstractContainerScreen<?> currentScreen = null;
    /**
     * @see ExtendedInventoryClientHandler#getLeftHotbarLocation(ExtendedInventory, Rect2i, int)
     */
    private Rect2i leftHotbar = null;
    /**
     * @see ExtendedInventoryClientHandler#getRightHotbarLocation(ExtendedInventory, Rect2i, int)
     */
    private Rect2i rightHotbar = null;
    /**
     * @see ExtendedInventoryClientHandler#getCombinedInventoryHotbarLocation(ExtendedInventory, Rect2i, int)
     */
    private Rect2i combinedInventoryHotbar = null;
    /**
     * @see ExtendedInventoryClientHandler#getInventoryLocation(ExtendedInventory, Rect2i, int)
     */
    private Rect2i inventory = null;
    /**
     * Parts of the screen that the Extended Inventory "windows" cover, so JEI knows not to render anything there.
     */
    private List<Rect2i> extraGuiAreas = Collections.emptyList();

    /**
     * Refresh the locations of the "windows" for the Extended Inventory Slots on the {@link ExtendedInventoryClientHandler#currentScreen current Screen}.
     */
    public void refreshExtraInventoryAreas(ExtendedInventory inv) {
        if (currentScreen == null) return;

        boolean mainInventoryLeft = isExtraInventoryOnLeft();

        if (currentScreen instanceof IExtendedInventoryScreen customScreen && !customScreen.customExtendedInventoryRendering()) {
            int leftHotbarSlots = getLeftExtraHotbarSlots(inv.getExtraHotbarSlots());
            leftHotbar = customScreen.getLeftHotbarLocation(inv, leftHotbarSlots, mainInventoryLeft);
            rightHotbar = customScreen.getRightHotbarLocation(inv, leftHotbarSlots, mainInventoryLeft);
            combinedInventoryHotbar = customScreen.getCombinedInventoryHotbarLocation(inv, leftHotbarSlots, mainInventoryLeft);
            inventory = customScreen.getInventoryLocation(inv, leftHotbarSlots, mainInventoryLeft);
            extraGuiAreas = customScreen.getExtendedInventoryGuiAreas(inv);
        } else {
            Rect2i screenArea = getScreenArea(currentScreen);
            int hotbarY = findHotbarY(currentScreen);
            leftHotbar = getLeftHotbarLocation(inv, screenArea, hotbarY);
            rightHotbar  = getRightHotbarLocation(inv, screenArea, hotbarY);
            combinedInventoryHotbar = getCombinedInventoryHotbarLocation(inv, screenArea, hotbarY);
            inventory = getInventoryLocation(inv, screenArea, hotbarY);
            extraGuiAreas = new ArrayList<>(3);
            if (combinedInventoryHotbar == null) {
                if (inventory != null) extraGuiAreas.add(offset(inventory, currentScreen.getGuiLeft(), currentScreen.getGuiTop()));
                if (leftHotbar != null) extraGuiAreas.add(offset(leftHotbar, currentScreen.getGuiLeft(), currentScreen.getGuiTop()));
                if (rightHotbar != null) extraGuiAreas.add(offset(rightHotbar, currentScreen.getGuiLeft(), currentScreen.getGuiTop()));
            } else {
                extraGuiAreas.add(offset(combinedInventoryHotbar, currentScreen.getGuiLeft(), currentScreen.getGuiTop()));
                Rect2i renderedHotbar = mainInventoryLeft ? rightHotbar : leftHotbar;
                if (renderedHotbar != null) extraGuiAreas.add(offset(renderedHotbar, currentScreen.getGuiLeft(), currentScreen.getGuiTop()));
            };
        };
    };

    /**
     * Search the Slots of the given Screen to find where the (non-Extended) Inventory's Hotbar is rendered.
     */
    public static int findHotbarY(AbstractContainerScreen<?> screen) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        int hotbarY = screen.height - 22;
        if (player == null) return hotbarY;
        for (Slot slot : screen.getMenu().slots) {
            if (slot.container == player.getInventory() && Inventory.isHotbarSlot(slot.getSlotIndex())) return slot.y;
        };
        return hotbarY;
    };

    /**
     * The leftmost point of any Extended Inventory "windows".
     */
    public int getLeftmostX() {
        if (isExtraInventoryOnLeft()) {
            if (combinedInventoryHotbar != null) return combinedInventoryHotbar.getX();
            return Math.min(leftHotbar == null ? 0 : leftHotbar.getX(), inventory == null ? 0 : inventory.getX());
        };
        return leftHotbar == null ? 0 : leftHotbar.getX();
    };

    /**
     * Add Slots corresponding to those of the Extended Inventory to a Menu, on the client side.
     * Unlike with {@link ExtendedInventory#refreshPlayerInventoryMenuServer(Player)}, the location is important.
     * @param inv
     * @param menu
     * @see ExtendedInventoryClientHandler#addSlotsToClientMenu(ExtendedInventory, Consumer, SlotFactory) Instantiating Slots in a special way (e.g. for the {@link CreativeModeInventoryScreen}).
     */
    public void addSlotsToClientMenu(ExtendedInventory inv, AbstractContainerMenu menu) {
        addSlotsToClientMenu(inv, menu::addSlot, Slot::new);
    };

    /**
     * Add Slots corresponding to those of the Extended Inventory to a Menu, on the client side.
     * Unlike with {@link ExtendedInventory#refreshPlayerInventoryMenuServer(Player)}, the location is important.
     * @param inv
     * @param slotAdder
     * @param slotFactory
     * @see ExtendedInventoryClientHandler#addSlotsToClientMenu(ExtendedInventory, AbstractContainerMenu) Instantiating Slots normally
     */
    public void addSlotsToClientMenu(ExtendedInventory inv, Consumer<Slot> slotAdder, SlotFactory slotFactory) {
        Rect2i inventoryRect = combinedInventoryHotbar == null ? inventory : combinedInventoryHotbar;
        int invX;
        int invY;
        if (inventoryRect == null) {
            invX = 0;
            invY = 0;
        } else {
            invX = inventoryRect.getX();
            invY = inventoryRect.getY();
        };
        int leftX;
        int leftY;
        if (leftHotbar != null) {
            leftX = leftHotbar.getX() + INVENTORY_PADDING;
            leftY = leftHotbar.getY() + INVENTORY_PADDING;
        } else {
            leftX = 0;
            leftY = 0;
        };
        int rightX;
        int rightY;
        if (rightHotbar != null) {
            rightX = rightHotbar.getX() + INVENTORY_PADDING;
            rightY = rightHotbar.getY() + INVENTORY_PADDING;
        } else {
            rightX = 0;
            rightY = 0;
        };
        inv.addExtraInventorySlotsToMenu(slotAdder, slotFactory, getExtraInventoryWidth(), invX + INVENTORY_PADDING, invY + INVENTORY_PADDING, getLeftExtraHotbarSlots(inv.getExtraHotbarSlots()), leftX, leftY, rightX, rightY);
    };

    /**
     * Before opening a Screen, add the additional Slots (if they are not already added).
     * @param event
     */
    @SubscribeEvent
    public void onScreenInitPost(ScreenEvent.Init.Post event) {
        if (!ExtendedInventory.enabled()) return;
        if (!(event.getScreen() instanceof AbstractContainerScreen screen)) {
            currentScreen = null;
            return;
        };
        AbstractContainerMenu menu = screen.getMenu();
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        if (menu == player.inventoryMenu) CatnipServices.NETWORK.sendToServer(RequestInventoryFullStatePacket.INSTANCE); // Just in case

        if (!ExtendedInventory.supportsExtraInventory(menu) && !(menu == player.inventoryMenu || screen instanceof CreativeModeInventoryScreen)) {
            currentScreen = null;
            return;
        };

        Optional<ExtendedInventory> invOp = ExtendedInventory.get(mc.player);
        if (invOp.isEmpty()) return;
        ExtendedInventory inv = invOp.get();

        if (screen != currentScreen && !(screen instanceof InventoryScreen && currentScreen instanceof CreativeModeInventoryScreen)) { // Don't override Creative Mode Screen with the Inventory Screen that also gets opened
            currentScreen = screen;
            refreshExtraInventoryAreas(inv);
        };

        if (!(
            menu == player.inventoryMenu // Survival Inventory Menu Slots are added in a Player mixin
            || screen instanceof CreativeModeInventoryScreen // Creative Inventory Menu Slots are added in a CreativeModeInventoryScreen mixin
            || menu instanceof IExtendedInventoryMenu // Custom Extended Inventory Menu screens add the Slots themselves
        )) {
            addSlotsToClientMenu(inv, menu);
            ((DelayedSlotPopulation)menu).populateDelayedSlots(); // Client recieves the stacks to fill too early. The mixin stores them and we put them back in their proper place here.
        };
    };

    /**
     * Render the "window" backgrounds and Slot backgrounds of Extended Inventory Slots.
     * @param event
     */
    @SubscribeEvent
    public void onScreenRenderPre(ScreenEvent.Render.Pre event) {
        if (!ExtendedInventory.enabled() || !(event.getScreen() instanceof AbstractContainerScreen screen) || screen != currentScreen) return;
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        if (!(
            screen.getMenu() == player.inventoryMenu // Render in the survival Inventory even though the Slots are not added to it in the usual way
            || ExtendedInventory.supportsExtraInventory(screen.getMenu()) // Render in menus to which Slots are added in the usual way
            || (screen instanceof CreativeModeInventoryScreen && CreativeModeInventoryScreen.selectedTab.getType() == CreativeModeTab.Type.INVENTORY) // Render in the Survival Inventory Tab of the Creative Menu even though the Slots are not added to it in the usual way
        ) || (
            screen instanceof IExtendedInventoryScreen customScreen && customScreen.customExtendedInventoryRendering() // Don't render in Screens which do it themselves
        )) return;

        Optional<ExtendedInventory> invOp = ExtendedInventory.get(player);
        if (invOp.isEmpty());
        ExtendedInventory inv = invOp.get();
        boolean left = isExtraInventoryOnLeft();
        int leftHotbarSlots = getLeftExtraHotbarSlots(inv.getExtraHotbarSlots());
        int columns = getExtraInventoryWidth();
        GuiGraphics graphics = event.getGuiGraphics();
        PoseStack ms = graphics.pose();

        RenderSystem.enableDepthTest();
        ms.pushPose(); {
            ms.translate(screen.getGuiLeft(), screen.getGuiTop(), 2f);
            ms.pushPose();
            ms.translate(-1f, -1f, 0f);
            if (combinedInventoryHotbar != null) {
                PetrolparkNineSlices.INVENTORY_BACKGROUND.render(graphics, combinedInventoryHotbar);
                Rect2i renderedHotbar = left ? rightHotbar : leftHotbar;
                if (renderedHotbar != null) PetrolparkNineSlices.INVENTORY_BACKGROUND.render(graphics, renderedHotbar);
            } else {
                if (inventory != null) PetrolparkNineSlices.INVENTORY_BACKGROUND.render(graphics, inventory);
                if (leftHotbar != null) PetrolparkNineSlices.INVENTORY_BACKGROUND.render(graphics, leftHotbar);
                if (rightHotbar != null) PetrolparkNineSlices.INVENTORY_BACKGROUND.render(graphics, rightHotbar);
            };
            ms.popPose();
            // Render left Hotbar Slot backgrounds
            if (leftHotbar != null) for (int i = 0; i < leftHotbarSlots; i++) {
                PetrolparkGuiTexture.INVENTORY_SLOT.render(graphics, leftHotbar.getX() + INVENTORY_PADDING - 1 + i * 18, leftHotbar.getY() + INVENTORY_PADDING - 1);
            };
            // Render right Hotbar Slot backgrounds
            int j = 0;
            if (rightHotbar != null) for (int i = leftHotbarSlots; i < inv.getExtraHotbarSlots(); i++) {
                PetrolparkGuiTexture.INVENTORY_SLOT.render(graphics, rightHotbar.getX() + INVENTORY_PADDING - 1 + j * 18, rightHotbar.getY() + INVENTORY_PADDING - 1);
                j++;
            };
            // Render main extra Inventory Slot backgrounds
            j = 0;
            Rect2i invRect = combinedInventoryHotbar == null ? inventory : combinedInventoryHotbar;
            if (invRect != null) for (int i = inv.getExtraHotbarSlots(); i < inv.extraItems.size(); i++) {
                PetrolparkGuiTexture.INVENTORY_SLOT.render(graphics, invRect.getX() + INVENTORY_PADDING - 1 + 18 * (j % columns), invRect.getY() + INVENTORY_PADDING - 1 + (j / columns) * 18);
                j++;
            };
        }; ms.popPose();
    };

    /**
     * Upon closing a Screen, forget which Screen should have extra Slots rendered for it.
     * @param event
     */
    @SubscribeEvent
    public void onScreenClosing(ScreenEvent.Closing event) {
        currentScreen = null;
    };

    /**
     * Render the borders for the Extended Inventory Slots on the Hotbar.
     */
    public static void renderExtraHotbarBackground(GuiGraphics graphics, DeltaTracker deltaTracker) {
        if (!ExtendedInventory.enabled()) return;
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        MultiPlayerGameMode gameMode = mc.gameMode;
		if (player == null || mc.options.hideGui || gameMode == null || gameMode.getPlayerMode() == GameType.SPECTATOR) return;

        PoseStack ms = graphics.pose();
        Optional<ExtendedInventory> invOp = ExtendedInventory.get(player);
        if (invOp.isEmpty()) return;
        ExtendedInventory inv = invOp.get();
        int extraSlots = inv.getExtraHotbarSlots();

		int y = graphics.guiHeight() - 22;
        
        RenderSystem.enableDepthTest();

        for (boolean right : Iterate.trueAndFalse) {
            int x = graphics.guiWidth() / 2 - 91;
            int slotCount;
            if (right) {
                slotCount = getRightExtraHotbarSlots(extraSlots);
                x += 9 * 20;
            } else {
                slotCount = getLeftExtraHotbarSlots(extraSlots);
                x -= slotCount * 20;
            };

            ms.pushPose();
            if (slotCount > 0 ) PetrolparkNineSlices.HOTBAR.render(graphics, x, y, 2 + slotCount * 20, 22);
            ms.popPose();
        };

    };

    /**
     * Render the Slot icons and actual Items in the Extended Inventory Hotbar Slots.
     * Also render the selected Slot (again).
     */
    public static void renderExtraHotbar(GuiGraphics graphics, DeltaTracker deltaTracker) {
        if (!ExtendedInventory.enabled()) return;
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        MultiPlayerGameMode gameMode = mc.gameMode;
		if (player == null || mc.options.hideGui || gameMode == null || gameMode.getPlayerMode() == GameType.SPECTATOR) return;

        PoseStack ms = graphics.pose();
        Optional<ExtendedInventory> invOp = ExtendedInventory.get(player);
        if (invOp.isEmpty()) return;
        ExtendedInventory inv = invOp.get();
        int extraSlots = inv.getExtraHotbarSlots();

		int y = graphics.guiHeight() - 21;

		RenderSystem.enableDepthTest();

        int item = 0;
        for (boolean right : Iterate.trueAndFalse) {
            int slotCount;
            int x = graphics.guiWidth() / 2 - 90;
            if (right) {
                slotCount = getRightExtraHotbarSlots(extraSlots);
                x += 9 * 20;
            } else {
                slotCount = getLeftExtraHotbarSlots(extraSlots);
                x -= slotCount * 20;
            };
            if (slotCount == 0) continue;

            ms.pushPose();
            for (int i = 0; i < slotCount; i++) {
                PetrolparkGuiTexture.HOTBAR_SLOT.render(graphics, x + i * 20, y);
                mc.gui.renderSlot(graphics, 2 + x + i * 20, y + 2, deltaTracker, player, inv.extraItems.get(item), 42069);
                item++;
            };
            ms.popPose();
        };

        ms.pushPose();

        int selected = inv.getSelectedHotbarIndex();
        int selectedX = graphics.guiWidth() / 2 - 92;
        if (inv.getSelectedHotbarIndex() >= Inventory.getSelectionSize() + getRightExtraHotbarSlots(extraSlots)) { // If a left extra Slot is selected
            selectedX -= getLeftExtraHotbarSlots(extraSlots) * 20;
            selected -= (Inventory.getSelectionSize() + getRightExtraHotbarSlots(extraSlots));
        };
        selectedX += selected * 20;

        graphics.blitSprite(Gui.HOTBAR_SELECTION_SPRITE, selectedX, y - 2, 24, 23);
        
        ms.popPose();
    };

    /**
     * The spaces on the screen taken up by the Extended Inventory "windows".
     * JEI needs to know these. We also use them to move the Recipe Book out of the way.
     */
    public List<Rect2i> getGuiExtraAreas() {
        if (currentScreen == null) return Collections.emptyList();
        return extraGuiAreas;
    };

    /**
     * Displace a {@link Rect2i}.
     * @param rect
     * @param x
     * @param y
     */
    private static Rect2i offset(Rect2i rect, int x, int y) {
        return new Rect2i(rect.getX() + x, rect.getY() + y, rect.getWidth(), rect.getHeight());
    };

    @EventBusSubscriber(value = Dist.CLIENT, modid = Petrolpark.MOD_ID)
    public static class ModBusEvents {

        /**
         * Register the renderers for the longer Hotbar.
         * @param event
         */
        @SubscribeEvent
        public static void registerGuiOverlays(RegisterGuiLayersEvent event) {
            event.registerBelow(VanillaGuiLayers.HOTBAR, Petrolpark.asResource("extra_hotbar_background"), ExtendedInventoryClientHandler::renderExtraHotbarBackground);
            event.registerAbove(VanillaGuiLayers.HOTBAR, Petrolpark.asResource("extra_hotbar"), ExtendedInventoryClientHandler::renderExtraHotbar);
        };
    };

    /**
     * @see PetrolparkClientConfig#extraHotbarSlotLocations
     */
    public static enum ExtraHotbarSlotLocations {
        ALL_LEFT,
        ALL_RIGHT,
        START_LEFT,
        START_RIGHT,
        PRIORITY_LEFT,
        PRIORITY_RIGHT;
    };

    public record ExtraInventoryClientSettings(boolean left, int width, ExtraHotbarSlotLocations loc, int hotbarCount) {};
};
