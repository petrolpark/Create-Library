package petrolpark.mc.library.core.world.restaurant.gui;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.lwjgl.glfw.GLFW;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.animation.LerpedFloat.Chaser;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import petrolpark.mc.library.core.client.rendering.PetrolparkGuiTexture;

@ParametersAreNonnullByDefault
public abstract class RestaurantScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    protected static final int LINE_HEIGHT = 10;
    protected static final double LINE_WRITING_SPEED = 0.2d;

    protected static final int INVENTORY_Y = 66;

    protected static final int NOTE_X = 14;
    protected static final int NOTE_FIRST_LINE_Y = PetrolparkGuiTexture.RESTAURANT_NOTE_TOP.height;
    protected static final int NOTE_BOTTOM_PADDING = LINE_HEIGHT / 2;
    protected static final float NOTE_END_LINES = PetrolparkGuiTexture.RESTAURANT_NOTE_BOTTOM.height / (float)LINE_HEIGHT;
    protected static final int NOTE_LINE_OVERHANG = 2;
    protected static final int NOTE_TEXT_X = 22;
    protected static final int NOTE_TEXT_Y = 1;
    protected static final int NOTE_TEXT_WIDTH = 121;
    protected static final int NOTE_TEXT_COLOR = 0x3A322A;

    protected final LerpedFloat lineWriting = LerpedFloat.linear().startWithValue(0d);

    protected final LerpedFloat scroll = LerpedFloat.linear().startWithValue(0d);
    protected boolean scrollLocked = true;

    public RestaurantScreen(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    };

    protected abstract List<Component> getLines();

    protected abstract boolean shouldRenderInventory();

    public abstract Optional<HoveredItemStack> getNoteStackUnderMouse(Vec2 mouse);

    @Override
    protected void init() {
        super.init();
        final float end = getLines().size() + NOTE_END_LINES;
        if (shouldRenderInventory()) {
            lineWriting.chase(end, LINE_WRITING_SPEED, Chaser.LINEAR);
            scroll.chase(end, LINE_WRITING_SPEED, Chaser.LINEAR);
        } else if (scrollLocked) {
            final float shown = Math.min(end, getVisibleLines());
            lineWriting.startWithValue(end).chase(end, LINE_WRITING_SPEED, Chaser.LINEAR);
            scroll.startWithValue(shown).chase(shown, 1d, Chaser.LINEAR);
            scrollLocked = false;
        };
    };

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        final boolean consumed = super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        if (consumed) return true;

        final double target = Mth.clamp(scroll.getChaseTarget() - scrollY, 0d, lineWriting.getValue());
        scroll.chase(target, 1d, Chaser.LINEAR);
        scrollLocked = !lineWriting.settled() && target >= lineWriting.getValue();

        return true;
    };

    @Override
    protected void containerTick() {
        super.containerTick();

        if (scrollLocked)
            scroll.startWithValue(lineWriting.getValue()).chase(lineWriting.getChaseTarget(), LINE_WRITING_SPEED, Chaser.LINEAR);

        lineWriting.tickChaser();
        scroll.tickChaser();
    };

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && !lineWriting.settled()) {
            final double end = lineWriting.getChaseTarget();
            lineWriting.startWithValue(end); // Not setValue, so the note does not lerp to its full length over the following tick
            if (scrollLocked) scroll.startWithValue(end);
            return true;
        };
        return super.keyPressed(keyCode, scanCode, modifiers);
    };

    protected float getVisibleLines() {
        return Math.max(0f, (getNoteBottom() - getNoteTop() - NOTE_BOTTOM_PADDING - NOTE_FIRST_LINE_Y) / (float)LINE_HEIGHT);
    };

    protected int getNoteTop() {
        return -getGuiTop();
    };

    protected int getNoteBottom() {
        return shouldRenderInventory() ? INVENTORY_Y : height - getGuiTop();
    };

    protected static int getLineY(int line) {
        return NOTE_FIRST_LINE_Y + line * LINE_HEIGHT;
    };

    protected float getNoteY(float partialTick) {
        return (float)(getNoteBottom() - NOTE_BOTTOM_PADDING - NOTE_FIRST_LINE_Y - scroll.getValue(partialTick) * LINE_HEIGHT);
    };

    @Nullable
    protected Vec2 getMouseOnNote(double mouseX, double mouseY) {
        final double x = mouseX - getGuiLeft() - NOTE_X;
        if (x < 0 || x >= PetrolparkGuiTexture.RESTAURANT_NOTE_TOP.width) return null;

        final double y = mouseY - getGuiTop();
        if (y < getNoteTop() || y >= getNoteBottom()) return null;

        return new Vec2((int)x, (int)(y - getNoteY(AnimationTickHolder.getPartialTicks())));
    };

    protected boolean isMouseOverIcon(Vec2 mouseOnNote, int line, int x, int y) {
        if (lineWriting.getValue() <= line) return false; // Nothing by this line has been written yet, so there is nothing to hover over

        final int iconY = getLineY(line) + y;
        return mouseOnNote.x >= x && mouseOnNote.x < x + 16 && mouseOnNote.y >= iconY && mouseOnNote.y < iconY + 16;
    };

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    };

    /**
     * Overriding methds must pop pose!
     */
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(getGuiLeft(), getGuiTop(), 0f);

        renderNote(guiGraphics, partialTick);

        if (shouldRenderInventory()) {
            PetrolparkGuiTexture.RESTAURANT_INVENTORY.render(guiGraphics, 0, INVENTORY_Y);
        };
    };

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (shouldRenderInventory()) guiGraphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 4210752, false);
    };

    protected void renderNote(GuiGraphics guiGraphics, float partialTick) {
        final List<Component> lines = getLines();
        if (lines.isEmpty()) return;

        final double written = lineWriting.getValue(partialTick);

        final int top = getNoteTop();
        final int cutOff = getNoteBottom();
        if (cutOff <= top) return;

        final float noteY = getNoteY(partialTick);
        final float from = top - noteY;
        final float to = cutOff - noteY;

        guiGraphics.enableScissor(getGuiLeft() + NOTE_X, getGuiTop() + top, getGuiLeft() + NOTE_X + PetrolparkGuiTexture.RESTAURANT_NOTE_TOP.width, getGuiTop() + cutOff);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(NOTE_X, noteY, 0f);

        // Paper
        final PetrolparkGuiTexture tile = PetrolparkGuiTexture.RESTAURANT_NOTE_MIDDLE;
        final int ruled = Mth.floor(Math.min(written, lines.size()) * LINE_HEIGHT); // Height of the part of the paper which has been unrolled, which stops at the last line even though the writing carries on past it
        PetrolparkGuiTexture.RESTAURANT_NOTE_TOP.render(guiGraphics, 0, 0);
        for (int t = Math.max(0, Mth.floor((from - NOTE_FIRST_LINE_Y) / tile.height)); t * tile.height < ruled; t++) {
            final int y = NOTE_FIRST_LINE_Y + t * tile.height;
            if (y > to) break; // Below the visible area
            // The last tile is only as tall as the line written on it has been unrolled, so it has no rule until it is complete
            guiGraphics.blit(tile.location, 0, y, tile.startX, tile.startY, tile.width, Math.min(tile.height, ruled - t * tile.height), tile.textureWidth, tile.textureHeight);
        };
        PetrolparkGuiTexture.RESTAURANT_NOTE_BOTTOM.render(guiGraphics, 0, NOTE_FIRST_LINE_Y + ruled);

        // Lines
        // Anything rendered by a line may hang below it, so lines above the visible area may still have something to show in it
        final float linesFrom = from - (NOTE_LINE_OVERHANG + 1) * LINE_HEIGHT;
        for (int line = 0; line < lines.size(); line++) {
            final double progress = written - line;
            if (progress <= 0d) break; // Neither this line nor any after it has been written yet

            final int y = getLineY(line);
            if (y + LINE_HEIGHT < linesFrom) continue; // Above the visible area
            if (y > to) break; // Below the visible area

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0f, y, 0f);
            renderByLine(guiGraphics, partialTick, line);
            guiGraphics.pose().popPose();

            guiGraphics.drawString(font, getWrittenText(lines.get(line), progress), NOTE_TEXT_X, y + NOTE_TEXT_Y, NOTE_TEXT_COLOR, false);
        };

        guiGraphics.pose().popPose();
        guiGraphics.disableScissor();
    };

    /**
     * Renders anything which belongs by a line of the note, such as in its margin.
     * The {@link GuiGraphics} is translated to the top left corner of the line, and everything rendered is cut off with the rest of the note, so it scrolls with it and hides behind the inventory.
     * Anything rendered may be up to {@code NOTE_LINE_OVERHANG + 1} lines high.
     * @param line Index of the line in {@link #getLines()}, which has at least started being written
     */
    protected void renderByLine(GuiGraphics guiGraphics, float partialTick, int line) {};

    /**
     * The part of a line which has been written.
     * @param progress {@code 0} for none of the line, {@code 1} for all of it
     */
    protected static FormattedCharSequence getWrittenText(Component line, double progress) {
        if (progress >= 1d) return line.getVisualOrderText();

        final String string = line.getString();
        final int[] remaining = new int[]{Mth.floor(progress * string.codePointCount(0, string.length()))};
        if (remaining[0] <= 0) return FormattedCharSequence.EMPTY;

        final MutableComponent written = Component.empty();
        final FormattedText.StyledContentConsumer<Boolean> writer = (style, content) -> {
            final int length = content.codePointCount(0, content.length());
            if (length >= remaining[0]) {
                written.append(Component.literal(content.substring(0, content.offsetByCodePoints(0, remaining[0]))).withStyle(style));
                return Optional.of(true); // Stop writing
            };
            written.append(Component.literal(content).withStyle(style));
            remaining[0] -= length;
            return Optional.empty();
        };
        line.visit(writer, Style.EMPTY);

        return Language.getInstance().getVisualOrder(written);
    };

    public record HoveredItemStack(int x, int y, ItemStack stack) {};
};
