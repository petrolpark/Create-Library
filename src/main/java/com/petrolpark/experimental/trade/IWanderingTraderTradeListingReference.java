package com.petrolpark.experimental.trade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.util.codec.CodecHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface IWanderingTraderTradeListingReference extends ITradeListingReference {

    public static <REF extends IWanderingTraderTradeListingReference> MapCodec<REF> codec(Factory<REF> factory) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.fieldOf("rare").forGetter(IWanderingTraderTradeListingReference::rare),
            CodecHelper.POS_INT.fieldOf("index").forGetter(IWanderingTraderTradeListingReference::index)
        ).apply(instance, factory::create));
    };

    public static <REF extends IWanderingTraderTradeListingReference> StreamCodec<ByteBuf, REF> streamCodec(Factory<REF> factory) {
        return StreamCodec.composite(
            ByteBufCodecs.BOOL, IWanderingTraderTradeListingReference::rare,
            ByteBufCodecs.INT, IWanderingTraderTradeListingReference::index,
            factory::create
        );
    };
    
    public boolean rare();

    public int index();

    @FunctionalInterface
    public static interface Factory<REF extends IWanderingTraderTradeListingReference> {
        public REF create(boolean rare, int index);
    };
};
