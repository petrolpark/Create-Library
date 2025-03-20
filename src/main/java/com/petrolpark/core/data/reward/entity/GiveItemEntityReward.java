package com.petrolpark.core.data.reward.entity;

import java.util.List;
import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

public class GiveItemEntityReward extends AbstractGiveItemsEntityReward {

    public static final MapCodec<GiveItemEntityReward> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            ItemStack.CODEC.fieldOf("item").forGetter(GiveItemEntityReward::getStack)
        ).and(lateItemFunctionsField(instance).t1())
        .apply(instance, GiveItemEntityReward::new)
    );

    protected final ItemStack stack;

    public GiveItemEntityReward(ItemStack stack, List<LootItemFunction> functions) {
        super(functions);
        this.stack = stack;
    };

    public ItemStack getStack() {
        return stack;
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
    }

    @Override
    public EntityRewardType getType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getType'");
    };
    
};
