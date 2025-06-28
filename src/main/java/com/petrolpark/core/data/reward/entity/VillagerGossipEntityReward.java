package com.petrolpark.core.data.reward.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.core.data.IEntityTarget;
import com.petrolpark.core.data.loot.numberprovider.NumberEstimate;
import com.petrolpark.util.Lang;
import com.petrolpark.util.Lang.IndentedTooltipBuilder;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public record VillagerGossipEntityReward(GossipType type, NumberProvider value, IEntityTarget target) implements IEntityReward {

    public static final MapCodec<VillagerGossipEntityReward> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        GossipType.CODEC.fieldOf("gossip_type").forGetter(VillagerGossipEntityReward::type),
        NumberProviders.CODEC.fieldOf("value").forGetter(VillagerGossipEntityReward::value),
        IEntityTarget.CODEC.fieldOf("target").forGetter(VillagerGossipEntityReward::target)
    ).apply(instance, VillagerGossipEntityReward::new));

    @Override
    public void reward(Entity entity, LootContext context, float multiplier) {
        Entity target = target().get(context);
        if (entity instanceof Villager villager && target != null) {
            villager.getGossips().add(target.getUUID(), type, value.getInt(context));
        };
    };

    @Override
    public EntityRewardType getType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getType'");
    };

    @Override
    public void render(GuiGraphics graphics) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'render'");
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder builder) {
        NumberEstimate amount = NumberEstimate.get(value());
        if (amount.unknown()) builder.add(translate("unknown_amount", Lang.gossipType(type), target().getName()));
        else builder.add(translateSimple(amount.getIntComponent(), Lang.gossipType(type), target.getName()));
    };
    
};
