package com.petrolpark.mixin.client;

import java.util.Optional;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.PetrolparkClient;
import com.petrolpark.client.creativemodetab.CustomTab;
import com.petrolpark.client.creativemodetab.CustomTab.ITabEntry;
import com.petrolpark.core.extendedinventory.ExtendedInventory;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen.ItemPickerMenu;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin extends EffectRenderingInventoryScreen<ItemPickerMenu> {
    
    public CreativeModeInventoryScreenMixin(ItemPickerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        throw new AssertionError(); // Should never be called
    };

    /**
     * Render fancy things in {@link CustomTab Custom Tabs}.
     */
    @Inject(
        method = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
        at = @At("RETURN")
    )
    public void inRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (getSelectedTab() instanceof CustomTab tab) {
            int offset = getMenu().getRowIndexForScroll(getScrollOffs());
            PoseStack ms = graphics.pose();
            ms.pushPose();
            ms.translate(getGuiLeft() + 8f, getGuiTop() + 17f, 0f);
            for (int y = 0; y < 5; y++) {
                for (int x = 0; x < 9; x++) {
                    ITabEntry entry = tab.renderedEntries.get(9 * (y + offset) + x);
                    if (entry != null) {
                        ms.pushPose();
                        ms.translate(x * 18f, y * 18f, 0f);
                        entry.render(graphics, mouseX, mouseY, partialTick);
                        ms.popPose();
                    };
                };
            };
            ms.popPose();
        };
    };

    @Shadow
    private Slot destroyItemSlot;

    /**
     * Don't add Extended Inventory Slots to the Survival Inventory section of the Creative Inventory in the normal way.
     */
    @WrapOperation(
        method = "selectTab",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/core/NonNullList;size()I"
        )
    )
    public int wrapSlotListSize(NonNullList<Slot> slots, Operation<Integer> original) {
        return 46;
    };


    /**
     * Add Extended Inventory Slots to the Survival Inventory section of the Creative Inventory.
     */
    @Inject(
        method = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen;selectTab(Lnet/minecraft/world/item/CreativeModeTab;)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen;destroyItemSlot:Lnet/minecraft/world/inventory/Slot;",
            ordinal = 0
        )
    )
    public void inSelectTab(CreativeModeTab tab, CallbackInfo ci) {
        Minecraft mc = getMinecraft();
        if (mc == null) return;
        LocalPlayer player = mc.player;
        if (player == null) return;
        Optional<ExtendedInventory> invOp = ExtendedInventory.get(player);
        if (invOp.isEmpty()) return;

        ExtendedInventory inv = invOp.get();
        Int2ObjectMap<Slot> extendedInventorySlots = new Int2ObjectArrayMap<>(); // Map Inventory indices to Slots in the Inventory Menu, as the index of the Slot in the menu is not guaranteed to match the index in the Inventory the Slot encapsulates
        for (Slot slot : player.inventoryMenu.slots) {
            if (slot.getSlotIndex() >= inv.getExtraInventoryStartSlotIndex()) extendedInventorySlots.put(slot.getSlotIndex(), slot);
        };
        PetrolparkClient.EXTENDED_INVENTORY_HANDLER.currentScreen = this;
        PetrolparkClient.EXTENDED_INVENTORY_HANDLER.refreshExtraInventoryAreas(inv);
        PetrolparkClient.EXTENDED_INVENTORY_HANDLER.addSlotsToClientMenu(inv, menu::addSlot, (c, i, x, y) -> new CreativeModeInventoryScreen.SlotWrapper(extendedInventorySlots.get(i), i, x, y));
    };

    /**
     * Allow adding Items to Extended Inventory Slots
     * @param slot
     * @param slotId
     * @param mouseButton
     * @param type
     * @param ci
     */
    @Inject(
        method = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen;slotClicked",
        at = @At("HEAD")
    )
    protected void inSlotClicked(@Nullable Slot slot, int slotId, int mouseButton, ClickType type, CallbackInfo ci) {
        Minecraft mc = getMinecraft();
        if (mc == null) return;
        LocalPlayer player = mc.player;
        MultiPlayerGameMode gameMode = mc.gameMode;
        if (player == null || gameMode == null) return;
        Optional<ExtendedInventory> invOp = ExtendedInventory.get(player);

        if (invOp.isPresent() && slot == destroyItemSlot && type == ClickType.QUICK_MOVE) {
            ExtendedInventory inv = invOp.get();
            for (Slot inventorySlot : menu.slots) {
                if (inventorySlot.getSlotIndex() >= inv.getExtraInventoryStartSlotIndex()) gameMode.handleCreativeModeItemAdd(ItemStack.EMPTY, inventorySlot.index);
            };
        };
    };

    @Accessor("selectedTab")
    public static CreativeModeTab getSelectedTab() {
        throw new AssertionError();
    };

    @Accessor("scrollOffs")
    public abstract float getScrollOffs();
};
