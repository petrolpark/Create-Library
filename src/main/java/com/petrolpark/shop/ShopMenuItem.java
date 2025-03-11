package com.petrolpark.shop;

import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.shop.customer.EntityCustomer;
import com.petrolpark.team.ITeam;
import com.petrolpark.team.ITeamBoundItem;
import com.petrolpark.team.data.TeamDataTypes;
import com.petrolpark.util.NBTHelper;

import java.util.List;
import java.util.Optional;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.Tag;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ShopMenuItem extends Item implements ITeamBoundItem<Item> {

    public static final String SHOP_TAG_KEY = "Shop";

    public ShopMenuItem(Properties properties) {
        super(properties);
    };

    public Optional<Shop> getShop(Level level, Player player, ItemStack stack) {
        if (!stack.hasTag() || !stack.getTag().contains(SHOP_TAG_KEY, Tag.TAG_STRING)) return Optional.empty();
        return Optional.ofNullable(NBTHelper.readRegistryObject(stack.getTag(), SHOP_TAG_KEY, PetrolparkRegistries.Keys.SHOP, level));
    };

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (getShop(level, player, stack).isPresent()) {
            InteractionResult result = trySelectTeam(stack, player, level);
            if (result != InteractionResult.PASS) return new InteractionResultHolder<>(result, stack);
        };
        return super.use(level, player, hand);
    };

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        return getShop(player.level(), player, stack)
            .filter(shop -> shop.canServe(entity))
            .map(shop -> {
                entity.getCapability(EntityCustomer.CAPABILITY);
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
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        Minecraft mc = Minecraft.getInstance();
        getShop(level, mc.player, stack).ifPresent(shop -> {
            ITeam<?> team = ITeamBoundItem.getTeam(stack, level);
            if (!team.isNone()) tooltipComponents.add(team.getTeamData(TeamDataTypes.SHOPS.get()).getName(shop).copy().withStyle(ChatFormatting.GRAY));
        });
    };

    @OnlyIn(Dist.CLIENT)
    @Override
    public Component getTeamSelectionScreenTitle(Level level, Player player, ItemStack stack) {
        return Component.translatable("item.petrolpark.menu.team_selection", getShop(level, player, stack).map(Shop::getName).orElse(Component.translatable("shop.petrolpark.unknown")));
    };
    
};
