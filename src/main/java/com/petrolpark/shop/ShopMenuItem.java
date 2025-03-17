package com.petrolpark.shop;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import com.petrolpark.PetrolparkAttachmentTypes;
import com.petrolpark.PetrolparkDataComponents;
import com.petrolpark.team.ITeam;
import com.petrolpark.team.ITeamBoundItem;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
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

public class ShopMenuItem extends Item implements ITeamBoundItem<Item> {

    public static final String SHOP_TAG_KEY = "Shop";

    public ShopMenuItem(Properties properties) {
        super(properties);
    };

    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.get(PetrolparkDataComponents.SHOP) != null) {
            InteractionResult result = trySelectTeam(stack, player, level);
            if (result != InteractionResult.PASS) return new InteractionResultHolder<>(result, stack);
        };
        return super.use(level, player, hand);
    };

    @Override
    public InteractionResult interactLivingEntity(@Nonnull ItemStack stack, @Nonnull Player player, @Nonnull LivingEntity entity, @Nonnull InteractionHand hand) {
        return Optional.ofNullable(stack.get(PetrolparkDataComponents.SHOP))
            .map(Holder::value)
            .filter(shop -> shop.canServe(entity))
            .map(shop -> {
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
        Optional.ofNullable(stack.get(PetrolparkDataComponents.SHOP)).ifPresent(shop -> {
            ITeam team = ITeamBoundItem.getTeam(stack, context.level());
            if (!team.isNone()) Optional.ofNullable(team.get(PetrolparkDataComponents.SHOPS_DATA)).ifPresent(shops ->  tooltipComponents.add(shops.getName(shop).copy().withStyle(ChatFormatting.GRAY)));
        });
    };

    @OnlyIn(Dist.CLIENT)
    @Override
    public Component getTeamSelectionScreenTitle(Level level, Player player, ItemStack stack) {
        return Component.translatable("item.petrolpark.menu.team_selection", Optional.ofNullable(stack.get(PetrolparkDataComponents.SHOP)).map(Holder::value).map(Shop::getName).orElse(Component.translatable("shop.petrolpark.unknown")));
    };
    
};
