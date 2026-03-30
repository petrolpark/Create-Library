package com.petrolpark.core.data.reward.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkRewardTypes;
import com.petrolpark.core.data.IEntityTarget;
import com.petrolpark.core.data.loot.numberprovider.NumberEstimate;
import com.petrolpark.util.Lang;
import com.petrolpark.util.Lang.IndentedTooltipBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.gossip.GossipContainer;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

/**
 * <p>{@code petrolpark:villager_gossip}</p>
 * 
 * {@link GossipContainer#add(java.util.UUID, GossipType, int) Add a Gossip} to the Entity, if they are a Villager.
 * 
 * Arguments:
 * <ul>
 * <li> {@code gossip_type} - The name of a {@link GossipType}
 * <li> {@code value} - A {@link NumberProvider} for the value of the Gossip
 * <li> {@code target} - A {@link IEntityTarget} specifying who the Gossip is about
 * </ul>
 * 
 * @author petrolpark
 */
public record VillagerGossipEntityReward(GossipType type, NumberProvider value, IEntityTarget target) implements IEntityReward {

    public static final ResourceLocation MAJOR_NEGATIVE_TEXTURE = Petrolpark.asResource("item/villager_gossip_major_negative");
    public static final ResourceLocation MINOR_NEGATIVE_TEXTURE = Petrolpark.asResource("item/villager_gossip_minor_negative");
    public static final ResourceLocation MINOR_POSITIVE_TEXTURE = Petrolpark.asResource("item/villager_gossip_minor_positive");
    public static final ResourceLocation MAJOR_POSITIVE_TEXTURE = Petrolpark.asResource("item/villager_gossip_major_positive");
    public static final ResourceLocation TRADING_TEXTURE = Petrolpark.asResource("item/villager_gossip_trading");

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
        return PetrolparkRewardTypes.GOSSIP.get();
    };

    @Override
    public void render(GuiGraphics graphics) {
        graphics.blit(0, 0, 0, 16, 16, Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(switch (type()) {
            case MAJOR_NEGATIVE -> MAJOR_NEGATIVE_TEXTURE;
            case MINOR_NEGATIVE -> MINOR_NEGATIVE_TEXTURE;
            case MINOR_POSITIVE -> MINOR_POSITIVE_TEXTURE;
            case MAJOR_POSITIVE -> MAJOR_POSITIVE_TEXTURE;
            case TRADING -> TRADING_TEXTURE;
        }));
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder builder) {
        NumberEstimate amount = NumberEstimate.get(value());
        if (amount.unknown()) builder.add(translate("unknown_amount", Lang.gossipType(type), target().getName()));
        else builder.add(translateSimple(amount.getIntComponent(), Lang.gossipType(type), target.getName()));
    };
    
};
