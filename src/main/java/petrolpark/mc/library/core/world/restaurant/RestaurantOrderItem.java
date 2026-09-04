package petrolpark.mc.library.core.world.restaurant;

import static petrolpark.mc.library.registry.PetrolparkDataComponentTypes.CUSTOMER_PROVIDER;
import static petrolpark.mc.library.registry.PetrolparkDataComponentTypes.RESTAURANT_ORDER_EXAMPLES;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import petrolpark.mc.library.core.client.tooltip.CyclingItemsClientTooltipComponent;
import petrolpark.mc.library.core.world.item.deletable.IDeletableItem;
import petrolpark.mc.library.core.world.restaurant.customer.ICustomer;
import petrolpark.mc.library.core.world.restaurant.customer.MobCustomer;
import petrolpark.mc.library.core.world.restaurant.gui.RestaurantOrderMenu;
import petrolpark.mc.library.registry.PetrolparkItems;

@EventBusSubscriber
@ParametersAreNonnullByDefault
public class RestaurantOrderItem extends Item implements IDeletableItem {

    public RestaurantOrderItem(Item.Properties properties) {
        super(properties);
    };

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        final ItemStack stack = player.getItemInHand(usedHand);
        final ICustomer customer = stack.getOrDefault(CUSTOMER_PROVIDER, ICustomer.none()).provideCustomer(level);
        if (!customer.isNone()) {
            player.openMenu(new RestaurantOrderItem.MenuProvider(customer, false));
            return InteractionResultHolder.success(stack);
        };
        return InteractionResultHolder.pass(stack);
    };

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!PetrolparkItems.ORDER.isIn(event.getItemStack())) return;
        if (!(event.getTarget() instanceof LivingEntity entity)) return;
        final InteractionResult result = getEntityInteractionResult(event.getItemStack(), event.getEntity(), entity);
        if (result.consumesAction()) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        };
    };

    public static InteractionResult getEntityInteractionResult(ItemStack stack, Player player, LivingEntity entity) {
        if (
            stack.getOrDefault(CUSTOMER_PROVIDER, ICustomer.none()).provideCustomer(player.level()) instanceof MobCustomer customer
            && customer.entity == entity // direct comparison fine as same level object used
        ) {
            player.openMenu(new RestaurantOrderItem.MenuProvider(customer, true));
            return InteractionResult.SUCCESS;
        };
        return InteractionResult.FAIL;
    };

    @Override
    public void delete(Player player, ItemStack stack) {
        if (player.level() instanceof ServerLevel level)
            stack.getOrDefault(CUSTOMER_PROVIDER, ICustomer.none()).provideCustomer(level).cancelOrder(level, player);
    };

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        stack.update(CUSTOMER_PROVIDER, ICustomer.none(), ICustomer.Provider.update(level)); // Update client in case the entity ID changed

        final ICustomer customer = stack.getOrDefault(CUSTOMER_PROVIDER, ICustomer.none()).provideCustomer(level); 
        
        if (stack.has(RESTAURANT_ORDER_EXAMPLES) == customer.isNone()) {
            if (customer.isNone()) {
                stack.remove(RESTAURANT_ORDER_EXAMPLES);
            } else {
                stack.set(RESTAURANT_ORDER_EXAMPLES, customer.getOrder().ingredient().streamExamples().toList());
            };
        };

        if (isSelected && entity instanceof Player player)
            customer.tickWhileOrderItemHeld(stack, level, player, slotId);
    };

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        final Level level = context.level();
        if (level == null) return;
        final ICustomer customer = stack.getOrDefault(CUSTOMER_PROVIDER, ICustomer.none()).provideCustomer(level);
        if (customer.isNone()) return;
        // tooltipComponents.add(customer.getTeamProvider().provideTeam(level).getOrDefault(TEAM_RESTAURANTS, new RestaurantsData())
        //     .getName(customer.getRestaurant()).copy().withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable(getDescriptionId() + ".customer", customer.getName(), customer.getPosition().getX(), customer.getPosition().getY(), customer.getPosition().getZ()).withStyle(ChatFormatting.GRAY));
    };

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.ofNullable(stack.get(RESTAURANT_ORDER_EXAMPLES)).map(CyclingItemsClientTooltipComponent.Image::new);
    };

    public record MenuProvider(ICustomer customer, boolean canServe) implements net.minecraft.world.MenuProvider {

        @Override
        @Nullable
        public RestaurantOrderMenu createMenu(int containerId, Inventory playerInventory, Player player) {
            return RestaurantOrderMenu.create(containerId, playerInventory, customer(), canServe());
        };

        @Override
        public Component getDisplayName() {
            return customer().getName();
        };

        @Override
        public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
            ICustomer.Provider.STREAM_CODEC.encode(buffer, customer().getProvider());
            buffer.writeBoolean(canServe());
        };

    };
    
};
