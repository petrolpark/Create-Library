package petrolpark.mc.library.core.data.reward.entity;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.storage.loot.LootContext;
import petrolpark.mc.library.registry.PetrolparkRewardTypes;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;
import petrolpark.mc.library.util.codec.CodecHelper;

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
@ParametersAreNonnullByDefault
public record UnlockTradeEntityReward(MerchantOffer trade) implements ISimpleEntityReward {

    public static final MapCodec<UnlockTradeEntityReward> CODEC = CodecHelper.singleFieldMap(MerchantOffer.CODEC, "trade", UnlockTradeEntityReward::trade, UnlockTradeEntityReward::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, UnlockTradeEntityReward> STREAM_CODEC = StreamCodec.composite(MerchantOffer.STREAM_CODEC, UnlockTradeEntityReward::trade, UnlockTradeEntityReward::new);
    
    @Override
    public boolean reward(Entity entity, LootContext context, float multiplier, boolean simulate) {
        if (!(entity instanceof Merchant merchant)) return false;
        if (!simulate) merchant.getOffers().add(trade);
        return true;
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
    public EntityRewardAndInfoType getType() {
        return PetrolparkRewardTypes.ENTITY_UNLOCK_TRADE.get();
    };
    
};
