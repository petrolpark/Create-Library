package com.petrolpark.core.recipe.recycling;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.util.BigItemStack;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

public final class RecyclingOutput {

    public static final Codec<RecyclingOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ItemStack.SINGLE_ITEM_CODEC.fieldOf("item").forGetter(RecyclingOutput::getItem),
        Codec.DOUBLE.fieldOf("expectedCount").forGetter(RecyclingOutput::getExpectedCount)
    ).apply(instance, RecyclingOutput::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RecyclingOutput> STREAM_CODEC = StreamCodec.composite(
        ItemStack.STREAM_CODEC, RecyclingOutput::getItem,
        ByteBufCodecs.DOUBLE, RecyclingOutput::getExpectedCount,
        RecyclingOutput::new
    );
    
    /**
     * {@link ItemStack#getCount() Count} is ignored.
     */
    protected ItemStack item;
    protected double expectedCount;

    public RecyclingOutput(ItemStack stack) {
        this(stack, stack.getCount());
    };

    public RecyclingOutput(ItemStack stack, double expectedCount) {
        this.item = stack.copyWithCount(1);
        this.expectedCount = expectedCount;
    };

    public RecyclingOutput(BigItemStack bigStack) {
        this(bigStack.getSingleItemStack(), bigStack.getCount());
    };

    protected ItemStack getItem() {
        return item;
    };

    protected double getExpectedCount() {
        return expectedCount;
    };

    public double getExpectedRemainder() {
        return expectedCount - (long)expectedCount;
    };

    public RecyclingOutput multiply(double multiplier) {
        expectedCount *= multiplier;
        return this;
    };

    public RecyclingOutput copy() {
        return new RecyclingOutput(item.copy(), expectedCount);
    };

    public BigItemStack rollStack(double multiplier, RandomSource random) {
        int amount = (int)(expectedCount * multiplier);
        return new BigItemStack(item, amount + random.nextFloat() > expectedCount * multiplier - (float)amount ? 1 : 0);
    };

    public int compareTo(RecyclingOutput recyclingOutput) {
        return recyclingOutput.item.hashCode() - item.hashCode();
    };

};
