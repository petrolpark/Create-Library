package com.petrolpark.data.reward.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkRewardTypes;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.storage.loot.LootContext;

public record UnlockTradeEntityReward(MerchantOffer trade) implements IEntityReward {

    public static final MapCodec<UnlockTradeEntityReward> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        MerchantOffer.CODEC.fieldOf("trade").forGetter(UnlockTradeEntityReward::trade)
    ).apply(instance, UnlockTradeEntityReward::new));

    @Override
    public void reward(Entity entity, LootContext context, float multiplier) {
        if (entity instanceof Merchant merchant) merchant.getOffers().add(trade);
    };

    @Override
    public void render(GuiGraphics graphics) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'render'");
    };

    @Override
    public Component getName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getName'");
    };

    @Override
    public EntityRewardType getType() {
        return PetrolparkRewardTypes.UNLOCK_TRADE.get();
    };
    
};
