package com.petrolpark.data.reward;

import java.util.Optional;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import com.petrolpark.data.IEntityTarget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.storage.loot.LootContext;

public class UnlockTradeReward extends ContextEntityReward {

    public final MerchantOffer trade;

    public UnlockTradeReward(IEntityTarget target, MerchantOffer trade) {
        super(target);
        this.trade = trade;
    };

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
    public RewardType getType() {
        return RewardTypes.UNLOCK_TRADE.get();
    };

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<UnlockTradeReward> {

        @Override
        public void serialize(JsonObject json, UnlockTradeReward value, JsonSerializationContext serializationContext) {
            json.addProperty("target", value.target.getSerializedName());
            CompoundTag.CODEC.encodeStart(JsonOps.INSTANCE, value.trade.createTag())
                .resultOrPartial(err -> {throw new IllegalStateException(err);})
                .ifPresent(element -> json.add("trade", element));
        };

        @Override
        public UnlockTradeReward deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
            IEntityTarget target = IEntityTarget.getByName(GsonHelper.getAsString(json, "target"));
            Optional<Pair<CompoundTag, JsonElement>> op = CompoundTag.CODEC.decode(JsonOps.INSTANCE, GsonHelper.getAsJsonObject(json, "trade"))
                .resultOrPartial(err -> {throw new JsonSyntaxException(err);});
            if (op.isEmpty()) throw new JsonSyntaxException("Unlock Trade reward specifies no or an invalid trade");
            return new UnlockTradeReward(target, new MerchantOffer(op.get().getFirst()));
        };

    };
    
};
