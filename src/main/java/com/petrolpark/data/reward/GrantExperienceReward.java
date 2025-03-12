package com.petrolpark.data.reward;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.petrolpark.data.IEntityTarget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class GrantExperienceReward extends ContextEntityReward {

    protected final NumberProvider amount;

    public GrantExperienceReward(IEntityTarget target, NumberProvider amount) {
        super(target);
        this.amount = amount;
    };

    @Override
    public void reward(Entity entity, LootContext context, float multiplier) {
        if (entity instanceof Player player) player.giveExperiencePoints(amount.getInt(context));
    };

    @Override
    public void render(GuiGraphics graphics) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'render'");
    };

    @Override
    public Component getName() {
        return Component.translatable("reward.petrolpark.xp");
    };

    @Override
    public RewardType getType() {
        return RewardTypes.GRANT_EXPERIENCE.get();
    };

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<GrantExperienceReward> {

        @Override
        public void serialize(JsonObject json, GrantExperienceReward value, JsonSerializationContext serializationContext) {
            json.addProperty("target", value.target.getSerializedName());
            json.add("amount", serializationContext.serialize(value.amount, NumberProvider.class));
        };

        @Override
        public GrantExperienceReward deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
            return new GrantExperienceReward(IEntityTarget.getByName(GsonHelper.getAsString(json, "target")), GsonHelper.getAsObject(json, "amount", serializationContext, NumberProvider.class));
        };

    };
    
};
