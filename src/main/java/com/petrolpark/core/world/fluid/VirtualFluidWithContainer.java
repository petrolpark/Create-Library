package com.petrolpark.core.world.fluid;

import javax.annotation.Nonnull;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public class VirtualFluidWithContainer extends BaseFlowingFluid {

    public static final VirtualFluidWithContainer createSource(BaseFlowingFluid.Properties properties, ItemLike bucketItem) {
		return new VirtualFluidWithContainer(properties, true, bucketItem);
	};

	public static final VirtualFluidWithContainer createFlowing(BaseFlowingFluid.Properties properties, ItemLike containerItem) {
		return new VirtualFluidWithContainer(properties, false, containerItem);
	};

    protected final boolean source;
    public final ItemLike containerItem;
	protected Boolean containerIsBucket = null;

    public VirtualFluidWithContainer(BaseFlowingFluid.Properties properties, boolean source, ItemLike bucketItem) {
        super(properties);
        this.source = source;
        this.containerItem = bucketItem;
    };

    @Override
	public Fluid getSource() {
		if (source) return this;
		return super.getSource();
	};

    @Override
	public Fluid getFlowing() {
		if (source) return super.getFlowing();
		return this;
	};

	@Override
	public Item getBucket() {
		if (containerIsBucket == null) containerIsBucket = containerItem.asItem().getClass() == BucketItem.class;
		return containerIsBucket ? containerItem.asItem() : Items.AIR;
	};

	@Override
	protected BlockState createLegacyBlock(@Nonnull FluidState state) {
		return Blocks.AIR.defaultBlockState();
	};

    @Override
    public int getAmount(@Nonnull FluidState state) {
        return 0;
    };

    @Override
    public boolean isSource(@Nonnull FluidState state) {
        return source;
    };
    
};
