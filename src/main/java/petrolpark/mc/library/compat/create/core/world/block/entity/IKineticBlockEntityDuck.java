package petrolpark.mc.library.compat.create.core.world.block.entity;

import javax.annotation.Nullable;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import petrolpark.mc.library.compat.create.core.world.block.composite.CompositeKineticBlockEntity;

public interface IKineticBlockEntityDuck {

    @Nullable
    public static KineticBlockEntity getSource(KineticBlockEntity thisKBE) {
        final Level level = thisKBE.getLevel();
        if (level == null || !thisKBE.hasSource()) return null;
        final BlockEntity sourceBE = level.getBlockEntity(thisKBE.source);
        final Integer sourceIndex = ((IKineticBlockEntityDuck)thisKBE).getSourceIndex();
        return sourceIndex == null
            ? sourceBE instanceof KineticBlockEntity sourceKBE ? sourceKBE : null
            : sourceBE instanceof CompositeKineticBlockEntity sourceCKBE && sourceIndex >= 0 && sourceIndex < sourceCKBE.getParts().size() ? sourceCKBE.getParts().get(sourceIndex) : null;
    };

    @Nullable
    public Integer getSourceIndex();
    
    public void setSourceIndex(@Nullable Integer sourceIndex);
    
    /**
     * @return {@code true} if the speed of this KBE can be overridden, even if {@link KineticBlockEntity#hasSource there is a source}.
     * Behaviour is only defined in the case there is a source.
     */
    boolean isSourceOverridable();
};
