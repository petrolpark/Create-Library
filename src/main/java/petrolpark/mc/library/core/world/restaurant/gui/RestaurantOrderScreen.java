package petrolpark.mc.library.core.world.restaurant.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.ParametersAreNonnullByDefault;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import petrolpark.mc.library.core.client.rendering.PetrolparkGuiTexture;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredient;
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;
import petrolpark.mc.library.core.world.restaurant.order.ClientRestaurantOrder;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;

@ParametersAreNonnullByDefault
public class RestaurantOrderScreen extends RestaurantScreen<RestaurantOrderMenu> {

    protected static final int ORDER_LINE = 4;
    protected static final int ICON_X = 4;
    protected static final int ICON_Y = -4;
    protected static final int REWARD_SIZE = 18;

    protected final ClientRestaurantOrder.Description order;
    protected final List<ItemStack> exampleStacks;
    protected final Int2ObjectMap<OrderModifierVisual> modifiers;
    protected final List<IRewardInfo> rewards;

    public RestaurantOrderScreen(RestaurantOrderMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        font = Minecraft.getInstance().font;
        imageHeight = 244;

        order = menu.customer.getDescription(inv.player.level(), font, NOTE_TEXT_WIDTH, LINE_HEIGHT);
        exampleStacks = menu.customer.getOrder().ingredient().streamExamples().toList();
        modifiers = new Int2ObjectArrayMap<>();
        for (int i = 0; i < menu.customer.getOrder().modifiersInfo().size(); i++) {
            final IAdvancedIngredient<ItemStack> ingredient = menu.customer.getOrder().modifiersInfo().get(i).ingredient();
            modifiers.put(order.orderModifierLineIndicies().getInt(i), new OrderModifierVisual(ingredient, ingredient.modifyExamples(exampleStacks.stream()).toList()));
        };
        rewards = menu.customer.getOrder().rewardsInfo();
    };

    @Override
    protected List<Component> getLines() {
        return order.lines();
    };

    @Override
    protected boolean shouldRenderInventory() {
        return getMenu().canServe;
    };

    @Override
    protected void init() {
        super.init();
        topPos += 64;
    };

    @Override
    protected void renderByLine(GuiGraphics guiGraphics, float partialTick, int line) {
        // Order
        if (line == ORDER_LINE) {
            guiGraphics.renderFakeItem(getShownStack(exampleStacks), ICON_X, ICON_Y);
            return;
        };

        // Rewards
        if (line == order.rewardsLineIndex()) {
            for (int i = 0; i < rewards.size(); i++) {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(getRewardX(i), getRewardY(i), 0f);
                rewards.get(i).render(guiGraphics);
                guiGraphics.pose().popPose();
            };
            return;
        };

        // Modifiers
        final OrderModifierVisual visual = modifiers.get(line);
        if (visual == null) return;
        guiGraphics.renderFakeItem(getShownStack(visual.stacks()), ICON_X, ICON_Y);
        guiGraphics.renderItemDecorations(font, getShownStack(visual.stacks()), ICON_X, ICON_Y);
        if (!getMenu().canServe) return;
        (visual.ingredient().test(getMenu().getSlot(0).getItem()) ? PetrolparkGuiTexture.RESTAURANT_CHECKBOX_CHECKED : PetrolparkGuiTexture.RESTAURANT_CHECKBOX)
            .render(guiGraphics, ICON_X + 2, ICON_Y + 18);
    };

    protected static int getRewardsPerRow() {
        return NOTE_TEXT_WIDTH / REWARD_SIZE;
    };

    protected static int getRewardX(int reward) {
        return NOTE_TEXT_X + REWARD_SIZE * (reward % getRewardsPerRow());
    };

    protected static int getRewardY(int reward) {
        return REWARD_SIZE * (reward / getRewardsPerRow());
    };

    protected static ItemStack getShownStack(List<ItemStack> stacks) {
        if (stacks.isEmpty()) return ItemStack.EMPTY;
        return stacks.get(AnimationTickHolder.getTicks(true) / 20 % stacks.size());
    };

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);

        if (shouldRenderInventory()) {
            PetrolparkGuiTexture.RESTAURANT_PLATE.render(guiGraphics, RestaurantOrderMenu.SERVING_SLOT_X - 6, RestaurantOrderMenu.SERVING_SLOT_Y + 8);
        };

        guiGraphics.pose().popPose();
    };

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        final Vec2 mouse = getMouseOnNote(x, y);
        if (mouse != null) {
            // Order
            if (!exampleStacks.isEmpty() && isMouseOverIcon(mouse, ORDER_LINE, ICON_X, ICON_Y)) {
                guiGraphics.renderTooltip(font, getShownStack(exampleStacks), x, y);
                return;
            };

            // Modifiers
            for (Int2ObjectMap.Entry<OrderModifierVisual> modifier : modifiers.int2ObjectEntrySet()) {
                if (modifier.getValue().stacks().isEmpty()) continue;
                if (!isMouseOverIcon(mouse, modifier.getIntKey(), ICON_X, ICON_Y)) continue;
                guiGraphics.renderTooltip(font, getShownStack(modifier.getValue().stacks()), x, y);
                return;
            };

            // Rewards
            for (int i = 0; i < rewards.size(); i++) {
                if (!isMouseOverIcon(mouse, order.rewardsLineIndex(), getRewardX(i), getRewardY(i))) continue;
                final IndentedTooltipBuilder description = new IndentedTooltipBuilder.Impl(new ArrayList<>());
                rewards.get(i).addToDescription(description);
                guiGraphics.renderComponentTooltip(font, description.build(), x, y);
                return;
            };
        };

        super.renderTooltip(guiGraphics, x, y);
    };

    @Override
    public Optional<HoveredItemStack> getNoteStackUnderMouse(Vec2 mouse) {
        // Order
        if (!exampleStacks.isEmpty() && isMouseOverIcon(mouse, ORDER_LINE, ICON_X, ICON_Y)) {
            return Optional.of(new RestaurantScreen.HoveredItemStack(ICON_X, getLineY(ORDER_LINE) + ICON_Y, getShownStack(exampleStacks)));
        };

        // Modifiers
        for (Int2ObjectMap.Entry<OrderModifierVisual> modifier : modifiers.int2ObjectEntrySet()) {
            if (!isMouseOverIcon(mouse, modifier.getIntKey(), ICON_X, ICON_Y)) continue;
            return Optional.of(new RestaurantScreen.HoveredItemStack(ICON_X, getLineY(modifier.getIntKey()) + ICON_Y, getShownStack(modifier.getValue().stacks())));
        };

        // Rewards
        for (int i = 0; i < rewards.size(); i++) {
            final int rewardX = getRewardX(i);
            final int rewardY = getRewardY(i);
            if (!isMouseOverIcon(mouse, order.rewardsLineIndex(), rewardX, rewardY)) continue;
            return rewards.get(i).getItemStack().map(stack -> new RestaurantScreen.HoveredItemStack(rewardX, rewardY, stack));
        };
        
        return Optional.empty();
    };

    record OrderModifierVisual(IAdvancedIngredient<ItemStack> ingredient, List<ItemStack> stacks) {};
    
};
