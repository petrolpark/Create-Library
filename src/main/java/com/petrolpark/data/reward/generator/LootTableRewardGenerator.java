package com.petrolpark.data.reward.generator;

import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.petrolpark.Petrolpark;
import com.petrolpark.data.IEntityTarget;
import com.petrolpark.data.reward.GiveItemReward;
import com.petrolpark.data.reward.IReward;
import com.petrolpark.data.reward.RewardGeneratorTypes;

import java.util.ArrayList;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootTable;

public class LootTableRewardGenerator extends ContextEntityRewardGenerator {

    public final ResourceLocation lootTableRL;

    public LootTableRewardGenerator(IEntityTarget target, ResourceLocation rl) {
        super(target);
        this.lootTableRL = rl;
    };

    @Override
    public List<IReward> generate(LootContext context) {
        List<IReward> rewards = new ArrayList<>();
        LootTable table = context.getResolver().getLootTable(lootTableRL);
        if (table.equals(LootTable.EMPTY) && lootTableRL.equals(LootDataManager.EMPTY_LOOT_TABLE_KEY.location())) Petrolpark.LOGGER.warn("Unknown Loot Table in Reward Generator: "+lootTableRL);
        table.getRandomItems(context, stack -> rewards.add(new GiveItemReward(target, stack)));
        return rewards;
    };

    @Override
    public RewardGeneratorType getType() {
        return RewardGeneratorTypes.LOOT_TABLE.get();
    };

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<LootTableRewardGenerator> {

        @Override
        public void serialize(JsonObject json, LootTableRewardGenerator value, JsonSerializationContext serializationContext) {
            json.addProperty("target", value.target.name());
            json.addProperty("lootTable", value.lootTableRL.toString());
        };

        @Override
        public LootTableRewardGenerator deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
            return new LootTableRewardGenerator(IEntityTarget.getByName(GsonHelper.getAsString(json, "target")), ResourceLocation.fromNamespaceAndPath(GsonHelper.getAsString(json, "lootTable")));
        };

    };
    
};
