package petrolpark.mc.library.compat.create.core.world.block.entity;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

public interface IOverridableKineticBlockEntity {

    public static boolean isSourceOverridable(KineticBlockEntity kbe) {
        return ((IKineticBlockEntityDuck)kbe).isSourceOverridable()
            || (kbe instanceof IOverridableKineticBlockEntity okbe && okbe.isSourceAlwaysOverridable());
    };
    
    boolean isSourceAlwaysOverridable();
};
