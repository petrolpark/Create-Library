package petrolpark.mc.library.core.world.item.restaurant;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.core.world.entity.player.team.ITeamBoundItem;
import petrolpark.mc.library.registry.PetrolparkAttachmentTypes;
import petrolpark.mc.library.registry.PetrolparkDataComponentTypes;

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

    @Override
    public InteractionResult interactLivingEntity(@Nonnull ItemStack stack, @Nonnull Player player, @Nonnull LivingEntity entity, @Nonnull InteractionHand hand) {
        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.SUCCESS;
        return Optional.ofNullable(stack.get(PetrolparkDataComponentTypes.RESTAURANT))
            .map(Holder::value)
            .filter(restaurant -> restaurant.canServe(serverPlayer, entity))
            .map(restaurant -> {
                entity.getData(PetrolparkAttachmentTypes.ENTITY_CUSTOMER);
                //TODO
                return InteractionResult.SUCCESS;
            }).orElse(super.interactLivingEntity(stack, player, entity, hand));
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
        return Component.translatable("item.petrolpark.menu.team_selection", Optional.ofNullable(stack.get(PetrolparkDataComponentTypes.RESTAURANT)).map(Holder::value).map(Restaurant::getName).orElse(Component.translatable("restaurant.petrolpark.unknown")));
    };
    
};
