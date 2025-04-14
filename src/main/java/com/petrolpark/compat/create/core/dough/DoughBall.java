package com.petrolpark.compat.create.core.dough;

import java.util.ArrayList;
import java.util.List;

import com.petrolpark.compat.create.CreateRegistries;
import com.petrolpark.util.NBTHelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;

public class DoughBall {

    public static final float THICKNESS_AREA_SCALE = 4f; // One tile of width/length is four units of thickness
    
    // Defining fields
    public final DoughType dough;
    protected float thickness = 4f;
    protected byte width = 1;
    protected byte length = 1;
    protected List<CutEntry> cuts = new ArrayList<>();

    // Internal fields
    protected int cutTiles = 0;

    public DoughBall(DoughType dough, float thickness, byte width, byte length, List<CutEntry> cuts) {
        this.dough = dough;
        this.thickness = thickness;
        this.width = width;
        this.length = length;
        this.cuts = cuts;
        //TODO modify cuttiles
    };

    public DoughBall(CompoundTag tag) {
        this(NBTHelper.readRegistryObject(tag, "Dough", CreateRegistries.Keys.DOUGH_TYPE), tag.getFloat("Thickness"), tag.getByte("Width"), tag.getByte("Length"), NBTHelper.readCompoundList(tag.getList("Cuts", Tag.TAG_COMPOUND), CutEntry::new));
    };

    public static DoughBall get(ItemStack stack) {
        if (!(stack.getItem() instanceof DoughBallItem)) return null;
        // return new DoughBall(stack.get(PetrolparkDataComponents.DOUGH));
        return null; //TODO
    };

    public boolean hasBeenCut() {
        return cutTiles != 0;
    };

    public record CutEntry(DoughCut cut, byte x, byte y, Rotation rotation) {

        public CutEntry(CompoundTag tag) {
            this(NBTHelper.readRegistryObject(tag, "Shape", CreateRegistries.Keys.DOUGH_CUT), tag.getByte("x"), tag.getByte("y"), NBTHelper.readEnum(tag, "Rotation", Rotation.class));
        };
    };
};
