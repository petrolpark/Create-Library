package com.petrolpark.data.reward;

import java.util.Optional;
import java.util.stream.Stream;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import com.petrolpark.data.IEntityTarget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class GiveItemReward extends AbstractGiveEntityItemsReward {

    protected final ItemStack stack;

    public GiveItemReward(IEntityTarget target, ItemStack stack) {
        super(target);
        this.stack = stack;
    };

    @Override
    public Stream<ItemStack> streamStacks(Entity recipient, LootContext context) {
        return Stream.of(stack);
    };

    @Override
    public void render(GuiGraphics graphics) {
        graphics.renderItem(stack, 0, 0);
    };

    @Override
    public Component getName() {
        return stack.getDisplayName();
    };

    @Override
    public RewardType getType() {
        return RewardTypes.GIVE_ITEM.get();
    };

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<GiveItemReward> {

        @Override
        public void serialize(JsonObject json, GiveItemReward value, JsonSerializationContext serializationContext) {
            json.addProperty("target", value.target.getSerializedName());
            ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, value.stack)
                .resultOrPartial(err -> {throw new IllegalStateException(err);})
                .ifPresent(element -> json.add("item", element));
        };

        @Override
        public GiveItemReward deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
            IEntityTarget target = IEntityTarget.getByName(GsonHelper.getAsString(json, "target"));
            Optional<Pair<ItemStack, JsonElement>> op = ItemStack.CODEC.decode(JsonOps.INSTANCE, GsonHelper.getAsJsonObject(json, "item"))
                .resultOrPartial(err -> {throw new JsonSyntaxException(err);});
            if (!op.isPresent()) throw new JsonSyntaxException("Give Item Reward specifies no or invalid Item Stack");
            return new GiveItemReward(target, op.get().getFirst());
        };

    };
    
};
