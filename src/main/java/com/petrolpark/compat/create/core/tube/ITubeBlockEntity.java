package com.petrolpark.compat.create.core.tube;

public interface ITubeBlockEntity {
    
    public void invalidateTubeRenderBoundingBox();

    /**
     * Called only on the controller in the Tube Block Entity pair.
     */
    public default void afterTubeConnect() {};

    /**
     * Called on both ends of the Tube, if they still exist.
     */
    public default void beforeTubeDisconnect() {};
};
