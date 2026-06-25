package petrolpark.mc.library.compat.create.core.world.block.tube;

import net.minecraft.world.Clearable;

public interface ITubeBlockEntity extends Clearable {
    
    public void invalidateTubeRenderBoundingBox();

    public TubeBehaviour getTube();

    /**
     * Called only on the controller in the Tube Block Entity pair.
     */
    public default void afterTubeConnect() {};

    /**
     * Called on both ends of the Tube, if they still exist.
     */
    public default void beforeTubeDisconnect() {};

    @Override
    public default void clearContent() {
        getTube().disconnect((t, s) -> {});
    };
};
