package petrolpark.mc.library.core.world.restaurant;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.core.world.entity.player.team.ITeamBoundItem;
import petrolpark.mc.library.core.world.entity.player.team.NoTeam;
import petrolpark.mc.library.core.world.restaurant.customer.MobCustomer;
import petrolpark.mc.library.core.world.restaurant.order.ServerRestaurantOrder;
import petrolpark.mc.library.registry.PetrolparkAttachmentTypes;
import petrolpark.mc.library.registry.PetrolparkCriteriaTriggers;
import petrolpark.mc.library.registry.PetrolparkDataComponentTypes;
import petrolpark.mc.library.registry.PetrolparkItems;

@EventBusSubscriber
public class RestaurantOrderBookItem extends Item implements ITeamBoundItem {

    public RestaurantOrderBookItem(Properties properties) {
        super(properties);
    };

    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.get(PetrolparkDataComponentTypes.RESTAURANT) != null) {
            InteractionResult result = trySelectTeam(stack, player, level);
            if (result != InteractionResult.PASS) return new InteractionResultHolder<>(result, stack);
        };
        return super.use(level, player, hand);
    };

    @SubscribeEvent
    public static void onInteractEntity(PlayerInteractEvent.EntityInteract event) {
        final ItemStack stack = event.getEntity().getItemInHand(event.getHand());
        if (!PetrolparkItems.ORDER_BOOK.isIn(stack)) return;
        if (!(event.getTarget() instanceof LivingEntity entity)) return;
        final InteractionResult result = getInteractionResult(stack, event.getEntity(), entity);
        if (result.consumesAction()) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        };
    };

    public static InteractionResult getInteractionResult(@Nonnull ItemStack stack, @Nonnull Player player, @Nonnull LivingEntity entity) {
        final Holder<Restaurant> restaurant = stack.get(PetrolparkDataComponentTypes.RESTAURANT);
        if (restaurant == null) return InteractionResult.FAIL;

        final ITeam.Provider teamProvider = stack.getOrDefault(PetrolparkDataComponentTypes.TEAM_PROVIDER, NoTeam.INSTANCE);
        final ITeam team = teamProvider.provideTeam(player.level());
        if (team.isNone()) return InteractionResult.FAIL;

        if (!entity.getData(PetrolparkAttachmentTypes.ENTITY_CUSTOMER).isNone()) return InteractionResult.FAIL; // Already has an order

        if (player.level().isClientSide() || !(player instanceof ServerPlayer serverPlayer)) return InteractionResult.SUCCESS;

        if (!restaurant.value().canServe(serverPlayer, entity)) return InteractionResult.FAIL;
        final ServerRestaurantOrder order = Restaurant.generateOrder(serverPlayer, restaurant, team, entity).orElse(null);
        if (order == null) return InteractionResult.FAIL;

        final MobCustomer customer = new MobCustomer(entity, restaurant, teamProvider, order, serverPlayer.level().getGameTime());
        entity.setData(PetrolparkAttachmentTypes.ENTITY_CUSTOMER, customer);

        player.awardStat(Stats.ITEM_USED.get(stack.getItem()));

        final ItemStack orderStack = PetrolparkItems.ORDER.asStack();
        orderStack.set(PetrolparkDataComponentTypes.CUSTOMER_PROVIDER, customer.getProvider());
        serverPlayer.getInventory().placeItemBackInInventory(orderStack, true);

        PetrolparkCriteriaTriggers.TAKE_ENTITY_RESTAURANT_ORDER.get().trigger(serverPlayer, restaurant, team, entity, customer);

        serverPlayer.openMenu(new RestaurantOrderItem.MenuProvider(customer, true));

        return InteractionResult.SUCCESS;
    };

    @Override
    public boolean isTeamRebindable(Level level, Player player, ItemStack stack) {
        return false;
    };

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> tooltipComponents, @Nonnull TooltipFlag isAdvanced) {
        Optional.ofNullable(stack.get(PetrolparkDataComponentTypes.RESTAURANT)).ifPresent(restaurant -> {
            Optional.of(ITeamBoundItem.getTeam(stack, context.level()))
                .filter(Predicate.not(ITeam::isNone))
                .map(team -> team.get(PetrolparkDataComponentTypes.TEAM_RESTAURANTS))
                .map(restaurants -> restaurants.getName(restaurant))
                .or(() -> Optional.of(restaurant.value().getName()))
                .ifPresent(name -> tooltipComponents.add(name.copy().withStyle(ChatFormatting.GRAY)));
        });
    };

    @OnlyIn(Dist.CLIENT)
    @Override
    public Component getTeamSelectionScreenTitle(Level level, Player player, ItemStack stack) {
        return Component.translatable(getDescriptionId() + ".team_selection", Optional.ofNullable(stack.get(PetrolparkDataComponentTypes.RESTAURANT)).map(Holder::value).map(Restaurant::getName).orElse(Component.translatable("restaurant.petrolpark.unknown")));
    };
    
};
