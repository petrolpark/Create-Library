package com.petrolpark.data.reward;

import java.util.stream.Stream;

import java.util.List;
import java.util.ArrayList;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.petrolpark.Petrolpark;
import com.petrolpark.data.IEntityTarget;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootTable;

public class GiveLootReward extends AbstractGiveEntityItemsReward {

    protected final ResourceLocation lootTableRL;

    public GiveLootReward(IEntityTarget target, ResourceLocation lootTableRL) {
        super(target);
        this.lootTableRL = lootTableRL;
    };

    @Override
    public Stream<ItemStack> streamStacks(Entity recipient, LootContext context) {
        LootTable table = context.getResolver().getLootTable(lootTableRL);
        if (table.equals(LootTable.EMPTY) && lootTableRL.equals(LootDataManager.EMPTY_LOOT_TABLE_KEY.location())) Petrolpark.LOGGER.warn("Unknown Loot Table in Give Loot Reward: "+lootTableRL);
        List<ItemStack> stacks = new ArrayList<>();
        table.getRandomItems(context, stacks::add);
        return stacks.stream();
    };

    @Override
    public void render(GuiGraphics graphics) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'render'");
    };

    @Override
    public Component getName() {
        return Component.translatable(Util.makeDescriptionId("loot", lootTableRL));
    };

    @Override
    public RewardType getType() {
        return RewardTypes.GIVE_LOOT.get();
    };

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<GiveLootReward> {

        @Override
        public void serialize(JsonObject json, GiveLootReward value, JsonSerializationContext serializationContext) {
            json.addProperty("target", value.target.getSerializedName());
            json.addProperty("lootTable", value.lootTableRL.toString());
        };

        @Override
        public GiveLootReward deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
            return new GiveLootReward(IEntityTarget.getByName(GsonHelper.getAsString(json, "target")), ResourceLocation.fromNamespaceAndPath(GsonHelper.getAsString(json, "lootTable")));
        };

    };
    
};
