package com.petrolpark.core.data.reward.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkRewardTypes;
import com.petrolpark.util.Lang.IndentedTooltipBuilder;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.storage.loot.LootContext;

/**
 * <p>{@code petrolpark:unlock_trade}</p>
 * 
 * Adds a {@link MerchantOffer Trade} to the Entity (if they are a {@link Merchant}).
 * 
 * Arguments:
 * <ul>
 * <li> {@code trade} - The {@link MerchantOffer} to add to the recipient
 * </ul>
 * 
 * @author petrolpark
 */
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
    public void addToDescription(IndentedTooltipBuilder builder) {
        builder.add(translateSimple())
            .indent()
            .add(trade().getResult().getHoverName())
            .unindent()
            .add(translate("buying"))
            .indent()
            .add(trade().getCostA().getHoverName());
        if (!trade().getCostB().isEmpty()) builder.add(trade().getCostB().getHoverName());
        builder.unindent();
    };

    @Override
    public EntityRewardType getType() {
        return PetrolparkRewardTypes.UNLOCK_TRADE.get();
    };
    
};
