package com.petrolpark.compat.create.core.dough;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.compat.create.PetrolparkCreateDataComponentTypes;
import com.petrolpark.util.CodecHelper;
import com.petrolpark.util.Neither;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;

public record DoughData(IDough dough, float thickness, byte width, byte length, Neither<DoughData.Cuts, DoughData.Toppings> decoration) {

    public static final byte MAX_WIDTH = (byte)4;

    public static final Codec<DoughData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        IDough.CODEC.fieldOf("dough").forGetter(DoughData::dough),
        Codec.floatRange(0f, 16f).fieldOf("thickness").forGetter(DoughData::thickness),
        CodecHelper.byteRanged((byte)1, MAX_WIDTH).fieldOf("width").forGetter(DoughData::width),
        CodecHelper.byteRanged((byte)1, MAX_WIDTH).fieldOf("length").forGetter(DoughData::length),
        Neither.fieldCodec(DoughData.Cuts.CODEC, DoughData.Toppings.CODEC, "decoration").forGetter(DoughData::decoration)
    ).apply(instance, DoughData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DoughData> STREAM_CODEC = StreamCodec.composite(
        IDough.STREAM_CODEC, DoughData::dough,
        ByteBufCodecs.FLOAT, DoughData::thickness,
        ByteBufCodecs.BYTE, DoughData::width,
        ByteBufCodecs.BYTE, DoughData::length,
        Neither.streamCodec(DoughData.Cuts.STREAM_CODEC, DoughData.Toppings.STREAM_CODEC), DoughData::decoration,
        DoughData::new
    );

    public boolean isRollable(boolean lengthwise) {
        return decoration().isEmpty() && (lengthwise ? length() < MAX_WIDTH : width() < MAX_WIDTH);
    };

    public DoughData rolled(boolean lengthwise) {
        return lengthwise ? rolled(width(), (byte)(length() + 1)) : rolled((byte)(width() + 1), length());
    };

    public DoughData rolled(byte width, byte length) {
        if (width >= 1 && width <= MAX_WIDTH && length >= 1 && length <= MAX_WIDTH && decoration().isEmpty()) return new DoughData(dough(), width() * length() * thickness() / ((float)width * (float)length), width, length, decoration());
        return this;
    };

    /**
     * If there are Toppings, preserves them.
     * Otherwise, turns back into a single ball to be rolled again.
     */
    public DoughData forItem() {
        if (decoration.isRight()) return this; // Preserve Toppings
        return new DoughData(dough(), thickness() * remainingArea(), (byte)1, (byte)1, Neither.neither());
    };

    public float remainingArea() {
        return width() * length() - (float)decoration.left().stream()
            .map(DoughData.Cuts::cuts)
            .flatMap(List::stream)
            .map(DoughData.Cuts.Entry::cut)
            .map(Holder::value)
            .mapToDouble(DoughCut::area)
            .sum();
    };

    @Nullable
    public static final DoughData get(ItemStack stack) {
        return stack.get(PetrolparkCreateDataComponentTypes.DOUGH);
    };
    
    public record Cuts(List<DoughData.Cuts.Entry> cuts) {
        
        public static final Codec<DoughData.Cuts> CODEC = CodecHelper.singleField(DoughData.Cuts.Entry.CODEC.listOf(), "cuts", DoughData.Cuts::cuts, DoughData.Cuts::new);
        public static final StreamCodec<RegistryFriendlyByteBuf, DoughData.Cuts> STREAM_CODEC = DoughData.Cuts.Entry.STREAM_CODEC.apply(ByteBufCodecs.list()).map(DoughData.Cuts::new, DoughData.Cuts::cuts);

        public record Entry(Holder<DoughCut> cut, byte x, byte y, Rotation rotation) {

            public static final Codec<DoughData.Cuts.Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                DoughCut.CODEC.fieldOf("cut").forGetter(DoughData.Cuts.Entry::cut),
                CodecHelper.byteRanged((byte)0, (byte)3).fieldOf("x").forGetter(DoughData.Cuts.Entry::x),
                CodecHelper.byteRanged((byte)0, (byte)3).fieldOf("y").forGetter(DoughData.Cuts.Entry::y),
                Rotation.CODEC.fieldOf("rotation").forGetter(DoughData.Cuts.Entry::rotation)
            ).apply(instance, DoughData.Cuts.Entry::new));

            public static final StreamCodec<RegistryFriendlyByteBuf, DoughData.Cuts.Entry> STREAM_CODEC = StreamCodec.composite(
                DoughCut.STREAM_CODEC, DoughData.Cuts.Entry::cut,
                ByteBufCodecs.BYTE, DoughData.Cuts.Entry::x,
                ByteBufCodecs.BYTE, DoughData.Cuts.Entry::y,
                CodecHelper.ROTATION_STREAM_CODEC, DoughData.Cuts.Entry::rotation,
                DoughData.Cuts.Entry::new
            );
        };
    };

    public record Toppings(List<DoughData.Toppings.Entry> toppings) {

        public static final Codec<DoughData.Toppings> CODEC = CodecHelper.singleField(DoughData.Toppings.Entry.CODEC.listOf(), "toppings", DoughData.Toppings::toppings, DoughData.Toppings::new);
        public static final StreamCodec<RegistryFriendlyByteBuf, DoughData.Toppings> STREAM_CODEC = DoughData.Toppings.Entry.STREAM_CODEC.apply(ByteBufCodecs.list()).map(DoughData.Toppings::new, DoughData.Toppings::toppings);
    
        public record Entry() {
        
            public static final Codec<DoughData.Toppings.Entry> CODEC = Codec.unit(Entry::new); //TODO
            public static final StreamCodec<RegistryFriendlyByteBuf, DoughData.Toppings.Entry> STREAM_CODEC = StreamCodec.unit(new Entry()); //TODO
        };
    };
};
