package petrolpark.mc.library.core.world.item.crafting.pocket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.lwjgl.glfw.GLFW;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.client.rendering.PetrolparkGuiTexture;
import petrolpark.mc.library.core.world.item.crafting.pocket.PocketCrafting.SlotGrid;
import petrolpark.mc.library.core.world.item.crafting.pocket.crafter.IPocketCrafter;
import petrolpark.mc.library.core.world.item.crafting.pocket.interpretedSlot.IInterpretedSlot;
import petrolpark.mc.library.registry.PetrolparkPocketCrafters;

public class PocketCraftingClientHandler {

    public static final ResourceLocation INPUT_BORDER = Petrolpark.asResource("textures/gui/sprites/pocket_crafting/input_border.png");
    public static final ResourceLocation INPUT_SLOT = Petrolpark.asResource("pocket_crafting/input_slot");
    public static final ResourceLocation TOOLTIP_BACKGROUND = Petrolpark.asResource("pocket_crafting/tooltip_background");
    public static final ResourceLocation ARROWS = Petrolpark.asResource("pocket_crafting/arrow");

    protected final Minecraft mc;

    protected final Int2ObjectMap<Int2ObjectMap<Slot>> menuSlots = new Int2ObjectArrayMap<>();
    protected final Int2ObjectMap<SlotGrid> menuGrids = new Int2ObjectArrayMap<>();

    // SLOT SELECTION

    protected @Nullable Slot hoveredSlot = null;
    protected @Nullable Slot dragStartSlot = null;
    protected @Nullable Slot dragEndSlot = null;

    protected final Set<Slot> existingInputSlots = new ObjectOpenCustomHashSet<>(PocketCrafting.SLOT_HASH_STRATEGY);
    protected final Set<Slot> inputSlotsToModify = new ObjectOpenCustomHashSet<>(PocketCrafting.SLOT_HASH_STRATEGY);
    protected boolean removeInputSlotsToModify = false;
    protected final Set<Slot> allInputSlots = new ObjectOpenCustomHashSet<>(PocketCrafting.SLOT_HASH_STRATEGY);

    protected final Int2ObjectMap<List<IInterpretedSlot<?>>> slotInterpretations = new Int2ObjectArrayMap<>();
    protected final Int2IntMap selectedSlotInterpretations = new Int2IntArrayMap();

    // RECIPE

    protected @Nullable ActiveCrafter<?> activeCrafter = null;
    protected ItemStack toolStack = ItemStack.EMPTY;
    protected int selectedRecipe = 0;
    protected @Nullable PocketCrafting.Result craftingResult = null;

    // RENDERING

    protected final List<BorderTexturePlacement> inputBorderTexturePlacements = new ArrayList<>();

    protected int outputTooltipX, outputTooltipY;
    protected final List<Component> tooltipLines = new ArrayList<>();

    public PocketCraftingClientHandler(Minecraft mc) {
        this.mc = mc;
        menuSlots.defaultReturnValue(Int2ObjectMaps.emptyMap());
        slotInterpretations.defaultReturnValue(Collections.emptyList());
    };

    public void cancel() {
        activeCrafter = null;
        craftingResult = null;

        hoveredSlot = null;
        dragStartSlot = null;
        dragEndSlot = null;

        existingInputSlots.clear();
        inputSlotsToModify.clear();

        slotInterpretations.clear();
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
        if (!Objects.equals(hoveredSlot, this.hoveredSlot)) {
            this.hoveredSlot = hoveredSlot;
            calculateTooltipLines();
        };
        if (hoveredSlot == null) return;

        // Set last dragged Slot
        final Slot dragStartSlot = this.dragStartSlot;
        if (dragStartSlot != null && dragEndSlot != hoveredSlot) { // If dragging
            final SlotGrid dragGrid = menuGrids.get(PocketCrafting.getIndex(dragStartSlot));
            if (dragGrid != null && menuGrids.get(PocketCrafting.getIndex(hoveredSlot)) == dragGrid) {
                dragEndSlot = hoveredSlot;
                calculateInputSlots(event.getContainerScreen());
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
        event.getGuiGraphics().pose().translate(-event.getContainerScreen().getGuiLeft() + outputTooltipX - 6, -event.getContainerScreen().getGuiTop() + outputTooltipY - 22, 400f);
        int height = 16;
        int width = 10;
        for (Component line : tooltipLines.reversed()) {
            width = Math.max(width, event.getGuiGraphics().drawString(mc.font, line, 6, 0, 0xFFFFFF) + 8);
            event.getGuiGraphics().pose().translate(0f, -mc.font.lineHeight, 0f);
            height += mc.font.lineHeight;
        };
        final PocketCrafting.Result result = craftingResult;
        if (result != null) {
            final List<? extends PocketCrafting.Output> outputs = result.outputs();
            if (outputs.size() > 0) {
                event.getGuiGraphics().pose().translate(0f, -17f, 100f);
                (result.successful() ? PetrolparkGuiTexture.RECIPE_ARROW : PetrolparkGuiTexture.RECIPE_ARROW_FAIL).render(event.getGuiGraphics(), 5, 7);
                event.getGuiGraphics().pose().pushPose();
                event.getGuiGraphics().pose().translate(24f, 7f, 0f);
                for (PocketCrafting.Output output : outputs) {
                    output.render(event.getGuiGraphics());
                    event.getGuiGraphics().pose().translate(18f, 0f, 0f);  
                };
                event.getGuiGraphics().pose().popPose();
                height += 18;
            };
        };
        event.getGuiGraphics().pose().translate(0f, 0f, -100f);
        event.getGuiGraphics().fill(2, 5, width - 2, height - 5, 0xF0262503);
        
        event.getGuiGraphics().blitSprite(TOOLTIP_BACKGROUND, 0, 0, width, height);
        event.getGuiGraphics().pose().popPose();

        // UPDATE FIELDS

        // Set default output tooltip location
        outputTooltipX = event.getMouseX() + 12;
        outputTooltipY = event.getMouseY() + 32;
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
        final ActiveCrafter<?> activeCrafter = this.activeCrafter;
        if (activeCrafter == null || !(event.getScreen() instanceof AbstractContainerScreen screen)) return;

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
            calculateTooltipLines();
        };

        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT && craftingResult != null && craftingResult.successful()) {
            final ClientLevel level = mc.level;
            final LocalPlayer player = mc.player;
            if (level != null && player != null) activeCrafter.sendPacket(new IPocketCraftingContext.Client.Impl(level, player, screen.getMenu(), toolStack));
        };

        event.setCanceled(true);
    };

    @SubscribeEvent
    public void onKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        if (!(event.getScreen() instanceof AbstractContainerScreen)) return;

        // TEMP
        if (event.getKeyCode() == GLFW.GLFW_KEY_C) {
            activeCrafter = new ActiveCrafter<>(PetrolparkPocketCrafters.SMELTING.get());
        };

        if (activeCrafter == null) return;
        if (event.getKeyCode() == GLFW.GLFW_KEY_ESCAPE) {
            activeCrafter = null;
            craftingResult = null;
        };
            
        event.setCanceled(true);
    };

    @SubscribeEvent
    public void onCloseScreen(ScreenEvent.Closing event) {
        cancel();
    };

    public void calculateTooltipLines() {
        tooltipLines.clear();
        final ActiveCrafter<?> activeCrafter = this.activeCrafter;
        if (activeCrafter == null) return;

        if (!allInputSlots.isEmpty() && activeCrafter.recipes.isEmpty())
            tooltipLines.add(Component.translatable(Petrolpark.translationKey("pocketCrafting.tooltip.noRecipe")).withStyle(ChatFormatting.RED));
        
        if (craftingResult != null)
            craftingResult.addToTooltip(tooltipLines::add);

        if (dragStartSlot == null ? hoveredSlot != null && existingInputSlots.contains(hoveredSlot) : removeInputSlotsToModify)
            tooltipLines.add(Component.translatable(Petrolpark.translationKey("pocketCrafting.control.deselectInput")).withStyle(ChatFormatting.GRAY));
        else
            tooltipLines.add(Component.translatable(Petrolpark.translationKey("pocketCrafting.control.selectInput")).withStyle(ChatFormatting.GRAY));

        if (craftingResult != null && craftingResult.successful())
            tooltipLines.add(Component.translatable(Petrolpark.translationKey("pocketCrafting.control.craft")).withStyle(ChatFormatting.GRAY));

        tooltipLines.add(Component.translatable(Petrolpark.translationKey("pocketCrafting.control.cancel")).withStyle(ChatFormatting.GRAY));
    };

    public void calculateRecipes(AbstractContainerScreen<?> screen) {
        final ActiveCrafter<?> activeCrafter = this.activeCrafter;
        if (activeCrafter == null) return;
        final ClientLevel level = mc.level;
        if (level == null) return;
        final LocalPlayer player = mc.player;
        if (player == null) return;

        activeCrafter.calculateRecipes(
            new IPocketCraftingContext.Client.Impl(level, player, screen.getMenu(), toolStack),
            allInputSlots.stream()
                .mapToInt(PocketCrafting::getIndex)
                .<IInterpretedSlot<?>>mapToObj(index -> slotInterpretations.get(index).get(selectedSlotInterpretations.get(index)))
                .filter(Predicate.not(Objects::isNull)) // Shouldn't really be null
                .toList()
        );

        calculateTooltipLines();
    };

    public void calculateInputSlots(AbstractContainerScreen<?> screen) {
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

        final ActiveCrafter<?> activeCrafter = this.activeCrafter;
        final ClientLevel level = mc.level;
        final LocalPlayer player = mc.player;
        if (activeCrafter != null && level != null && player != null) {
            final IPocketCraftingContext context = new IPocketCraftingContext.Client.Impl(level, player, screen.getMenu(), toolStack);
            for (Slot slot : allInputSlots) {
                calculateSlotInterpretation(context, activeCrafter.crafter, slot);
            };
        };

        calculateRecipes(screen);
        calculateInputSlotsToRender();
    };

    public void calculateSlotInterpretation(@Nonnull IPocketCraftingContext context, @Nonnull IPocketCrafter<?> crafter, @Nonnull Slot slot) {
        final int slotIndex = PocketCrafting.getIndex(slot);
        final List<IInterpretedSlot<?>> previousInterpretations = slotInterpretations.get(slotIndex);
        final int previousInterpretationIndex = selectedSlotInterpretations.get(slotIndex);
        final List<IInterpretedSlot<?>> interpretations = crafter.getSlotInterpretations(context, slot);
        slotInterpretations.put(slotIndex, interpretations);
        selectedSlotInterpretations.put(slotIndex, 0);
        if (!previousInterpretations.isEmpty() && previousInterpretationIndex >= 0 && previousInterpretationIndex < previousInterpretations.size()) {
            final IInterpretedSlot<?> previousInterpretation = previousInterpretations.get(previousInterpretationIndex);
            final int interpretationIndex = interpretations.indexOf(previousInterpretation);
            selectedSlotInterpretations.put(slotIndex, interpretationIndex != -1 ? interpretationIndex : 0);
        };
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
        final Pair<Int2ObjectMap<Int2ObjectMap<Slot>>, Int2ObjectMap<SlotGrid>> slotsAndGrids = PocketCrafting.organiseSlots(menu.slots, PocketCrafting.SlotArrangement.NONE);
        
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

    public class ActiveCrafter<R extends Recipe<?>> {
    
        protected final IPocketCrafter<R> crafter;
        protected final List<RecipeHolder<? extends R>> recipes = new ArrayList<>();

        public ActiveCrafter(IPocketCrafter<R> crafter) {
            this.crafter = crafter;
        };

        public void calculateRecipes(IPocketCraftingContext.Client context, List<IInterpretedSlot<?>> slots) {
            final RecipeHolder<? extends R> previousRecipe = selectedRecipe >= 0 && selectedRecipe < recipes.size() ? recipes.get(selectedRecipe) : null;
            recipes.clear();
            recipes.addAll(crafter.getRecipes(context, slots, (x, y) -> menuSlots.get(y).get(x)));
            final int newRecipeIndex = recipes.indexOf(previousRecipe);
            if (newRecipeIndex != -1) {
                selectedRecipe = newRecipeIndex;
            } else {
                selectedRecipe = 0;
                craftingResult = recipes.isEmpty() ? null : crafter.craft(context, true, recipes.get(0), slots, null);
            };
        };

        public void sendPacket(IPocketCraftingContext.Client context) {
            if (selectedRecipe < 0 || selectedRecipe >= recipes.size()) return;
            crafter.sendPacket(
                context,
                recipes.get(selectedRecipe),
                selectedSlotInterpretations.int2IntEntrySet().stream().map(entry -> new PocketCraftPacket.SlotAndInterpretation(entry.getIntKey(), entry.getIntValue())).toList(),
                Optional.ofNullable(hoveredSlot).map(PocketCrafting::getIndex)
            );
        };
    };

    public record BorderTexturePlacement(int x, int y, int uIndex, int vIndex) {

    };
};
