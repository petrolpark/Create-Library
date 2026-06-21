package petrolpark.mc.library.core.world.item.wooden;

import javax.annotation.Nonnull;

import petrolpark.mc.library.core.world.block.entity.BlockEntityBase;
import petrolpark.mc.library.registry.PetrolparkDataComponentTypes;
import petrolpark.mc.library.util.WoodHelper;
import petrolpark.mc.library.util.WoodHelper.Wood;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class WoodenBlockEntity extends BlockEntityBase {

    protected Wood wood;

    public WoodenBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    };

    @Override
    protected void read(CompoundTag tag, Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        wood = Wood.CODEC.parse(NbtOps.INSTANCE, tag.get("Wood")).result().orElse(WoodHelper.OAK);
    };

    @Override
    protected void write(CompoundTag tag, Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.put("Wood", Wood.CODEC.encodeStart(NbtOps.INSTANCE, wood).getOrThrow());
    };

    @Override
    protected void collectImplicitComponents(@Nonnull DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(PetrolparkDataComponentTypes.WOOD, wood);
    };

    @Override
    protected void applyImplicitComponents(@Nonnull DataComponentInput componentInput) {
        wood = componentInput.get(PetrolparkDataComponentTypes.WOOD);
    };

    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(WoodenModel.WOOD_PROPERTY, wood).build();
    };
    
};
