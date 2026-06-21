package petrolpark.mc.library.compat.create.core.world.block.chainConveyor;

import java.util.List;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;

import net.minecraft.world.item.ItemStack;

public interface IArmBlockEntityDuck {
    
    public List<ArmInteractionPoint> getInputs();

    public List<ArmInteractionPoint> getOutputs();

    public float getChasedPointProgress();

    public void setChasedPointProgress(float progress);

    public void setChasedPointIndex(int index);

    public ItemStack getHeldItem();

    public void setHeldItem(ItemStack item);

    public ArmBlockEntity.Phase getPhase();

    public void setPhase(ArmBlockEntity.Phase phase);

    public ArmInteractionPoint invokeGetTargetedInteractionPoint();

    public void invokeSearchForItem();

    public void invokeSearchForDestination();
};
