package petrolpark.mc.library.core.data.reward.entity;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.gossip.GossipContainer;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.data.IEntityTarget;
import petrolpark.mc.library.core.data.numberProvider.NumberEstimate;
import petrolpark.mc.library.core.data.reward.info.INamedRewardInfo;
import petrolpark.mc.library.registry.PetrolparkRewardTypes;
import petrolpark.mc.library.util.Lang;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;
import petrolpark.mc.library.util.codec.CodecHelper;

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
@ParametersAreNonnullByDefault
public record VillagerGossipEntityReward(GossipType type, NumberProvider value, IEntityTarget target) implements IEntityReward {

    public static final MapCodec<VillagerGossipEntityReward> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        GossipType.CODEC.fieldOf("gossip_type").forGetter(VillagerGossipEntityReward::type),
        NumberProviders.CODEC.fieldOf("value").forGetter(VillagerGossipEntityReward::value),
        IEntityTarget.STRICT_CODEC.fieldOf("target").forGetter(VillagerGossipEntityReward::target)
    ).apply(instance, VillagerGossipEntityReward::new));

    @Override
    public boolean reward(Entity entity, LootContext context, float multiplier, boolean simulate) {
        final Entity target = target().get(context);
        if (!(entity instanceof Villager villager) || target == null) return false;
        if (!simulate) villager.getGossips().add(target.getUUID(), type, value().getInt(context));
        return true;
    };

    @Override
    public VillagerGossipEntityReward.Info info() {
        return new VillagerGossipEntityReward.Info(type(), NumberEstimate.get(value()), target());
    };

    public record Info(GossipType type, NumberEstimate amount, IEntityTarget target) implements INamedRewardInfo {

        public static final ResourceLocation MAJOR_NEGATIVE_TEXTURE = Petrolpark.asResource("item/gui/villager_gossip_major_negative");
        public static final ResourceLocation MINOR_NEGATIVE_TEXTURE = Petrolpark.asResource("item/gui/villager_gossip_minor_negative");
        public static final ResourceLocation MINOR_POSITIVE_TEXTURE = Petrolpark.asResource("item/gui/villager_gossip_minor_positive");
        public static final ResourceLocation MAJOR_POSITIVE_TEXTURE = Petrolpark.asResource("item/gui/villager_gossip_major_positive");
        public static final ResourceLocation TRADING_TEXTURE = Petrolpark.asResource("item/gui/villager_gossip_trading");

        public static final MapCodec<VillagerGossipEntityReward.Info> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                GossipType.CODEC.fieldOf("gossip_type").forGetter(VillagerGossipEntityReward.Info::type),
                NumberEstimate.fieldCodec("amount").forGetter(VillagerGossipEntityReward.Info::amount),
                IEntityTarget.STRICT_CODEC.fieldOf("target").forGetter(VillagerGossipEntityReward.Info::target)
            ).apply(instance, VillagerGossipEntityReward.Info::new)
        );

        public static final StreamCodec<ByteBuf, VillagerGossipEntityReward.Info> STREAM_CODEC = StreamCodec.composite(
            CodecHelper.GOSSIP_TYPE_STREAM, VillagerGossipEntityReward.Info::type,
            NumberEstimate.STREAM_CODEC, VillagerGossipEntityReward.Info::amount,
            IEntityTarget.STREAM_CODEC, VillagerGossipEntityReward.Info::target,
            VillagerGossipEntityReward.Info::new
        );

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
            if (amount().unknown() || amount().equals(NumberEstimate.POSITIVE)) builder.add(translate("unknown_amount", Lang.gossipType(type()), target().getName()));
            else builder.add(translateSimple(amount().getIntComponent(), Lang.gossipType(type()), target().getName()));
        };

        @Override
        public EntityRewardAndInfoType getRewardInfoType() {
            return PetrolparkRewardTypes.ENTITY_GOSSIP.get();
        };
    };

    @Override
    public EntityRewardAndInfoType getType() {
        return PetrolparkRewardTypes.ENTITY_GOSSIP.get();
    };
    
};
