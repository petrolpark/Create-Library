package petrolpark.mc.library.compat.create.core.world.dough;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import petrolpark.mc.library.compat.create.RequiresCreate;
import petrolpark.mc.library.compat.create.core.world.dough.client.DoughRenderer;
import petrolpark.mc.library.compat.create.core.world.item.transported.DirectionalTransportedItemStack;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreateDataComponentTypes;

@RequiresCreate
public class DoughTransportedItemStack extends DirectionalTransportedItemStack {

    public DoughData doughData;
    public final DoughRenderer renderer = new DoughRenderer();

    protected final BlockState referenceState; // Needed for model cache

    public DoughTransportedItemStack(ItemStack stack) {
        this(stack, DoughData.get(stack));
    };

    protected DoughTransportedItemStack(ItemStack stack, DoughData doughData) {
        super(stack);
        this.doughData = doughData;
        if (doughData != null) renderer.setFrom(doughData);

        referenceState = stack.getItem() instanceof BlockItem blockItem ? blockItem.getBlock().defaultBlockState() : Blocks.AIR.defaultBlockState();
    };
    
    public DoughTransportedItemStack update(DoughData modified) {
        stack.set(PetrolparkCreateDataComponentTypes.DOUGH, modified);
        renderer.update(modified);
        if (doughData != null && doughData.width() != modified.width() || doughData.length() != modified.length() || doughData.thickness() != modified.thickness()) renderer.rollingProgress = renderer.oldRollingProgress = 0f;
        doughData = modified;
        return this;
    };

    @Override
    public DoughTransportedItemStack copy() {
        return copy(this, s -> new DoughTransportedItemStack(s, doughData));
    };

    @Override
    public void tick(Level level) {
        renderer.tick(level);
    };

    @Override
    public boolean render(PoseStack ms, MultiBufferSource buffer, float partialTicks, int light, int overlay) {
        ms.translate(-0.5f, -2 / 16f, -0.5f);
        renderer.render(referenceState, partialTicks, ms, buffer.getBuffer(RenderType.solid()), light);
        return true;
    };

    @Override
    public void deserializeNBT(CompoundTag nbt, HolderLookup.Provider registries) {
        super.deserializeNBT(nbt, registries);
        renderer.rollingProgress = nbt.getFloat("rolling_progress");
        renderer.oldRollingProgress = nbt.getFloat("old_rolling_progress");
        if (renderer.oldRollingProgress < 1f) {
            renderer.oldWidth = nbt.getByte("old_width");
            renderer.oldLength = nbt.getByte("old_length");
            renderer.oldThickness = nbt.getByte("old_thickness");
        };
    };

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider registries) {
        final CompoundTag nbt = super.serializeNBT(registries);
        nbt.putFloat("rolling_progress", renderer.rollingProgress);
        nbt.putFloat("old_rolling_progress", renderer.oldRollingProgress);
        if (renderer.oldRollingProgress < 1f) {
            nbt.putByte("old_width", renderer.oldWidth);
            nbt.putByte("old_length", renderer.oldLength);
            nbt.putFloat("old_thickness", renderer.oldThickness);
        };
        return nbt;
    };
    
};
