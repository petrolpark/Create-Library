package com.petrolpark.badge;

import com.petrolpark.PetrolparkAttachmentTypes;
import com.petrolpark.PetrolparkDataComponents;
import com.petrolpark.PetrolparkLootItemFunctions;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class BadgeAwardLootItemFunction implements LootItemFunction {

    @Override
    public ItemStack apply(ItemStack stack, LootContext context) {
        if (context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof Player player && stack.getItem() instanceof BadgeItem item) {
            player.getData(PetrolparkAttachmentTypes.BADGES.get()).awardDate(item.badge.get()).ifPresent(date -> stack.set(PetrolparkDataComponents.BADGE_AWARD, new BadgeItem.BadgeAward(player.getUUID(), date.getTime())));
        };
        return stack;
    };

    @Override
    public LootItemFunctionType<BadgeAwardLootItemFunction> getType() {
        return PetrolparkLootItemFunctions.BADGE_AWARD.get();
    };
    
};
