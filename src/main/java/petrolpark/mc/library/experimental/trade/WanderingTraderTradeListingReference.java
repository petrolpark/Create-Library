package petrolpark.mc.library.experimental.trade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import petrolpark.mc.library.registry.PetrolparkTradeListingReferenceTypes;
import petrolpark.mc.library.util.codec.CodecHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;

public record WanderingTraderTradeListingReference(boolean rare, int index) implements ITradeListingReference {

    public static final MapCodec<WanderingTraderTradeListingReference> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.fieldOf("rare").forGetter(WanderingTraderTradeListingReference::rare),
            CodecHelper.POS_INT.fieldOf("index").forGetter(WanderingTraderTradeListingReference::index)
        ).apply(instance, WanderingTraderTradeListingReference::new));

    public static final StreamCodec<ByteBuf, WanderingTraderTradeListingReference> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, WanderingTraderTradeListingReference::rare,
        ByteBufCodecs.INT, WanderingTraderTradeListingReference::index,
        WanderingTraderTradeListingReference::new
    );

    @Override
    public ItemListing get() {
        ItemListing[] listings = VillagerTrades.WANDERING_TRADER_TRADES.get(rare() ? 2 : 1);
        if (index() < listings.length) return listings[index()];
        return FAILURE;
    };

    @Override
    public Type getType() {
        return PetrolparkTradeListingReferenceTypes.WANDERING_TRADER.get();
    };
};
