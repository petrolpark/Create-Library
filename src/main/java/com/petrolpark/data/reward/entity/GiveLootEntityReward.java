package com.petrolpark.data.reward.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.data.loot.ILootTableAccessor;
import com.petrolpark.data.reward.PetrolparkRewardTypes;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

public class GiveLootEntityReward extends AbstractGiveItemsEntityReward implements ILootTableAccessor {

    public static final MapCodec<GiveLootEntityReward> CODEC = RecordCodecBuilder.mapCodec(instance -> 
        ILootTableAccessor.lootTableField(instance)
        .and(lateItemFunctionsField(instance).t1())
        .apply(instance, GiveLootEntityReward::new)
    );

    protected final Either<ResourceKey<LootTable>, LootTable> lootTable;

    public GiveLootEntityReward(Either<ResourceKey<LootTable>, LootTable> lootTable, List<LootItemFunction> functions) {
        super(functions);
        this.lootTable = lootTable;
    };

    @Override
    public Either<ResourceKey<LootTable>, LootTable> lootTable() {
        return lootTable;
    };

    @Override
    public Stream<ItemStack> streamStacks(Entity recipient, LootContext context) {
        List<ItemStack> stacks = new ArrayList<>();
        getLootTable(context).getRandomItems(context, stacks::add);
        return stacks.stream();
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
        return PetrolparkRewardTypes.GIVE_LOOT.get();
    };
    
};
