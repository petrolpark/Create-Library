package petrolpark.mc.library.core.world.item.deletable;

import com.google.common.base.Strings;
import com.mojang.blaze3d.systems.RenderSystem;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.platform.CatnipClientServices;
import net.createmod.catnip.theme.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import petrolpark.mc.library.registry.PetrolparkKeyBinds;
import petrolpark.mc.library.util.ItemHelper;
import petrolpark.mc.library.util.Lang;

public class DeletableItemClientHandler {

	private static final Color RED = new Color(0xFF0050, false).setImmutable();
    
    private double mouseX, mouseY;
    private LerpedFloat holdKeyProgress = LerpedFloat.linear().startWithValue(0);
    private ItemStack hoveredStack = ItemStack.EMPTY;
    private int hoveredSlot = -1;

    protected boolean deferTick = true;

    @SubscribeEvent
    public void onTick(ClientTickEvent.Pre event) {
        deferTick = true;
    };

    public void deferredTick() {
		deferTick = false;
		final Minecraft mc = Minecraft.getInstance();
		final Screen currentScreen = mc.screen;

		if (hoveredStack.isEmpty() || hoveredSlot == -1) {
			hoveredStack = ItemStack.EMPTY;
			holdKeyProgress.startWithValue(0);
			return;
		};

		final float value = holdKeyProgress.getValue();

		if (RenderSystem.isOnRenderThread() && PetrolparkKeyBinds.DELETE_ITEM.isDown() && currentScreen != null) {
			if (value >= 1) {
				CatnipClientServices.NETWORK.sendToServer(new DeleteItemPacket(hoveredSlot));
				return;
			};
			holdKeyProgress.setValue(Math.min(1, value + Math.max(.25f, value) * .25f));
		} else
			holdKeyProgress.setValue(Math.max(0, value - .05f));
	};

    @SubscribeEvent
    public void addToTooltip(ItemTooltipEvent event) {
		updateHovered(event.getItemStack());

		if (deferTick) deferredTick();

		if (hoveredStack != event.getItemStack()) return;

		final float renderPartialTicks = AnimationTickHolder.getPartialTicksUI();
		final Component component = makeProgressBar(Math.min(1, holdKeyProgress.getValue(renderPartialTicks) * 8 / 7f));
		if (event.getToolTip().size() < 2)
			event.getToolTip().add(component);
		else
			event.getToolTip().add(1, component);
	};

    @SubscribeEvent
    public void onRenderScreen(ScreenEvent.Render.Post event) {
        mouseX = event.getMouseX();
        mouseY = event.getMouseY();
    };

    protected void updateHovered(ItemStack stack) {
		final Minecraft mc = Minecraft.getInstance();

		final ItemStack prevStack = hoveredStack;
		hoveredStack = ItemStack.EMPTY;
        hoveredSlot = -1;

		if (stack.isEmpty()) return;
		if (!(stack.getItem() instanceof IDeletableItem)) return;
        if (!(mc.screen instanceof AbstractContainerScreen screen) || screen instanceof CreativeModeInventoryScreen) return;
        final Slot slot = screen.findSlot(mouseX, mouseY);
        if (slot == null) return;
		final Player player = mc.player;
		if (player == null) return;

		if (prevStack.isEmpty() || !prevStack.is(stack.getItem()))
            holdKeyProgress.startWithValue(0);

		hoveredStack = stack;
        hoveredSlot = ItemHelper.getActualIndex(player.containerMenu, slot);
	};

    @SubscribeEvent
	public void onRenderTooltipColor(RenderTooltipEvent.Color event) {
		if (hoveredStack != event.getItemStack()) return;
		if (holdKeyProgress.getValue() == 0) return;

		final float renderPartialTicks = AnimationTickHolder.getPartialTicksUI();

		float progress = Math.min(1, holdKeyProgress.getValue(renderPartialTicks) * 8 / 7f);

		event.setBorderStart(getSmoothColorForProgress(new Color(event.getBorderStart()), progress).getRGB());
		event.setBorderEnd(getSmoothColorForProgress(new Color(event.getBorderEnd()), progress).getRGB());
	};

	private static Color getSmoothColorForProgress(Color initialColor, float progress) {
		return initialColor.mixWith(RED, progress);
	};

    private static Component makeProgressBar(float progress) {
		final MutableComponent holdW = Lang.builder()
			.translate("gui.deleteItem",
				PetrolparkKeyBinds.DELETE_ITEM.keybind.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.GRAY))
			.style(ChatFormatting.DARK_GRAY)
			.component();

		final Font font = Minecraft.getInstance().font;
		final float charWidth = font.width("|");
		final float tipWidth = font.width(holdW);

		final int total = (int) (tipWidth / charWidth);
		final int current = (int) (progress * total);

		if (progress > 0) {
			String bars = "";
			bars += ChatFormatting.GRAY + Strings.repeat("|", current);
			if (progress < 1)
				bars += ChatFormatting.DARK_GRAY + Strings.repeat("|", total - current);
			return Component.literal(bars);
		};

		return holdW;
	};
};
