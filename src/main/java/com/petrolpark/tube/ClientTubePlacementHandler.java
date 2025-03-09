package com.petrolpark.tube;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.RequiresCreate;
import com.petrolpark.client.key.PetrolparkKeys;
import com.petrolpark.compat.create.CreateClient;
import com.petrolpark.network.PetrolparkMessages;
import com.petrolpark.util.BlockFace;
import com.petrolpark.util.Lang;
import com.petrolpark.util.Pair;
import com.petrolpark.util.RayHelper;
import com.petrolpark.util.RayHelper.CustomHitResult;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.foundation.gui.RemovedGuiUtils;

import net.createmod.catnip.theme.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;

@OnlyIn(Dist.CLIENT)
@RequiresCreate
public class ClientTubePlacementHandler {
    
    protected static ItemStack currentStack = ItemStack.EMPTY;
    protected static ITubeBlock tubeBlock = null;
    protected static BlockFace start = null;
    protected static BlockFace end = null;
    protected static TubeSpline spline = null;

    protected static int ttl = 0;
    protected static int targetedControlPoint = -1;
    protected static double distanceToSelectedControlPoint = 0f;
    protected static boolean draggingSelectedControlPoint = false;
    protected static List<AABB> controlPointBoxes = new ArrayList<>();
    protected static boolean canAfford = true;

    @SubscribeEvent
    public static void tick(ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || !(mc.player.getMainHandItem() == currentStack || AllItems.WRENCH.isIn(mc.player.getMainHandItem())) || currentStack.isEmpty() || (start != null && mc.level.getBlockState(start.getPos()).getBlock() != tubeBlock)) {
            cancel();
            return;
        };
        if (ttl > 0) {
            ttl--;
        } else {
            cancel();
        };
        if (!active()) return;
        // Check end blocks are still present
        if (mc.level.getBlockState(end.getPos()).getBlock() != tubeBlock) {
            cancel();
            return;
        };

        boolean buildable = spline.getResult().success;
        int color = buildable ? 0xFF_4DE680 : 0xFF_E64D80;

        // Render path of spline
        for (int i = 0; i < spline.getPoints().size() - 1; i++) {
            Vec3 point = spline.getPoints().get(i);
            Vec3 tangent = spline.getTangents().get(i);
            CreateClient.OUTLINER.showLine(Pair.of(point, tangent), point, spline.getPoints().get(i+1)).colored(color);
        };

        // Render blocking Blocks and decide if the Tube is being blocked
        spline.checkBlocked(mc.level, pos -> CreateClient.OUTLINER.chaseAABB(Pair.of("blocking_tube", pos), new AABB(pos)).colored(0xFF_E64D80));

        // Check there are enough Items
        canAfford = spline.checkCanAfford(mc.player, currentStack.getItem(), tubeBlock);

        if (controlPointBoxes.isEmpty()) controlPointBoxes = spline.getControlPoints().stream().map(v -> new AABB(v.subtract(3 / 32d, 3 / 32d, 3 / 32d), v.add(3 / 32d, 3 / 32d, 3 / 32d))).toList();

        // Locate targeted Control Point, or move it if there already is one
        if (!draggingSelectedControlPoint && !controlPointBoxes.isEmpty()) {
            if (RayHelper.getHitResult(controlPointBoxes, mc.player, AnimationTickHolder.getPartialTicks(), false) instanceof CustomHitResult hr) targetedControlPoint = hr.index; else targetedControlPoint = -1;
        } else {
            if (targetedControlPoint != -1) relocateControlPoint(); // The check to see its not -1 should be redundant
        };

        // Render Control Points
        for (int i = 0; i < controlPointBoxes.size(); i++) {
            AABB controlPointBox = controlPointBoxes.get(i);
            CreateClient.OUTLINER.showBox(Pair.of("control_point", i), controlPointBox.inflate(i == targetedControlPoint ? 1 / 16d : 0d), 2).colored(color);
        };

        // Show message
        mc.player.displayClientMessage(spline.result.translate(currentStack), true);
    };

    public static final IGuiOverlay OVERLAY = ClientTubePlacementHandler::renderOverlay;

    public static void renderOverlay(ForgeGui gui, GuiGraphics graphics, float partialTicks, int width, int height) {
        if (spline == null) return;
        Minecraft mc = Minecraft.getInstance();

        ItemStack itemIcon = currentStack.copyWithCount(1);
        String requiredItemCount = ""+tubeBlock.getItemsForTubeLength(spline.getLength());

        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.translatable("petrolpark.tube.title", requiredItemCount, currentStack.getHoverName()));
        for (Controls control : Controls.values()) if (control.useCondition.get()) tooltip.add(control.translate());

        int tooltipTextWidth = 0;
        for (Component line : tooltip) tooltipTextWidth = Math.max(tooltipTextWidth, mc.font.width(line));
        int tooltipHeight = tooltip.size() * 10;

        CClient cfg = AllConfigs.client();
		int posX = width / 2 + cfg.overlayOffsetX.get();
		int posY = height / 2 + cfg.overlayOffsetY.get();
		posX = Math.min(posX, width - tooltipTextWidth - 20);
		posY = Math.min(posY, height - tooltipHeight - 20);
		boolean useCustom = cfg.overlayCustomColor.get();
		Color colorBackground = useCustom ? new Color(cfg.overlayBackgroundColor.get()) : new Color(0x3c_101010);
		Color colorBorderTop = useCustom ? new Color(cfg.overlayBorderColorTop.get()) : new Color(0xff_c9974c);
		Color colorBorderBot = useCustom ? new Color(cfg.overlayBorderColorBot.get()) : new Color(0xff_f1dd79);

        PoseStack ms = graphics.pose();
        ms.pushPose();
        GuiGameElement.of(itemIcon)
			.at(posX + 10, posY - 16, 450)
			.render(graphics);

        if (!Mods.MODERNUI.isLoaded()) { // Default tooltip rendering when modernUI is not loaded
			RemovedGuiUtils.drawHoveringText(graphics, tooltip, posX, posY, width, height, -1, colorBackground.getRGB(), colorBorderTop.getRGB(), colorBorderBot.getRGB(), mc.font);
            ms.translate(0f, 0f, 100f);
		} else { // Special handling for modernUI - copied from Create source code
            MouseHandler mouseHandler = mc.mouseHandler;
            Window window = mc.getWindow();
            double guiScale = window.getGuiScale();
            double cursorX = mouseHandler.xpos();
            double cursorY = mouseHandler.ypos();
            ((MouseHandlerAccessor)mouseHandler).create$setXPos(Math.round(cursorX / guiScale) * guiScale);
            ((MouseHandlerAccessor)mouseHandler).create$setYPos(Math.round(cursorY / guiScale) * guiScale);
            RemovedGuiUtils.drawHoveringText(graphics, tooltip, posX, posY, width, height, -1, colorBackground.getRGB(), colorBorderTop.getRGB(), colorBorderBot.getRGB(), mc.font);
            ((MouseHandlerAccessor)mouseHandler).create$setXPos(cursorX);
            ((MouseHandlerAccessor)mouseHandler).create$setYPos(cursorY);
        };

		ms.popPose();
    };

    protected static enum Controls {
        BUILD(() -> spline.getResult().success, () -> {
            PetrolparkMessages.sendToServer(new BuildTubePacket(tubeBlock, spline));
            cancel();
            }, PetrolparkKeys.TUBE_BUILD),
        GRAB_CONTROL_POINT(() -> targetedControlPoint > 0 && targetedControlPoint < spline.getControlPoints().size() - 1, () -> {}, null),
        MOVE_CONTROL_POINT(() -> draggingSelectedControlPoint, () -> {}, null),
        DELETE_CONTROL_POINT(() -> targetedControlPoint > 0 && targetedControlPoint < spline.getControlPoints().size() - 1 && !draggingSelectedControlPoint, () -> {spline.removeControlPoint(targetedControlPoint);}, PetrolparkKeys.TUBE_DELETE_CONTROL_POINT),
        ADD_CONTROL_POINT_AFTER(() -> spline.getControlPoints().size() < TubeSpline.MAX_CONTROL_POINTS && targetedControlPoint >= 0 && targetedControlPoint < spline.getControlPoints().size() - 1 && !draggingSelectedControlPoint, () -> spline.addInterpolatedControlPoint(targetedControlPoint + 1), PetrolparkKeys.TUBE_ADD_CONTROL_POINT_AFTER),
        ADD_CONTROL_POINT_BEFORE(() -> spline.getControlPoints().size() < TubeSpline.MAX_CONTROL_POINTS && targetedControlPoint > 0 && targetedControlPoint < spline.getControlPoints().size(), () -> spline.addInterpolatedControlPoint(targetedControlPoint), PetrolparkKeys.TUBE_ADD_CONTROL_POINT_BEFORE),
        CANCEL(() -> true, ClientTubePlacementHandler::cancel, PetrolparkKeys.TUBE_CANCEL)
        ;

        public final Supplier<Boolean> useCondition;
        public final Runnable action;
        public final PetrolparkKeys key;

        Controls(Supplier<Boolean> useCondition, Runnable action, PetrolparkKeys key) {
            this.useCondition = useCondition;
            this.action = action;
            this.key = key;
        };

        public Component translate() {
            return Component.translatable("petrolpark.tube.control."+ Lang.asId(name()), key == null ? null : key.keybind.getKey().getDisplayName()).withStyle(ChatFormatting.GRAY);
        };
    };

    @SubscribeEvent
    public static void onUseMouse(InputEvent.MouseButton event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && spline != null && targetedControlPoint > 0 && targetedControlPoint < spline.getControlPoints().size() - 1 && event.getButton() == InputConstants.MOUSE_BUTTON_RIGHT && draggingSelectedControlPoint == (event.getAction() == InputConstants.RELEASE)) {
            draggingSelectedControlPoint = !draggingSelectedControlPoint;
            distanceToSelectedControlPoint = mc.player.getEyePosition().distanceTo(spline.getControlPoints().get(targetedControlPoint));
            event.setCanceled(true);
            resetTTL();
        };
    };

    @SubscribeEvent
    public static void onScrollMouse(InputEvent.MouseScrollingEvent event) {
        if (draggingSelectedControlPoint) {
            distanceToSelectedControlPoint = Mth.clamp(distanceToSelectedControlPoint + event.getScrollDelta() / 8d, Math.min(distanceToSelectedControlPoint, 0.5d), Math.max(distanceToSelectedControlPoint, 6d));
            relocateControlPoint();
            event.setCanceled(true);
            resetTTL();
        };
    };

    @SubscribeEvent
    public static void onUseKey(InputEvent.Key event) {
        if (spline == null) return;
        if (event.getAction() == InputConstants.RELEASE) {
            Minecraft mc = Minecraft.getInstance();
            for (Controls control : Controls.values()) if (control.useCondition.get() && control.key != null && event.getKey() == control.key.keybind.getKey().getValue()) {
                control.key.keybind.consumeClick();
                control.action.run();
                if (spline != null) {
                    controlPointBoxes = new ArrayList<>();
                    revalidateSpline(mc);
                };
                resetTTL();
                return;
            };
        };
    };

    protected static void relocateControlPoint() {
        Minecraft mc = Minecraft.getInstance();
        if (spline.moveControlPoint(targetedControlPoint, mc.player.getEyePosition().add(mc.player.getViewVector(AnimationTickHolder.getPartialTicks()).scale(distanceToSelectedControlPoint)))) {
            controlPointBoxes = new ArrayList<>(); // All control points need to be moved
            mc.level.playSound(mc.player, mc.player.getOnPos(), SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.25f, 0.1f);
            revalidateSpline(mc);
        };
    };

    public static void tryConnect(BlockFace location, ItemStack stack, ITubeBlock tubeBlock, boolean manualPlacement) {
        Minecraft mc = Minecraft.getInstance();
        if (start == null) { // If placing the first Block
            currentStack = stack;
            ClientTubePlacementHandler.tubeBlock = tubeBlock;
            start = location;
            spline = null;
            if (manualPlacement) mc.player.displayClientMessage(Component.translatable("petrolpark.tube.connect_another"), true);
            resetTTL();
        } else if (spline == null) { // If placing the second Block
            if (stack != currentStack) {
                cancel();
                return;
            };
            end = location;
            spline = new TubeSpline(start, end, tubeBlock.getTubeMaxAngle(), tubeBlock.getTubeSegmentLength(), tubeBlock.getTubeSegmentRadius());
            if (manualPlacement) spline.addControlPoint(start.getCenter().add(end.getCenter()).scale(0.5d));
            revalidateSpline(mc);
            resetTTL();
        } else {
            cancel();
            tryConnect(location, stack, tubeBlock, manualPlacement);  
        };
    };

    public static void revalidateSpline(Minecraft mc) {
        spline.validate(mc.level, mc.player, currentStack.getItem(), tubeBlock);
    };

    public static void addControlPointWithoutRevalidating(Vec3 controlPoint) {
        if (spline != null) spline.addControlPoint(controlPoint);
    };

    public static void resetTTL() {
        ttl = 400;
    };

    public static boolean active() {
        return spline != null;
    };

    public static void cancel() {
        currentStack = ItemStack.EMPTY;
        tubeBlock = null;
        start = null;
        end = null;
        spline = null;

        ttl = 0;
        targetedControlPoint = -1;
        distanceToSelectedControlPoint = 0f;
        draggingSelectedControlPoint = false;
        controlPointBoxes = new ArrayList<>();
        canAfford = true;
    };

};
