package com.petrolpark.core.extendedinventory;

import java.util.List;

import org.jetbrains.annotations.ApiStatus;

import com.petrolpark.config.PetrolparkConfigs;

import net.minecraft.client.renderer.Rect2i;

/**
 * Screens which have special support for rendering the {@link ExtendedInventory}.
 * The Menu associated with this Screen should implement {@link IExtendedInventoryMenu}.
 */
@ApiStatus.Experimental
public interface IExtendedInventoryScreen {
    
    /**
     * Whether the rendering of the {@link ExtendedInventory} is entirely overridden.
     * If this returns {@code false}, the backgrounds and Slot backgrounds will be rendered as usual (e.g. as in vanilla Chests).
     * This method should be effectively static.
     * @return {@code true} if you handle rendering all Extended Inventory things in the appropriate render method of this Screen
     */
    public default boolean customExtendedInventoryRendering() {
        return false;
    };

    /**
     * Get the location of the of the "window" which shows the left-hand-side hotbar Slots of the Extended Inventory, including the border around the Slots.
     * This will be called if {@link IExtendedInventoryScreen#customExtendedInventoryRendering()} returns {@code false}.
     * This should match the specifications of {@link ExtendedInventoryClientHandler#getLeftHotbarLocation(ExtendedInventory, Rect2i, int) this method}.
     * This method gets called when the Screen opens, or whenever {@link ExtendedInventoryClientHandler#refreshClientInventoryMenu(ExtendedInventory)} gets called, so call that if the location of the Extended Inventory Slots should change (you may also have to change the locations of the Slots in the Menu attached to this Screen).
     * @param extendedInventory
     * @param leftHotbarSlots The number of hotbar Slots to render to the left of the vanilla hotbar, according to the {@link PetrolparkConfigs Player's configurations}.
     * @param renderMainInventoryLeft Whether to render the Inventory Slots not in the hotbar to the left of the vanilla Inventory
     * @return An area in which to render the background of this section of the Extended Inventory, or {@code null} if it should not be rendered
     */
    public Rect2i getLeftHotbarLocation(ExtendedInventory extendedInventory, int leftHotbarSlots, boolean renderMainInventoryLeft);

    /**
     * Get the location of the of the "window" which shows the right-hand-side hotbar Slots of the Extended Inventory, including the border around the Slots.
     * This will be called if {@link IExtendedInventoryScreen#customExtendedInventoryRendering()} returns {@code false}.
     * This should match the specifications of {@link ExtendedInventoryClientHandler#getRightHotbarLocation(ExtendedInventory, Rect2i, int) this method}.
     * This method gets called when the Screen opens, or whenever {@link ExtendedInventoryClientHandler#refreshClientInventoryMenu(ExtendedInventory)} gets called, so call that if the location of the Extended Inventory Slots should change (you may also have to change the locations of the Slots in the Menu attached to this Screen).
     * @param extendedInventory
     * @param leftHotbarSlots The number of hotbar Slots to render to the left of the vanilla hotbar, according to the {@link PetrolparkConfigs Player's configurations}.
     * @param renderMainInventoryLeft Whether to render the Inventory Slots not in the hotbar to the left of the vanilla Inventory
     * @return An area in which to render the background of this section of the Extended Inventory, or {@code null} if it should not be rendered
     */
    public Rect2i getRightHotbarLocation(ExtendedInventory extendedInventory, int leftHotbarSlots, boolean renderMainInventoryLeft);

    /**
     * Get the location of the of the "window" which shows the Slots of the Extended Inventory not on the hotbar, including the border around the Slots.
     * This will be called if {@link IExtendedInventoryScreen#customExtendedInventoryRendering()} returns {@code false}.
     * This should match the specifications of {@link ExtendedInventoryClientHandler#getInventoryLocation(ExtendedInventory, Rect2i, int) this method}.
     * This method gets called when the Screen opens, or whenever {@link ExtendedInventoryClientHandler#refreshClientInventoryMenu(ExtendedInventory)} gets called, so call that if the location of the Extended Inventory Slots should change (you may also have to change the locations of the Slots in the Menu attached to this Screen).
     * @param extendedInventory
     * @param leftHotbarSlots The number of hotbar Slots to render to the left of the vanilla hotbar, according to the {@link PetrolparkConfigs Player's configurations}.
     * @param renderMainInventoryLeft Whether to render the Inventory Slots not in the hotbar to the left of the vanilla Inventory
     * @return An area in which to render the background of this section of the Extended Inventory, or {@code null} if it should not be rendered
     */
    public Rect2i getInventoryLocation(ExtendedInventory extendedInventory, int leftHotbarSlots, boolean renderMainInventoryLeft);

    /**
     * Get the location of the of the "window" which shows the combined Slots of appropriate side of the hotbar and the non-hotbar Slots of the Extended Inventory, including the border around the Slots.
     * This will be called if {@link IExtendedInventoryScreen#customExtendedInventoryRendering()} returns {@code false}.
     * This should match the specifications of {@link ExtendedInventoryClientHandler#getCombinedInventoryHotbarLocation(ExtendedInventory, Rect2i, int) this method}.
     * This method gets called when the Screen opens, or whenever {@link ExtendedInventoryClientHandler#refreshClientInventoryMenu(ExtendedInventory)} gets called, so call that if the location of the Extended Inventory Slots should change (you may also have to change the locations of the Slots in the Menu attached to this Screen).
     * @param extendedInventory
     * @param leftHotbarSlots The number of hotbar Slots to render to the left of the vanilla hotbar, according to the {@link DestroyClientConfigs Player's configurations}.
     * @param renderMainInventoryLeft Whether to render the Inventory Slots not in the hotbar to the left of the vanilla Inventory
     * @return An area in which to render the background of this section of the Extended Inventory, or {@code null} if it should not be rendered
     */
    public Rect2i getCombinedInventoryHotbarLocation(ExtendedInventory extendedInventory, int leftHotbarSlots, boolean renderMainInventoryLeft);

    /**
     * Get any portion of the screen obscured by the Extended Inventory being rendered, so JEI will not render anything there.
     * It is acceptable for this to be empty if the JEI extra GUI areas are handled in another way.
     */
    public List<Rect2i> getExtendedInventoryGuiAreas(ExtendedInventory extendedInventory);
};
