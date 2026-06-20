package com.petrolpark.compat.create.core.world.block.tube;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.compat.create.RequiresCreate;
import com.petrolpark.compat.create.registry.PetrolparkCreateClient;
import com.petrolpark.registry.PetrolparkKeyBinds;
import com.petrolpark.util.BlockFace;
import com.petrolpark.util.Lang;
import com.petrolpark.util.Pair;
import com.petrolpark.util.RayHelper;
import com.petrolpark.util.RayHelper.CustomHitResult;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.foundation.gui.RemovedGuiUtils;
import com.simibubi.create.foundation.mixin.accessor.MouseHandlerAccessor;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CClient;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.catnip.theme.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;

@OnlyIn(Dist.CLIENT)
@RequiresCreate
public class ClientTubePlacementHandler {

    public static final int TIMEOUT = 12000;
    
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
    public static final void tick(ClientTickEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        LocalPlayer player = mc.player;

        if (level == null ||
            player == null ||
            !(ItemStack.isSameItem(player.getItemInHand(InteractionHand.MAIN_HAND), currentStack) || AllItems.WRENCH.isIn(player.getMainHandItem())) ||
            currentStack.isEmpty() ||
            (start != null && level.getBlockState(start.getPos()).getBlock() != tubeBlock
        )) {
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
        if (level.getBlockState(end.getPos()).getBlock() != tubeBlock) {
            cancel();
            return;
        };

        boolean buildable = spline.getResult().success;
        int color = buildable ? 0xFF_4DE680 : 0xFF_E64D80;

        // Render path of spline
        for (int i = 0; i < spline.getPoints().size() - 1; i++) {
            Vec3 point = spline.getPoints().get(i);
            Vec3 tangent = spline.getTangents().get(i);
            PetrolparkCreateClient.OUTLINER.showLine(Pair.of(point, tangent), point, spline.getPoints().get(i+1)).colored(color);
        };

        // Render blocking Blocks and decide if the Tube is being blocked
        spline.checkBlocked(mc.level, pos -> PetrolparkCreateClient.OUTLINER.chaseAABB(Pair.of("blocking_tube", pos), new AABB(pos)).colored(0xFF_E64D80));

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
            PetrolparkCreateClient.OUTLINER.showBox(Pair.of("control_point", i), controlPointBox.inflate(i == targetedControlPoint ? 1 / 16d : 0d), 2).colored(color);
        };

        // Show message
        player.displayClientMessage(spline.result.translate(currentStack), true);
    };

    public static final LayeredDraw.Layer OVERLAY = ClientTubePlacementHandler::renderOverlay;

    public static final void renderOverlay(GuiGraphics graphics, DeltaTracker deltaTracker) {
        if (spline == null) return;
        Minecraft mc = Minecraft.getInstance();

        ItemStack itemIcon = currentStack.copyWithCount(1);
        String requiredItemCount = ""+tubeBlock.getItemsForTubeLength(spline.getLength());

        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Lang.translate("tube.title", requiredItemCount, currentStack.getHoverName()));
        for (Controls control : Controls.values()) if (control.canUse()) tooltip.add(control.translate());

        int tooltipTextWidth = 0;
        for (Component line : tooltip) tooltipTextWidth = Math.max(tooltipTextWidth, mc.font.width(line));
        int tooltipHeight = tooltip.size() * 10;

        CClient cfg = AllConfigs.client();
		int posX = graphics.guiWidth() / 2 + cfg.overlayOffsetX.get();
		int posY = graphics.guiHeight() / 2 + cfg.overlayOffsetY.get();
		posX = Math.min(posX, graphics.guiWidth() - tooltipTextWidth - 20);
		posY = Math.min(posY, graphics.guiHeight() - tooltipHeight - 20);
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
			RemovedGuiUtils.drawHoveringText(graphics, tooltip, posX, posY, graphics.guiWidth(), graphics.guiHeight(), -1, colorBackground.getRGB(), colorBorderTop.getRGB(), colorBorderBot.getRGB(), mc.font);
            ms.translate(0f, 0f, 100f);
		} else { // Special handling for modernUI - copied from Create source code
            MouseHandler mouseHandler = mc.mouseHandler;
            Window window = mc.getWindow();
            double guiScale = window.getGuiScale();
            double cursorX = mouseHandler.xpos();
            double cursorY = mouseHandler.ypos();
            ((MouseHandlerAccessor)mouseHandler).create$setXPos(Math.round(cursorX / guiScale) * guiScale);
            ((MouseHandlerAccessor)mouseHandler).create$setYPos(Math.round(cursorY / guiScale) * guiScale);
            RemovedGuiUtils.drawHoveringText(graphics, tooltip, posX, posY, graphics.guiWidth(), graphics.guiHeight(), -1, colorBackground.getRGB(), colorBorderTop.getRGB(), colorBorderBot.getRGB(), mc.font);
            ((MouseHandlerAccessor)mouseHandler).create$setXPos(cursorX);
            ((MouseHandlerAccessor)mouseHandler).create$setYPos(cursorY);
        };

		ms.popPose();
    };

    protected static enum Controls {
        BUILD(PetrolparkKeyBinds.TUBE_BUILD) {
            public boolean canUse() { return spline.getResult().success; };
            public void use() {
                CatnipServices.NETWORK.sendToServer(new BuildTubePacket(tubeBlock, spline));
                cancel();
            };
        }, GRAB_CONTROL_POINT(null) {
            public boolean canUse() { return targetedControlPoint > 0 && targetedControlPoint < spline.getControlPoints().size() - 1; };
            public void use() {};
        }, MOVE_CONTROL_POINT(null) {
            public boolean canUse() { return draggingSelectedControlPoint; };
            public void use() {};
        }, DELETE_CONTROL_POINT(PetrolparkKeyBinds.TUBE_DELETE_CONTROL_POINT) {
            public boolean canUse() { return targetedControlPoint > 0 && targetedControlPoint < spline.getControlPoints().size() - 1 && !draggingSelectedControlPoint; };
            public void use() { spline.removeControlPoint(targetedControlPoint); };
        }, ADD_CONTROL_POINT_AFTER(PetrolparkKeyBinds.TUBE_ADD_CONTROL_POINT_AFTER) {
            public boolean canUse() { return  spline.getControlPoints().size() < TubeSpline.MAX_CONTROL_POINTS && targetedControlPoint >= 0 && targetedControlPoint < spline.getControlPoints().size() - 1 && !draggingSelectedControlPoint; };
            public void use() { spline.addInterpolatedControlPoint(targetedControlPoint + 1); };
        }, ADD_CONTROL_POINT_BEFORE(PetrolparkKeyBinds.TUBE_ADD_CONTROL_POINT_BEFORE) {
            public boolean canUse() { return spline.getControlPoints().size() < TubeSpline.MAX_CONTROL_POINTS && targetedControlPoint > 0 && targetedControlPoint < spline.getControlPoints().size(); };
            public void use() { spline.addInterpolatedControlPoint(targetedControlPoint); };
        }, CANCEL(PetrolparkKeyBinds.TUBE_CANCEL) {
            public boolean canUse() { return true; };
            public void use() { cancel(); };
        };

        public final PetrolparkKeyBinds key;

        Controls(PetrolparkKeyBinds key) {
            this.key = key;
        };

        public abstract boolean canUse();

        public abstract void use();

        public Component translate() {
            return Lang.translate("tube.control."+ Lang.asId(name()), key == null ? "" : key.keybind.getKey().getDisplayName()).withStyle(ChatFormatting.GRAY);
        };
    };

    @SubscribeEvent
    public static final void onUseMouse(InputEvent.MouseButton.Pre event) {
        final Minecraft mc = Minecraft.getInstance();
        final LocalPlayer player = mc.player;

        if (player == null || spline == null) return; 
        event.setCanceled(true); // No other mouse inputs allowed in Tube placing mode
        KeyMapping.set(InputConstants.Type.MOUSE.getOrCreate(event.getButton()), false);
        
        if (targetedControlPoint > 0 && targetedControlPoint < spline.getControlPoints().size() - 1 && event.getButton() == InputConstants.MOUSE_BUTTON_RIGHT && draggingSelectedControlPoint == (event.getAction() == InputConstants.RELEASE)) {
            draggingSelectedControlPoint = !draggingSelectedControlPoint;
            distanceToSelectedControlPoint = player.getEyePosition().distanceTo(spline.getControlPoints().get(targetedControlPoint));
            resetTTL();
        };
    };

    @SubscribeEvent
    public static final void onScrollMouse(InputEvent.MouseScrollingEvent event) {
        if (draggingSelectedControlPoint) {
            distanceToSelectedControlPoint = Mth.clamp(distanceToSelectedControlPoint + event.getScrollDeltaY() / 8d, Math.min(distanceToSelectedControlPoint, 0.5d), Math.max(distanceToSelectedControlPoint, 6d));
            relocateControlPoint();
            event.setCanceled(true);
            resetTTL();
        };
    };

    @SubscribeEvent
    public static final void onUseKey(InputEvent.Key event) {
        if (spline == null) return;
        if (event.getAction() == InputConstants.RELEASE) {
            final Minecraft mc = Minecraft.getInstance();
            for (final Controls control : Controls.values()) if (control.canUse() && control.key != null && event.getKey() == control.key.keybind.getKey().getValue()) {
                control.key.keybind.consumeClick();
                control.use();
                if (spline != null) {
                    controlPointBoxes = new ArrayList<>();
                    revalidateSpline(mc);
                };
                resetTTL();
                return;
            };
        };
    };

    @SubscribeEvent
    public static final void onRenderHighlight(RenderHighlightEvent.Block event) {
        if (spline != null) event.setCanceled(true);
    };

    protected static final void relocateControlPoint() {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        LocalPlayer player = mc.player;
        if (level != null && player != null && spline.moveControlPoint(targetedControlPoint, player.getEyePosition().add(player.getViewVector(AnimationTickHolder.getPartialTicks()).scale(distanceToSelectedControlPoint)))) {
            controlPointBoxes = new ArrayList<>(); // All control points need to be moved
            level.playSound(player, player.getOnPos(), SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.25f, 0.1f);
            revalidateSpline(mc);
        };
    };

    public static final void tryConnect(BlockFace location, ItemStack stack, ITubeBlock tubeBlock, boolean manualPlacement) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (start == null) { // If placing the first Block
            currentStack = stack;
            ClientTubePlacementHandler.tubeBlock = tubeBlock;
            start = location;
            spline = null;
            if (manualPlacement && player != null) player.displayClientMessage(Lang.translate("tube.connect_another", stack.getHoverName()), true);
            resetTTL();
        } else if (spline == null) { // If placing the second Block
            if (!ItemStack.isSameItemSameComponents(stack, currentStack)) {
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

    public static final void revalidateSpline(Minecraft mc) {
        spline.validate(mc.level, mc.player, currentStack.getItem(), tubeBlock);
    };

    public static final void addControlPointWithoutRevalidating(Vec3 controlPoint) {
        if (spline != null) spline.addControlPoint(controlPoint);
    };

    public static final void resetTTL() {
        ttl = TIMEOUT;
    };

    public static final boolean active() {
        return spline != null;
    };

    public static final void cancel() {
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
