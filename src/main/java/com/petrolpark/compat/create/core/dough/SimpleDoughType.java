package com.petrolpark.compat.create.core.dough;

import com.mojang.serialization.MapCodec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class SimpleDoughType implements IDoughType<SimpleDough> {

    public final boolean cuttable;

    protected final SimpleDough doughUnit = new SimpleDough(this);
    protected final MapCodec<SimpleDough> codec = MapCodec.unit(doughUnit);
    protected final StreamCodec<ByteBuf, SimpleDough> streamCodec = StreamCodec.unit(doughUnit);

    public SimpleDoughType(boolean cuttable) {
        this.cuttable = cuttable;
    };

    @Override
    public MapCodec<SimpleDough> codec() {
        return codec;
    };

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, SimpleDough> streamCodec() {
        return streamCodec;
    };
    

};
