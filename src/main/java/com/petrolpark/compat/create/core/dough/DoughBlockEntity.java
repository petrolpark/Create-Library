package com.petrolpark.compat.create.core.dough;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.petrolpark.compat.create.PetrolparkCreateBlockEntityTypes;
import com.petrolpark.compat.create.PetrolparkCreateBlocks;
import com.petrolpark.compat.create.PetrolparkCreateDataComponentTypes;
import com.petrolpark.compat.create.PetrolparkCreateDoughTypes;
import com.petrolpark.util.Neither;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.model.data.ModelData;

public class DoughBlockEntity extends SmartBlockEntity implements Nameable {

    protected DoughData doughData;
    protected VoxelShape shape = null;

    // Client stuff
    protected final DoughRenderer renderingData = new DoughRenderer();

    protected DoughBlockEntity(DoughData data) {
        this(PetrolparkCreateBlockEntityTypes.DOUGH.get(), BlockPos.ZERO, PetrolparkCreateBlocks.DOUGH.getDefaultState());
        this.doughData = data;
        renderingData.setFrom(data);
    };

    public DoughBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }; 

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        
    };

    @Override
    public void tick() {
        super.tick();
        if (doughData == null) { // Templrary
            doughData = new DoughData(PetrolparkCreateDoughTypes.TEST.get(), 4f, (byte)1, (byte)1, Neither.neither()); // TEMP
            onDoughChanged();
        };
        renderingData.tick(getLevel());
    };

    public Optional<VoxelShape> getVoxelShape() {
        if (shape == null && doughData != null) {
            final float width = doughData.width() * DoughRenderer.WIDTH_UNIT, length = doughData.length() * DoughRenderer.WIDTH_UNIT;
            shape = Block.box((1f - width) * 8f, 0f, (1f - length) * 8f, (1f + width) * 8f, doughData.thickness(), (1f + length) * 8f);
        };
        return Optional.ofNullable(shape);
    };

    public void modifyDough(UnaryOperator<DoughData> operator) {
        final DoughData modified = operator.apply(doughData);
        if (!modified.equals(doughData)) {
            if (doughData.width() != modified.width() || doughData.length() != modified.length() || doughData.thickness() != modified.thickness()) renderingData.rollingProgress = 0f;
            doughData = modified;
            onDoughChanged();
        };
    };

    public void onDoughChanged() {
        renderingData.update(doughData);
        shape = null;
        setChanged();
    };

    @Override
    public Component getName() {
        final Component customName = getCustomName();
        return customName == null ? getBlockState().getBlock().getName() : customName;
    };

    /**
     * Only really implemented so Jade shows the right name
     */
    @Override
    @Nullable
    public Component getCustomName() {
        return doughData == null ? null : doughData.dough().name();
    };

    @Override
    public ModelData getModelData() {
        return doughData == null ? ModelData.EMPTY : ModelData.builder().with(DoughModel.DOUGH_PROPERTY, renderingData).build();
    };

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);

        doughData = DoughData.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("Dough")).resultOrPartial().orElse(null);
        renderingData.setFrom(doughData);
    };

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);

        if (doughData != null) DoughData.CODEC.encodeStart(NbtOps.INSTANCE, doughData).ifSuccess(t -> tag.put("Dough", t));
    };

    @Override
    protected void applyImplicitComponents(@Nonnull DataComponentInput componentInput) {
        doughData = componentInput.get(PetrolparkCreateDataComponentTypes.DOUGH);
        renderingData.setFrom(doughData);
    };

    @Override
    protected void collectImplicitComponents(@Nonnull DataComponentMap.Builder components) {
        components.set(PetrolparkCreateDataComponentTypes.DOUGH, doughData == null ? null : doughData.forItem());
    };
    
};
