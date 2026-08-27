package petrolpark.mc.library.core.world.item.crafting.pocket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.lwjgl.glfw.GLFW;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.world.item.crafting.pocket.PocketCrafting.SlotGrid;
import petrolpark.mc.library.core.world.item.crafting.pocket.crafter.CraftingPocketCrafter;
import petrolpark.mc.library.core.world.item.crafting.pocket.crafter.IPocketCrafter;
import petrolpark.mc.library.core.world.item.crafting.pocket.interpretedSlot.IInterpretedSlot;

public class PocketCraftingClientHandler {

    public static final ResourceLocation INPUT_BORDER = Petrolpark.asResource("textures/gui/sprites/pocket_crafting/input_border.png");
    public static final ResourceLocation INPUT_SLOT = Petrolpark.asResource("pocket_crafting/input_slot");
    public static final ResourceLocation TOOLTIP_BACKGROUND = Petrolpark.asResource("pocket_crafting/tooltip_background");

    protected final Minecraft mc;

    protected final Int2ObjectMap<Int2ObjectMap<Slot>> menuSlots = new Int2ObjectArrayMap<>();
    protected final Int2ObjectMap<SlotGrid> menuGrids = new Int2ObjectArrayMap<>();
    protected @Nullable IPocketCrafter<?> activeCrafter = null;

    protected @Nullable Slot hoveredSlot = null;
    protected @Nullable Slot dragStartSlot = null;
    protected @Nullable Slot dragEndSlot = null;

    protected final Set<Slot> existingInputSlots = new ObjectOpenCustomHashSet<>(SLOT_HASH_STRATEGY);
    protected final Set<Slot> inputSlotsToModify = new ObjectOpenCustomHashSet<>(SLOT_HASH_STRATEGY);
    protected boolean removeInputSlotsToModify = false;

    protected final Int2ObjectMap<IInterpretedSlot> interpretedSlots = new Int2ObjectArrayMap<>();

    protected final List<BorderTexturePlacement> inputBorderTexturePlacements = new ArrayList<>();
    protected final Set<Slot> allInputSlots = new ObjectOpenCustomHashSet<>(SLOT_HASH_STRATEGY);

    protected int outputTooltipX, outputTooltipY;

    public PocketCraftingClientHandler(Minecraft mc) {
        this.mc = mc;
        menuSlots.defaultReturnValue(Int2ObjectMaps.emptyMap());
    };

    public void cancel() {
        activeCrafter = null;

        hoveredSlot = null;
        dragStartSlot = null;
        dragEndSlot = null;

        existingInputSlots.clear();
        inputSlotsToModify.clear();

        interpretedSlots.clear();
        inputSlotsToModify.clear();

        inputBorderTexturePlacements.clear();
        allInputSlots.clear();
    };

    @SubscribeEvent
    public void onOpenScreen(ScreenEvent.Opening event) {
        calculateMenuGrids(event.getScreen());
    };

    @SubscribeEvent
    public void onRenderScreenBackground(ContainerScreenEvent.Render.Background event) {
        if (activeCrafter == null) return;

        // ACTUALLY RENDER

        event.getGuiGraphics().pose().pushPose();
        event.getGuiGraphics().pose().translate(event.getContainerScreen().getGuiLeft(), event.getContainerScreen().getGuiTop(), 0f);
        for (Slot slot : allInputSlots) {
            event.getGuiGraphics().blitSprite(INPUT_SLOT, slot.x - 1, slot.y - 1, 18, 18);
        };
        event.getGuiGraphics().pose().popPose();

        // UPDATE FIELDS

        // Set hovered Slot
        final Slot hoveredSlot = event.getContainerScreen().findSlot(event.getMouseX(), event.getMouseY());
        this.hoveredSlot = hoveredSlot;
        if (hoveredSlot == null) return;

        // Set last dragged Slot
        final Slot dragStartSlot = this.dragStartSlot;
        if (dragStartSlot != null && dragEndSlot != hoveredSlot) { // If dragging
            final SlotGrid dragGrid = menuGrids.get(dragStartSlot.index);
            if (dragGrid != null && menuGrids.get(hoveredSlot.index) == dragGrid) {
                dragEndSlot = hoveredSlot;
                calculateInputSlots();
            };
        };
    };

    @SubscribeEvent
    public void onRenderTooltip(RenderTooltipEvent.Color event) {
        if (activeCrafter == null) return;

        outputTooltipX = event.getX();
        outputTooltipY = event.getY();
    };

    @SubscribeEvent
    public void onRenderScreenForeground(ContainerScreenEvent.Render.Foreground event) {
        if (activeCrafter == null) return;

        // ACTUAL RENDERING

        // Highlight currently dragged Slots
        for (Slot slot : inputSlotsToModify) {
            event.getContainerScreen().renderSlotHighlight(event.getGuiGraphics(), slot, event.getMouseX(), event.getMouseY(), AnimationTickHolder.getPartialTicksUI());
        };

        // Borders around input
        for (BorderTexturePlacement placement : inputBorderTexturePlacements) {
            event.getGuiGraphics().blit(
                Petrolpark.asResource("textures/gui/sprites/pocket_crafting/input_border.png"),
                placement.x() - 1, placement.y() - 1,
                0,
                (float)placement.uIndex() * 18f, (float)placement.vIndex() * 18f,
                18, 18,
                72, 72
            );
        };

        // Output
        event.getGuiGraphics().pose().pushPose();
        event.getGuiGraphics().pose().translate(-event.getContainerScreen().getGuiLeft(), -event.getContainerScreen().getGuiTop(), 0f);
        event.getGuiGraphics().blitSprite(TOOLTIP_BACKGROUND, outputTooltipX, outputTooltipY, 48, 48);
        event.getGuiGraphics().pose().popPose();

        // UPDATE FIELDS

        // Set default output tooltip location
        outputTooltipX = event.getMouseX();
        outputTooltipY = event.getMouseY();
    };

    @SubscribeEvent
    public void onMousePressed(ScreenEvent.MouseButtonPressed.Pre event) {
        if (activeCrafter == null) return;
        
        // Left-click: begin input selection
        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (hoveredSlot != null && dragStartSlot != hoveredSlot) {
                dragStartSlot = hoveredSlot;
                // Remove input Slots
                if (existingInputSlots.contains(dragStartSlot)) {
                    removeInputSlotsToModify = true;
                // Add input Slots
                } else {
                    removeInputSlotsToModify = false;
                };
            };
        // Right click: select output
        } else if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            // final Slot hoveredSlot = this.hoveredSlot;
            // if (hoveredSlot != null) {
            //     if (outputSlots.contains(hoveredSlot)) {
            //         outputSlots.remove(hoveredSlot);
            //     } else {
            //         outputSlots.add(hoveredSlot);
            //     };
            // };
        };

        event.setCanceled(true);
    };

    @SubscribeEvent
    public void onMouseReleased(ScreenEvent.MouseButtonReleased.Pre event) {
        if (activeCrafter == null) return;

        // Left click: complete input selection
        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (removeInputSlotsToModify) {
                existingInputSlots.removeAll(inputSlotsToModify);
            } else {
                existingInputSlots.addAll(inputSlotsToModify);
            };
            inputSlotsToModify.clear();
            dragStartSlot = null;
            dragEndSlot = null;
        };

        event.setCanceled(true);
    };

    @SubscribeEvent
    public void onKeyPressed(ScreenEvent.KeyPressed.Pre event) {

        // TEMP
        if (event.getKeyCode() == GLFW.GLFW_KEY_C) {
            activeCrafter = new CraftingPocketCrafter();
        };


        if (activeCrafter == null) return;
        if (event.getKeyCode() == GLFW.GLFW_KEY_ESCAPE)
            activeCrafter = null;
        event.setCanceled(true);
    };

    @SubscribeEvent
    public void onCloseScreen(ScreenEvent.Closing event) {
        cancel();
    };

    public void calculateInputSlots() {
        final Slot dragStartSlot = this.dragStartSlot;
        final Slot dragEndSlot = this.dragEndSlot;
        if (dragStartSlot == null || dragEndSlot == null) return; // Should never fail

        final Slot topLeftSlot = menuSlots.get(Math.min(dragStartSlot.y, dragEndSlot.y)).get(Math.min(dragStartSlot.x, dragEndSlot.x));
        final Slot bottomRightSlot = menuSlots.get(Math.max(dragStartSlot.y, dragEndSlot.y)).get(Math.max(dragStartSlot.x, dragEndSlot.x));

        if (topLeftSlot == null || bottomRightSlot == null) return; // Should also never fail

        inputSlotsToModify.clear();
        Slot rightSlot = topLeftSlot;
        findAll: while (rightSlot != null && rightSlot.x <= bottomRightSlot.x) {
            inputSlotsToModify.add(rightSlot);
            if (rightSlot == bottomRightSlot) break findAll;

            Slot belowSlot = menuSlots.get(rightSlot.y + 18).get(rightSlot.x);
            while (belowSlot != null && belowSlot.y <= bottomRightSlot.y) {
                inputSlotsToModify.add(belowSlot);
                if (belowSlot == bottomRightSlot) break findAll;
                belowSlot = menuSlots.get(belowSlot.y + 18).get(belowSlot.x);
            };

            rightSlot = menuSlots.get(rightSlot.y).get(rightSlot.x + 18);
        };

        allInputSlots.clear();
        allInputSlots.addAll(existingInputSlots);
        if (removeInputSlotsToModify) {
            allInputSlots.removeAll(inputSlotsToModify);
        } else {
            allInputSlots.addAll(inputSlotsToModify);
        };

        calculateInputSlotsToRender();
    };

    public void calculateInputSlotsToRender() {
        inputBorderTexturePlacements.clear();
        inputBorderTexturePlacements.addAll(getBorderTexturePlacements(allInputSlots));
    };

    public void calculateMenuGrids(Screen screen) {
        if (!(screen instanceof AbstractContainerScreen containerScreen)) return;
        final AbstractContainerMenu menu = containerScreen.getMenu();
        if (menu == null) return;

        // Sort slots by their positions
        final Pair<Int2ObjectMap<Int2ObjectMap<Slot>>, Int2ObjectMap<SlotGrid>> slotsAndGrids = PocketCrafting.organiseSlots(menu.slots);
        
        menuSlots.clear();
        menuSlots.putAll(slotsAndGrids.getFirst());
        menuGrids.clear();
        menuGrids.putAll(slotsAndGrids.getSecond());
    };

    public List<BorderTexturePlacement> getBorderTexturePlacements(Collection<Slot> slots) {

        class Border {
            boolean topLeft, topRight, bottomLeft, bottomRight;
        };

        final Int2ObjectMap<Int2ObjectMap<Border>> borders = new Int2ObjectArrayMap<>();
        for (Slot slot : slots) {
            borders
                .computeIfAbsent(slot.y + 9, $ -> new Int2ObjectArrayMap<>())
                .computeIfAbsent(slot.x + 9, $ -> new Border())
                .topLeft = true;
            borders
                .computeIfAbsent(slot.y + 9, $ -> new Int2ObjectArrayMap<>())
                .computeIfAbsent(slot.x - 9, $ -> new Border())
                .topRight = true;
            borders
                .computeIfAbsent(slot.y - 9, $ -> new Int2ObjectArrayMap<>())
                .computeIfAbsent(slot.x + 9, $ -> new Border())
                .bottomLeft = true;
            borders
                .computeIfAbsent(slot.y - 9, $ -> new Int2ObjectArrayMap<>())
                .computeIfAbsent(slot.x - 9, $ -> new Border())
                .bottomRight = true;
        };

        final List<BorderTexturePlacement> textures = new ArrayList<>();

        for (Int2ObjectMap.Entry<Int2ObjectMap<Border>> row : borders.int2ObjectEntrySet()) {
            final int y = row.getIntKey();
            for (Int2ObjectMap.Entry<Border> entry : row.getValue().int2ObjectEntrySet()) {
                final Border border = entry.getValue();
                textures.add(new BorderTexturePlacement(
                    entry.getIntKey(), y,
                    (border.bottomLeft ? 2 : 0) + (border.bottomRight ? 1 : 0),
                    (border.topLeft ? 2 : 0) + (border.topRight ? 1 : 0)
                ));
            };
        };
        
        return textures;
    };

    public record BorderTexturePlacement(int x, int y, int uIndex, int vIndex) {

    };

    public static final Hash.Strategy<Slot> SLOT_HASH_STRATEGY = new Hash.Strategy<>() {

        @Override
        public int hashCode(Slot o) {
            return o.index;
        };

        @Override
        public boolean equals(Slot a, Slot b) {
            if (a == null) return b == null;
            if (b == null)
                return false;
            return a.index == b.index;
        };
        
    };
};
