package com.petrolpark.compat.create.core.dough.type;

import com.mojang.serialization.MapCodec;
import com.petrolpark.compat.create.core.dough.IDough;
import com.petrolpark.compat.create.core.dough.IDoughType;

import io.netty.buffer.ByteBuf;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public final class SimpleDoughType implements IDough, IDoughType<SimpleDoughType> {

    protected final MapCodec<SimpleDoughType> codec = MapCodec.unit(this);
    protected final StreamCodec<ByteBuf, SimpleDoughType> streamCodec = StreamCodec.unit(this);

    protected final ResourceLocation id;

    protected final float minimumThickness;
    protected final boolean cuttable;
    protected final boolean toppable;

    protected final String translationKey;
    protected final ResourceLocation textureLocation;
    protected final int tint;

    public SimpleDoughType(ResourceLocation id) {
        this(id, 0.5f, true, true, 0xFFFFFFFF);
    };

    public SimpleDoughType(ResourceLocation id, float minimumThickness, boolean cuttable, boolean toppable, int tint) {
        this.id = id;

        this.minimumThickness = minimumThickness;
        this.cuttable = cuttable;
        this.toppable = toppable;

        this.translationKey = Util.makeDescriptionId("dough", id);
        this.textureLocation = id.withPrefix("block/");
        this.tint = tint;
    };

    @Override
    public float minimumThickness() {
        return minimumThickness;
    };

    @Override
    public boolean cuttable() {
        return cuttable;
    };

    @Override
    public boolean toppable() {
        return toppable;
    };

    @Override
    public Component name() {
        return Component.translatable(translationKey);
    };

    @Override
    public ResourceLocation textureLocation() {
        return textureLocation;
    };

    @Override
    public int tint() {
        return tint;
    };

    @Override
    public SimpleDoughType getType() {
        return this;
    };

    @Override
    public MapCodec<SimpleDoughType> codec() {
        return codec;
    };

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, SimpleDoughType> streamCodec() {
        return streamCodec;
    };
    
};
