package petrolpark.mc.library.compat.create.core.world.block.multi;

import net.minecraft.core.Direction;

public interface IMultiSideBehaviour<M extends IMulti<? super M>> extends IMultiBehaviour<M> {
    
    /**
     * The face of the {@link IMulti} in which this {@link MultiBehaviour} is found.
     * This is not {@link MultiBehaviour#transform(com.simibubi.create.content.contraptions.StructureTransform) transformed} automatically.
     * @return Non-{@code null} face
     */
    public abstract Direction getMultiFace();
};
