package petrolpark.mc.library.core.world.item.wooden;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.neoforged.neoforge.client.model.data.ModelData;
import petrolpark.mc.library.compat.create.RequiresCreate;
import petrolpark.mc.library.registry.PetrolparkDataComponentTypes;
import petrolpark.mc.library.util.WoodHelper;
import petrolpark.mc.library.util.WoodHelper.Wood;

@RequiresCreate
public class WoodenBlockEntityBehaviour extends BlockEntityBehaviour {

    protected Wood wood = WoodHelper.OAK;

    public static final BehaviourType<WoodenBlockEntityBehaviour> TYPE = new BehaviourType<>();

    public WoodenBlockEntityBehaviour(SmartBlockEntity be) {
        super(be);
    };

    public Wood getWood() {
        return wood;
    };

    @Override
    public void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        wood = tag.contains("wood") ? Wood.CODEC.parse(NbtOps.INSTANCE, tag.get("Wood")).result().orElse(WoodHelper.OAK) : WoodHelper.OAK;
    };

    @Override
    public void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.put("wood", Wood.CODEC.encodeStart(NbtOps.INSTANCE, wood).getOrThrow());
    };

    public void collectImplicitComponents(@Nonnull DataComponentMap.Builder components) {
        components.set(PetrolparkDataComponentTypes.WOOD, wood);
    };

    public void applyImplicitComponents(@Nonnull DataComponentInput componentInput) {
        wood = componentInput.get(PetrolparkDataComponentTypes.WOOD);
    };

    public ModelData getModelData() {
        return ModelData.builder().with(WoodenModel.WOOD_PROPERTY, wood).build();
    };

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    };

    @FunctionalInterface
    public interface DataComponentInput {

        @Nullable
        public <T> T get(DataComponentType<T> type);
    };
};
