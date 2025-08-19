package com.petrolpark.compat.create.core.dough;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.util.CodecHelper;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;

@ApiStatus.Experimental
public class DoughBall<DOUGH extends IDough<DOUGH>> {

    public static final Codec<DoughBall<?>> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        IDough.CODEC.fieldOf("dough").forGetter(DoughBall::getDough),
        Codec.floatRange(0f, 16f).fieldOf("thickness").forGetter(DoughBall::getThickness),
        CodecHelper.byteRanged((byte)0, (byte)3).fieldOf("width").forGetter(DoughBall::getWidth),
        CodecHelper.byteRanged((byte)0, (byte)3).fieldOf("length").forGetter(DoughBall::getLength),
        Codec.list(CutEntry.CODEC).fieldOf("cuts").forGetter(DoughBall::getCuts)
    ).apply(instance, DoughBall::create));

    public static final float THICKNESS_AREA_SCALE = 4f; // One tile of width/length is four units of thickness
    
    // Defining fields
    public final DOUGH dough;
    protected float thickness = 4f;
    protected byte width = 1;
    protected byte length = 1;
    protected List<CutEntry> cuts = new ArrayList<>();

    protected int cutTiles = 0;

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static final DoughBall<?> create(IDough<?> dough, float thickness, byte width, byte length, List<CutEntry> cuts) {
        return new DoughBall(dough, thickness, width, length, cuts);
    };

    public DoughBall(DOUGH dough, float thickness, byte width, byte length, List<CutEntry> cuts) {
        this.dough = dough;
        this.thickness = thickness;
        this.width = width;
        this.length = length;
        this.cuts = cuts;
        //TODO modify cuttiles
    };

    @Nullable
    public static DoughBall<?> get(ItemStack stack) {
        if (!(stack.getItem() instanceof DoughBallItem)) return null;
        // return new DoughBall(stack.get(PetrolparkDataComponents.DOUGH));
        return null; //TODO
    };

    public DOUGH getDough() {
        return dough;
    };

    public float getThickness() {
        return thickness;
    };

    public byte getWidth() {
        return width;
    };

    public byte getLength() {
        return length;
    };

    public List<CutEntry> getCuts() {
        return cuts;
    };

    public final boolean canBeCut() {
        return dough.canBeCut();
    };

    public final boolean hasBeenCut() {
        return cutTiles != 0;
    };

    public record CutEntry(Holder<DoughCut> cut, byte x, byte y, Rotation rotation) {

        public static final Codec<CutEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DoughCut.CODEC.fieldOf("cut").forGetter(CutEntry::cut),
            CodecHelper.byteRanged((byte)0, (byte)3).fieldOf("x").forGetter(CutEntry::x),
            CodecHelper.byteRanged((byte)0, (byte)3).fieldOf("y").forGetter(CutEntry::y),
            Rotation.CODEC.fieldOf("rotation").forGetter(CutEntry::rotation)
        ).apply(instance, CutEntry::new));

        //TODO validate x and y in context of how large the dough ball is
    };
};
