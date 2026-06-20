package com.petrolpark.compat.create.core.dough.type;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.petrolpark.compat.create.core.dough.IDough;
import com.petrolpark.compat.create.core.dough.IDoughType;
import com.petrolpark.util.CodecHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class CookableDough implements IDough, ICookableDough {

    protected CookableDough.Type type;

    protected final int timesCooked;

    protected final float minimumThickness;
    protected final boolean cuttable;
    protected final boolean toppable;

    protected final String translationKey;
    protected final ResourceLocation textureLocation;
    protected final int tint;

    public CookableDough(CookableDough.Type type, int timesCooked) {
        this.type = type;
        this.timesCooked = timesCooked;

        this.minimumThickness = getType().getMinimumThickness(timesCooked);
        this.cuttable = getType().isCuttable(timesCooked);
        this.toppable = getType().isToppable(timesCooked);

        this.translationKey = getType().getTranslationKey(timesCooked);
        this.textureLocation = getType().getTextureLocation(timesCooked);
        this.tint = getType().getTint(timesCooked);
    };

    @Override
    public int getTimesCooked() {
        return timesCooked;
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
    public String uniqueString() {
        // TODO Auto-generated method stub
        return null;
    };

    @Override
    public CookableDough.Type getType() {
        return type;
    };

    public static abstract class Type implements IDoughType<CookableDough> {

        protected final List<CookableDough> doughs = new ArrayList<>(getMaxCookingTimes() + 1);

        protected final MapCodec<CookableDough> codec = CodecHelper.singleFieldMap(Codec.intRange(0, getMaxCookingTimes()), "cooked", CookableDough::getTimesCooked, this::getDough);
        protected final StreamCodec<ByteBuf, CookableDough> streamCodec = ByteBufCodecs.INT.map(this::getDough, CookableDough::getTimesCooked);

        public int getMaxCookingTimes() {
            return 1;
        };

        public CookableDough getDough(int timesCooked) {
            if (timesCooked < 0 || timesCooked > getMaxCookingTimes());
            if (doughs.get(timesCooked) == null) doughs.set(timesCooked, new CookableDough(this, timesCooked));
            return doughs.get(timesCooked);
        };

        public float getMinimumThickness(int timesCooked) {
            return 0.25f;
        };

        public boolean isCuttable(int timesCooked) {
            return true;
        };

        public boolean isToppable(int timesCooked) {
            return true;
        };

        public abstract String getTranslationKey(int timesCooked);

        public abstract ResourceLocation getTextureLocation(int timesCooked);

        public int getTint(int timesCooked) {
            return 0xFFFFFFFF;
        };

        @Override
        public MapCodec<CookableDough> codec() {
            return codec;
        };

        @Override
        public StreamCodec<ByteBuf, CookableDough> streamCodec() {
            return streamCodec;
        };

    };
    
};
